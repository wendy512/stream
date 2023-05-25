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

import io.github.stream.core.Sink;
import io.github.stream.core.lifecycle.AbstractLifecycleAware;
import io.github.stream.core.SinkProcessor;

import java.util.Collections;
import java.util.List;

/**
 * 抽象输出处理者
 * @author wendy512@yeah.net
 * @date 2023-05-19 10:04:48
 * @since 1.0.0
 */
public abstract class AbstractSinkProcessor<T> extends AbstractLifecycleAware implements SinkProcessor<T> {

    private List<Sink<T>> sinks = Collections.emptyList();

    @Override
    public void setSinks(List<Sink<T>> sinks) {
        this.sinks = sinks;
    }

    public List<Sink<T>> getSinks() {
        return sinks;
    }

    @Override
    public void start() {
        sinks.forEach(Sink::start);
        super.start();
    }

    @Override
    public void stop() {
        sinks.forEach(Sink::stop);
        super.stop();
    }
}
