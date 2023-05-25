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

package io.github.stream.core.sink;

import io.github.stream.core.Consumer;
import io.github.stream.core.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 默认实现
 * @author wendy512@yeah.net
 * @date 2023-05-19 10:25:56
 * @since 1.0.0
 */
@Slf4j
public class DefaultSink<T> extends AbstractSink<T> {

    public DefaultSink(int cacheSize) {
        super(cacheSize);
    }

    @Override
    public void startProcess(List<Message<T>> messages) {
        Set<Consumer<T>> consumers = getConsumers();
        for (Consumer<T> consumer : consumers) {
            try {
                consumer.accept(new ArrayList<>(messages));
            } catch (Exception ex) {
                log.error("Consumer " + consumer.getClass().getName() + " accept error", ex);
            }
        }
    }
}
