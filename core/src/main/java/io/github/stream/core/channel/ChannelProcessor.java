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

package io.github.stream.core.channel;

import io.github.stream.core.ChannelSelector;
import io.github.stream.core.Message;

import java.util.List;

/**
 * channel 处理者
 * @author wendy512@yeah.net
 * @date 2023-05-19 10:34:55
 * @since 1.0.0
 */
public class ChannelProcessor<T> {

    private final ChannelSelector<T> selector;

    public ChannelProcessor(ChannelSelector<T> selector) {
        this.selector = selector;
    }

    public void send(Message<T> message) {
        selector.getChannel().put(message);
    }

    public void send(List<Message<T>> messages) {
        messages.forEach(selector.getChannel()::put);
    }
}
