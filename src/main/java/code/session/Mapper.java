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

import java.util.List;

public class Mapper {
    private static final int PARTITIONS = 10;

    private Discovery discovery;

    public Mapper(Discovery discovery) {
        this.discovery = discovery;
    }

    public Discovery.Node node(String key) {
        return node(partition(key));
    }

    public int partition(String key) {
        return Math.abs(key.hashCode()) % PARTITIONS;
    }

    public Discovery.Node node(int partition) {
        List<Discovery.Node> topology = discovery.getTopology();

        int idx = partition % topology.size();

        return topology.get(idx);
    }

    public Discovery.Node[] partitionMapping() {
        Discovery.Node[] mapping = new Discovery.Node[PARTITIONS];

        for (int partition = 0; partition < PARTITIONS; partition++) {
            mapping[partition] = node(partition);
        }

        return mapping;
    }
}
