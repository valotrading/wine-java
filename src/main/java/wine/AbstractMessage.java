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

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import silvertip.GarbledMessageException;
import silvertip.PartialMessageException;

abstract class AbstractMessage implements Message {
    protected interface Type {
        final byte LOGIN          = 'L';
        final byte LOGIN_ACCEPTED = 'A';
        final byte LOGIN_REJECTED = 'J';
        final byte GET            = 'G';
        final byte VALUE          = 'V';
        final byte SET            = 'S';
    }

    private byte messageType;

    protected AbstractMessage(byte messageType) {
        this.messageType = messageType;
    }

    public static AbstractMessage from(ByteBuffer buffer) throws GarbledMessageException, PartialMessageException {
        try {
            AbstractMessage message = create(buffer);

            message.parse(buffer);

            return message;
        } catch (BufferUnderflowException e) {
            throw new PartialMessageException();
        }
    }

    private static AbstractMessage create(ByteBuffer buffer) throws GarbledMessageException {
        byte messageType = buffer.get();

        switch (messageType) {
        case Type.LOGIN:
            return new Login();
        case Type.LOGIN_ACCEPTED:
            return new LoginAccepted();
        case Type.LOGIN_REJECTED:
            return new LoginRejected();
        case Type.GET:
            return new Get();
        case Type.VALUE:
            return new Value();
        case Type.SET:
            return new Set();
        }

        throw new GarbledMessageException(String.format("Unexpected message type %c", messageType));
    }

    @Override
    public void parse(ByteBuffer buffer) {
    }

    @Override
    public void format(ByteBuffer buffer) {
        buffer.put(messageType);
    }

    @Override
    public byte[] format() {
        byte[]     bytes  = new byte[length()];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        format(buffer);

        return bytes;
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public boolean equals(Object that) {
        return Objects.equals(this, that);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this);
    }

    @Override
    public String toString() {
        return Objects.toString(this);
    }

}
