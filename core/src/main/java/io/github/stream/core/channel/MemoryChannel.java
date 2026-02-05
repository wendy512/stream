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

import java.util.List;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import io.github.stream.core.Message;

/**
 * 基于本地Queue的channel
 * @author wendy512@yeah.net
 * @date 2023-05-18 16:32:18
 * @since 1.0.0
 */
public class MemoryChannel<T> extends AbstractChannel<T> {

    public MemoryChannel(int capacity, Disruptor<Message<T>> disruptor) {
        super(capacity, disruptor);
    }

    @Override
    protected void doPut(Message<T> message) {
        RingBuffer<Message<T>> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();
        Message<T> eventMessage = ringBuffer.get(sequence);
        eventMessage.setHeaders(message.getHeaders());
        eventMessage.setPayload(message.getPayload());
        ringBuffer.publish(sequence);
    }

    @Override
    public void put(List<Message<T>> messages) {
        messages.forEach(this::put);
    }
}
