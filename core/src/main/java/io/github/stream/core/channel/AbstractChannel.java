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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.lmax.disruptor.dsl.Disruptor;

import io.github.stream.core.Channel;
import io.github.stream.core.Message;
import io.github.stream.core.configuration.ConfigContext;
import io.github.stream.core.interceptor.InterceptableChannel;
import io.github.stream.core.interceptor.Interceptor;
import io.github.stream.core.lifecycle.AbstractLifecycleAware;
import io.github.stream.core.lifecycle.LifecycleState;

/**
 * 提取公共方法抽象类
 * @author wendy512@yeah.net
 * @date 2023-05-18 16:35:16
 * @since 1.0.0
 */
public abstract class AbstractChannel<T> extends AbstractLifecycleAware implements Channel<T>, InterceptableChannel<T> {

    private final List<Interceptor<T>> interceptors = new CopyOnWriteArrayList<>();

    protected final int capacity;

    protected final Disruptor<Message<T>> disruptor;

    public AbstractChannel(int capacity, Disruptor<Message<T>> disruptor) {
        this.capacity = capacity;
        this.disruptor = disruptor;
    }

    @Override
    public void put(Message<T> message) {
        if (getLifecycleState() == LifecycleState.STOP) {
            throw new IllegalStateException("channel is closed");
        }
        Message<T> messageToUse = new InterceptorChain().applyPrePut(message);
        doPut(messageToUse);
    }

    @Override
    public void configure(ConfigContext context) {}

    protected abstract void doPut(Message<T> message);

    @Override
    public void addInterceptors(List<Interceptor<T>> interceptors) {
        this.interceptors.clear();
        this.interceptors.addAll(interceptors);
    }

    @Override
    public void addInterceptor(Interceptor<T> interceptor) {
        this.interceptors.add(interceptor);
    }

    @Override
    public void addInterceptor(int index, Interceptor<T> interceptor) {
        this.interceptors.add(index, interceptor);
    }

    @Override
    public List<Interceptor<T>> getInterceptors() {
        return Collections.unmodifiableList(this.interceptors);
    }

    @Override
    public boolean removeInterceptor(Interceptor<T> interceptor) {
        return this.interceptors.remove(interceptor);
    }

    @Override
    public Interceptor<T> removeInterceptor(int index) {
        return this.interceptors.remove(index);
    }

    protected class InterceptorChain {

        public Message<T> applyPrePut(Message<T> message) {
            Message<T> messageToUse = message;
            for (Interceptor<T> interceptor : interceptors) {
                Message<T> resolvedMessage = interceptor.intercept(messageToUse);
                if (null == resolvedMessage) {
                    return null;
                }

                messageToUse = resolvedMessage;
            }
            return messageToUse;
        }
    }
}
