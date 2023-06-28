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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import io.github.stream.core.Consumer;
import io.github.stream.core.Message;
import io.github.stream.core.Sink;
import io.github.stream.core.lifecycle.AbstractLifecycleAware;
import io.github.stream.core.properties.AbstractProperties;

/**
 * 抽象的
 * @author wendy512@yeah.net
 * @date 2023-05-19 10:24:06
 * @since 1.0.0
 */
public abstract class AbstractSink<T> extends AbstractLifecycleAware implements Sink<T> {

    private Set<Consumer<T>> consumers = new LinkedHashSet<>();

    public abstract void process(List<Message<T>> messages);

    @Override
    public void configure(AbstractProperties properties) {}

    @Override
    public void addConsumer(Consumer<T> consumer) {
        this.consumers.add(consumer);
    }

    @Override
    public Set<Consumer<T>> getConsumers() {
        return this.consumers;
    }
}
