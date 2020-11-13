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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import code.session.request.Request;
import java.net.ServerSocket;
import java.net.Socket;

public class Communication {
    private ServerSocket serverSocket;

    public int start() {
        int port = 4000;

        while (true) {
            try {
                serverSocket = new ServerSocket(port);

                System.out.println("accepting connections on: " + port);

                break;
            }
            catch (IOException e) {
                e.printStackTrace();
                port++;
            }
        }

        return port;
    }

    public void listen(Storage storage) throws Exception {
        while (true) {
            Socket socket = serverSocket.accept();

            new Thread(() -> {
                try {
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                    while (true) {
                        Request request = (Request)in.readObject();

                        Object result = request.handle(storage);

                        out.writeObject(result);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public <R> R execute(Request<R> request, InetSocketAddress address) throws Exception {
        try (Socket socket = new Socket(address.getAddress(), address.getPort())) {

            new ObjectOutputStream(socket.getOutputStream()).writeObject(request);

            R result = (R)new ObjectInputStream(socket.getInputStream()).readObject();

            return result;
        }
    }
}
