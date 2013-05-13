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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

abstract class Objects {

    public static boolean equals(Object a, Object b) {
        return EqualsBuilder.reflectionEquals(a, b);
    }

    public static int hashCode(Object o) {
        return HashCodeBuilder.reflectionHashCode(o);
    }

    public static String toString(Object o) {
        String name   = o.getClass().getSimpleName();
        String fields = ReflectionToStringBuilder.toString(o, ToStringStyle.SIMPLE_STYLE);

        return String.format("%s(%s)", name, fields);
    }

}
