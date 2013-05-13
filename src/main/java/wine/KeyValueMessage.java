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

import java.nio.ByteBuffer;

public abstract class KeyValueMessage extends AbstractMessage {
    private final ByteField key   = new ByteField();
    private final ByteField value = new ByteField();

    protected KeyValueMessage(byte messageType) {
        super(messageType);
    }

    protected KeyValueMessage(byte messageType, byte[] key, byte[] value) {
        this(messageType);

        key(key);
        value(value);
    }

    public void key(byte[] key) {
        this.key.set(key);
    }

    public byte[] key() {
        return this.key.get();
    }

    public void value(byte[] value) {
        this.value.set(value);
    }

    public byte[] value() {
        return this.value.get();
    }

    @Override
    public void parse(ByteBuffer buffer) {
        super.parse(buffer);

        key.lengthField().parse(buffer);
        value.lengthField().parse(buffer);

        key.parse(buffer);
        value.parse(buffer);
    }

    @Override
    public void format(ByteBuffer buffer) {
        super.format(buffer);

        key.lengthField().format(buffer);
        value.lengthField().format(buffer);

        key.format(buffer);
        value.format(buffer);
    }

    @Override
    public int length() {
        return super.length()
            + key.lengthField().length()
            + value.lengthField().length()
            + key.length()
            + value.length();
    }
}
