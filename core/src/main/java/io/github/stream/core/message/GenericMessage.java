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

import io.github.stream.core.Message;

import java.io.Serializable;
import java.util.Map;

/**
 * 消息默认实现
 * @author wendy512@yeah.net
 * @date 2023-04-11 10:48:08
 * @since 1.0.0
 */
public class GenericMessage<T> implements Message<T>, Serializable {

    private static final long serialVersionUID = -1047293681481507361L;

    private final T payload;

    private final MessageHeaders headers;

    public GenericMessage(T payload) {
        this(payload, new MessageHeaders(null));
    }

    public GenericMessage(T payload, Map<String, Object> headers) {
        this(payload, new MessageHeaders(headers));
    }

    public GenericMessage(T payload, MessageHeaders headers) {
        assert null != payload : "Payload must not be null";
        assert null != headers : "MessageHeaders must not be null";
        this.payload = payload;
        this.headers = headers;
    }

    public T getPayload() {
        return this.payload;
    }

    public MessageHeaders getHeaders() {
        return this.headers;
    }

}
