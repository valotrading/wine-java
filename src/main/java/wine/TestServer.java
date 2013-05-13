/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wine;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import silvertip.Connection;
import silvertip.Events;
import silvertip.Server;

public class TestServer {

    public static void main(String[] args) throws IOException {
        if (args.length != 1)
            usage();

        int port = port(args);
        if (port == 0)
            usage();

        Map<List<Byte>, List<Byte>> config = new HashMap<List<Byte>, List<Byte>>();

        final Callback callback = new Callback(config);

        final Parser parser = new Parser();
        final Server server = Server.accept(port, new Server.ConnectionFactory<Message>() {
            @Override
            public Connection<Message> newConnection(SocketChannel channel) {
                return new Connection(channel, parser, callback);
            }
        });

        info(String.format("Listening on port %s", port));

        Events io = Events.open();

        io.register(server);

        while (true)
            io.process(1000);
    }

    private static class Callback implements Connection.Callback<Message> {
        private Map<List<Byte>, List<Byte>> config;

        public Callback(Map<List<Byte>, List<Byte>> config) {
            this.config = config;
        }

        @Override
        public void connected(Connection<Message> connection) {
        }

        @Override
        public void messages(Connection<Message> connection, Iterator<Message> messages) {
            while (messages.hasNext())
                handle(connection, messages.next());
        }

        @Override
        public void closed(Connection<Message> connection) {
        }

        @Override
        public void garbledMessage(Connection<Message> connection, String message, byte[] data) {
        }

        @Override
        public void sent(ByteBuffer buffer) {
        }

        private void handle(final Connection<Message> connection, Message message) {
            message.accept(new MessageVisitor() {
                @Override
                public void visit(Login message) {
                    send(connection, new LoginAccepted());
                }

                @Override
                public void visit(LoginAccepted message) {
                }

                @Override
                public void visit(LoginRejected message) {
                }

                @Override
                public void visit(Get message) {
                    List<Byte> key   = asList(message.key());
                    List<Byte> value = config.get(key);

                    if (value == null)
                        value = Collections.emptyList();

                    send(connection, new Value(message.key(), toArray(value)));
                }

                @Override
                public void visit(Value message) {
                }

                @Override
                public void visit(Set message) {
                    List<Byte> key   = asList(message.key());
                    List<Byte> value = asList(message.value());

                    config.put(key, value);
                }

                private List<Byte> asList(byte[] bytes) {
                    return Arrays.asList(ArrayUtils.toObject(bytes));
                }

                private byte[] toArray(List<Byte> bytes) {
                    return ArrayUtils.toPrimitive(bytes.toArray(new Byte[0]));
                }
            });
        }

        private void send(Connection<Message> connection, Message message) {
            byte[]     bytes  = new byte[message.length()];
            ByteBuffer buffer = ByteBuffer.wrap(bytes);

            message.format(buffer);
            buffer.flip();

            connection.send(buffer);
        }
    }

    private static int port(String[] args) {
        try {
            return Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static void info(String message) {
        System.out.println(String.format("wine-server: info: %s", message));
    }

    private static void usage() {
        System.err.println("Usage: test-server <port>");
        System.exit(2);
    }

}
