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

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import silvertip.GarbledMessageException;
import silvertip.PartialMessageException;

public class MessageTest {
    private static final Charset CHARSET = Charset.forName("US-ASCII");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void loginFormatting() throws Exception {
        Login  message  = new Login("foo", "bar");
        String expected = "Lfoo     bar                 ";

        assertEquals(expected, format(message));
    }

    @Test
    public void loginParsing() throws Exception {
        String message  = "Lfoo     bar                 ";
        Login  expected = new Login("foo", "bar");

        assertEquals(expected, parse(message));
    }

    @Test
    public void loginAcceptedFormatting() throws Exception {
        assertEquals("A", format(new LoginAccepted()));
    }

    @Test
    public void loginAcceptedParsing() throws Exception {
        assertEquals(new LoginAccepted(), parse("A"));
    }

    @Test
    public void loginRejectedFormatting() throws Exception {
        assertEquals("J", format(new LoginRejected()));
    }

    @Test
    public void loginRejectedParsing() throws Exception {
        assertEquals(new LoginRejected(), parse("J"));
    }

    @Test
    public void getFormattingWithEmptyKey() throws Exception {
        Get    message  = new Get();
        byte[] expected = new byte[] { 'G', 0, 0, 0, 0 };

        assertArrayEquals(expected, format(message));
    }

    @Test
    public void getFormattingWithNonEmptyKey() throws Exception {
        Get    message  = new Get(new byte[] { 'f', 'o', 'o' });
        byte[] expected = new byte[] { 'G', 0, 0, 0, 3, 'f', 'o', 'o' };

        assertArrayEquals(expected, format(message));
    }

    @Test
    public void getParsingWithEmptyKey() throws Exception {
        byte[] message  = new byte[] { 'G', 0, 0, 0, 0 };
        Get    expected = new Get();

        assertEquals(expected, parse(message));
    }

    @Test
    public void getParsingWithNonEmptyKey() throws Exception {
        byte[]  message  = new byte[] { 'G', 0, 0, 0, 3, 'f', 'o', 'o' };
        Message expected = new Get(new byte[] { 'f', 'o', 'o' });

        assertEquals(expected, parse(message));
    }

    @Test
    public void valueFormattingWithEmptyKeyAndEmptyValue() throws Exception {
        Value  message  = new Value();
        byte[] expected = new byte[] { 'V', 0, 0, 0, 0, 0, 0, 0, 0 };

        assertArrayEquals(expected, format(message));
    }

    @Test
    public void valueFormattingWithNonEmptyKeyAndNonEmptyValue() throws Exception {
        Value  message  = new Value(new byte[] { 'f', 'o', 'o' }, new byte[] { 'q', 'u', 'u', 'x' });
        byte[] expected = new byte[] { 'V', 0, 0, 0, 3, 0, 0, 0, 4, 'f', 'o', 'o', 'q', 'u', 'u', 'x' };

        assertArrayEquals(expected, format(message));
    }

    @Test
    public void valueParsingWithEmptyKeyAndEmptyValue() throws Exception {
        byte[]  message  = new byte[] { 'V', 0, 0, 0, 0, 0, 0, 0, 0 };
        Message expected = new Value();

        assertEquals(expected, parse(message));
    }

    @Test
    public void valueParsingWithNonEmptyValue() throws Exception {
        byte[]  message  = new byte[] { 'V', 0, 0, 0, 3, 0, 0, 0, 4, 'f', 'o', 'o', 'q', 'u', 'u', 'x' };
        Message expected = new Value(new byte[] { 'f', 'o', 'o' }, new byte[] { 'q', 'u', 'u', 'x' });

        assertEquals(expected, parse(message));
    }

    @Test
    public void setFormattingWithEmptyKeyAndEmptyValue() throws Exception {
        Set    message  = new Set();
        byte[] expected = new byte[] { 'S', 0, 0, 0, 0, 0, 0, 0, 0 };

        assertArrayEquals(expected, format(message));
    }

    @Test
    public void setFormattingWithNonEmptyKeyAndNonEmptyValue() throws Exception {
        Set    message  = new Set(new byte[] { 'f', 'o', 'o' }, new byte[] { 'q', 'u', 'u', 'x' });
        byte[] expected = new byte[] { 'S', 0, 0, 0, 3, 0, 0, 0, 4, 'f', 'o', 'o', 'q', 'u', 'u', 'x' };

        assertArrayEquals(expected, format(message));
    }

    @Test
    public void setParsingWithEmptyKeyAndEmptyValue() throws Exception {
        byte[]  message  = new byte[] { 'S', 0, 0, 0, 0, 0, 0, 0, 0 };
        Message expected = new Set();

        assertEquals(expected, parse(message));
    }

    @Test
    public void setParsingWithNonEmptyKeyAndNonEmptyValue() throws Exception {
        byte[]  message  = new byte[] { 'S', 0, 0, 0, 3, 0, 0, 0, 4, 'f', 'o', 'o', 'q', 'u', 'u', 'x' };
        Message expected = new Set(new byte[] { 'f', 'o', 'o' }, new byte[] { 'q', 'u', 'u', 'x' });

        assertEquals(expected, parse(message));
    }

    @Test
    public void partialMessageWithoutMessageType() throws Exception {
        thrown.expect(PartialMessageException.class);

        parse(new byte[] {});
    }

    @Test
    public void partialMessageWithoutLengthField() throws Exception {
        thrown.expect(PartialMessageException.class);

        parse(new byte[] { 'G' });
    }

    @Test
    public void partialMessageWithPartialLengthField() throws Exception {
        thrown.expect(PartialMessageException.class);

        parse(new byte[] { 'G', 0, 0 });
    }

    @Test
    public void unknownMessageType() throws Exception {
        thrown.expect(GarbledMessageException.class);
        thrown.expectMessage("Unexpected message type X");

        parse("X");
    }

    private byte[] format(Message message) throws Exception {
        byte[]     bytes  = new byte[message.length()];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        message.format(buffer);

        return bytes;
    }

    private String format(StringMessage message) throws Exception {
        byte[] bytes = format((Message) message);

        return new String(bytes, CHARSET);
    }

    private Message parse(String message) throws Exception {
        return parse(message.getBytes(CHARSET));
    }

    private Message parse(byte[] message) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(message);
        Parser     parser = new Parser();

        return parser.parse(buffer);
    }

}
