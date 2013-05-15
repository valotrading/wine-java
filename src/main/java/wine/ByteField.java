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
import java.util.Arrays;

class ByteField extends AbstractField {
    private final LengthField lengthField = new LengthField(this);

    private byte[] value = new byte[0];

    @Override
    public int length() {
        return value.length;
    }

    public byte[] get() {
        return this.value;
    }

    public void set(byte[] value) {
        this.value = value;
    }

    @Override
    public void format(ByteBuffer buffer) {
        buffer.put(value);
    }

    @Override
    public void parse(ByteBuffer buffer) {
        buffer.get(value);
    }

    public Field lengthField() {
        return lengthField;
    }

    @Override
    public boolean equals(Object that) {
        if (that == null)
            return false;

        if (that == this)
            return true;

        if (that.getClass() != this.getClass())
            return false;

        byte[] thatValue = ((ByteField) that).value;

        return Arrays.equals(thatValue, this.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public String toString() {
        return Arrays.toString(value);
    }

    static class LengthField extends AbstractField {
        private ByteField parent;

        private LengthField(ByteField parent) {
            this.parent = parent;
        }

        @Override
        public int length() {
            return 4;
        }

        @Override
        public void format(ByteBuffer buffer) {
            buffer.putInt(parent.value.length);
        }

        @Override
        public void parse(ByteBuffer buffer) {
            parent.value = new byte[buffer.getInt()];
        }
    }
}
