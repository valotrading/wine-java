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

public class Login extends AbstractMessage implements StringMessage {
    private final StringField username = new StringField(8);
    private final StringField password = new StringField(20);

    public Login() {
        super(Type.LOGIN);
    }

    public Login(String username, String password) {
        this();
       
        username(username);
        password(password);
    }

    public void username(String username) {
        this.username.set(username);
    }

    public String username() {
        return this.username.get();
    }

    public void password(String password) {
        this.password.set(password);
    }

    public String password() {
        return this.password.get();
    }

    @Override
    public void accept(MessageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void parse(ByteBuffer buffer) {
        super.parse(buffer);

        username.parse(buffer);
        password.parse(buffer);
    }

    @Override
    public void format(ByteBuffer buffer) {
        super.format(buffer);

        username.format(buffer);
        password.format(buffer);
    }

    @Override
    public int length() {
        return super.length() + username.length() + password.length();
    }
}
