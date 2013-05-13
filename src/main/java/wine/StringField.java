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
import java.nio.charset.Charset;
import java.util.Arrays;

class StringField extends AbstractField {
    private static final Charset CHARSET = Charset.forName("US-ASCII");

    private final int length;

    private String value = "";

    public StringField(int length) {
        this.length = length;
        this.value  = "";
    }

    public String get() {
        return value;
    }

    public void set(String value) {
        this.value = value;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public void format(ByteBuffer buffer) {
        byte[] valueBytes  = value.getBytes(CHARSET);

        buffer.put(valueBytes, 0, Math.min(valueBytes.length, length));

        if (valueBytes.length < length) {
            byte[] paddingBytes = new byte[length - valueBytes.length];

            Arrays.fill(paddingBytes, (byte) ' ');

            buffer.put(paddingBytes);
        }
    }

    @Override
    public void parse(ByteBuffer buffer) {
        byte[] bytes = new byte[length];

        buffer.get(bytes);

        value = new String(bytes, CHARSET).trim();
    }

    @Override
    public String toString() {
        return value;
    }
}
