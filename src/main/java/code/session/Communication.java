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

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import code.session.request.Request;
import java.net.ServerSocket;
import java.net.Socket;

public class Communication {
    private ServerSocket serverSocket;

    // Used by Servers
    public int start() {
        int port = 4000;

        while (true) {
            try {
                serverSocket = new ServerSocket(port);

                System.out.println("Server process started on: " + port);
                break;
            }
            catch (IOException e) {
                e.printStackTrace();
                port++;
            }

        }

        return port;
    }

    // Used by Servers
    public void listen(Storage storage) throws Exception {
        while (true) {
            Socket socket = serverSocket.accept();

            new Thread(() -> {
                while (true) {
                    try {
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                        Request<?> request = (Request<?>)in.readObject();
                        Object result = request.handle(storage);

                        out.writeObject(result);
                    }
                    catch (EOFException e1) {
                        break;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }

                }
            }).start();
        }
    }

    // Used by Clients
    public <R> R execute(Request<R> request, InetSocketAddress address) throws Exception {
        Socket socket = new Socket(address.getAddress(), address.getPort());

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        out.writeObject(request);

        return (R)in.readObject();
    }
}
