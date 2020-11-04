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
import code.session.request.GetRequest;
import code.session.request.PutRequest;

public class Client {
    private final Communication comm = new Communication();

    public void start() throws Exception {
    }

    public void put(String key, String value) throws Exception {
        comm.execute(new PutRequest(key, value), new InetSocketAddress("127.0.0.1", 4000));
    }

    public String get(String key) throws Exception {
        return comm.execute(new GetRequest(key), new InetSocketAddress("127.0.0.1", 4000));
    }
}
