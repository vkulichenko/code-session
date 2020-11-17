/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package code.session;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;

@SuppressWarnings("UnstableApiUsage")
public class Discovery {
    private static final String PREFIX = "nodes/";
    private static final ByteSequence PREFIX_BS = bs(PREFIX);

    private final Map<UUID, Integer> topology = new LinkedHashMap<>();

    public void join(UUID localId, Integer localPort, Runnable listener) throws Exception {
        Client client = Client.builder().endpoints("http://127.0.0.1:2379").build();

        // Get current topology.
        synchronized (topology) {
            for (KeyValue kv : client.getKVClient().get(PREFIX_BS, GetOption.newBuilder().withPrefix(PREFIX_BS).build()).get().getKvs()) {
                add(kv);
            }
        }

        // Create a watch.
        client.getWatchClient().watch(PREFIX_BS, WatchOption.newBuilder().withPrefix(PREFIX_BS).build(), res -> {
            synchronized (topology) {
                for (WatchEvent event : res.getEvents()) {
                    if (event.getEventType() == WatchEvent.EventType.PUT) {
                        add(event.getKeyValue());
                    }
                }

                if (listener != null)
                    listener.run();

                print();
            }
        });

        // Register this server node.
        if (localId != null && localPort != null) {
            client.getKVClient().put(PREFIX_BS.concat(bs(localId)), bs(localPort)).get();
        } else {
            print();
        }
    }

    public List<Node> getTopology() {
        synchronized (topology) {
            return topology.entrySet().stream()
                .map(e -> new Node(e.getKey(), new InetSocketAddress("127.0.0.1", e.getValue())))
                .collect(Collectors.toList());
        }
    }

    private void add(KeyValue kv) {
        UUID id = UUID.fromString(kv.getKey().toString(StandardCharsets.UTF_8).substring(PREFIX.length()));
        int port = Integer.parseInt(kv.getValue().toString(StandardCharsets.UTF_8));

        topology.put(id, port);
    }

    private void print() {
        System.out.println("Topology:");

        for (Map.Entry<UUID, Integer> entry : topology.entrySet()) {
            System.out.println("   " + entry.getKey() + " -> 127.0.0.1:" + entry.getValue());
        }
    }

    private static ByteSequence bs(Object o) {
        return ByteSequence.from(o.toString(), StandardCharsets.UTF_8);
    }

    @SuppressWarnings("PublicInnerClass")
    public static class Node {
        private final UUID id;

        private final InetSocketAddress address;

        public Node(UUID id, InetSocketAddress address) {
            this.id = id;
            this.address = address;
        }

        public UUID getId() {
            return id;
        }

        public InetSocketAddress getAddress() {
            return address;
        }
    }
}
