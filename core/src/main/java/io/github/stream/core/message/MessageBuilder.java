/**
 * Copyright wendy512@yeah.net
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.github.stream.core.message;

import java.util.HashMap;
import java.util.Map;

import io.github.stream.core.Message;

/**
 * message builder
 * @author wendy512@yeah.net
 * @date 2023-04-11 10:48:08
 * @since 1.0.0
 */
public final class MessageBuilder<T> {

    private final T payload;

    private final Map<String,Object> header;

    private MessageBuilder(T payload, Map<String, Object> header) {
        this.payload = payload;
        this.header = header;
    }

    public static <T> MessageBuilder<T> withPayload(T payload) {
        return new MessageBuilder<T>(payload, new HashMap<>());
    }

    public MessageBuilder<T> setHeader(String headerName, Object headerValue) {
        this.header.put(headerName, headerValue);
        return this;
    }

    public Message<T> build() {
        return new GenericMessage<T>(this.payload, new MessageHeaders(header));
    }
}
