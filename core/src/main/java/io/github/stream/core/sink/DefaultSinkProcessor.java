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

import java.util.ArrayList;
import java.util.List;

import io.github.stream.core.Message;
import io.github.stream.core.Sink;

/**
 * 默认实现
 * @author wendy512@yeah.net
 * @date 2023-05-19 10:21:42
 * @since 1.0.0
 */
public class DefaultSinkProcessor<T> extends AbstractSinkProcessor<T> {

    private final List<Message<T>> caches;

    public DefaultSinkProcessor(int cacheSize, long ordinal, long numberOfConsumers) {
        super(cacheSize, ordinal, numberOfConsumers);
        this.caches = new ArrayList<>(cacheSize);
    }

    @Override
    public int process() {
        if (!caches.isEmpty()) {
            List<Sink<T>> sinks = getSinks();
            for (Sink<T> sink : sinks) {
                sink.handle(caches);
            }
        }
        return caches.size();
    }

    @Override
    public void onEvent(Message<T> event, long sequence, boolean endOfBatch) throws Exception {
        // 【核心逻辑】只处理属于自己的序列号
        if (sequence % numberOfConsumers == ordinal) {
            caches.add(event);
        }

        if (caches.size() >= cacheSize || endOfBatch) {
            process();
            caches.clear();
        }
    }
}
