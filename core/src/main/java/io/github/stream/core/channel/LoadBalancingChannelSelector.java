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
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.stream.core.Channel;
import io.github.stream.core.ChannelSelector;

/**
 * channel选择器，支持轮训和随机两种策略
 * @author wendy512@yeah.net
 * @date 2023-05-18 17:39:40
 * @since 1.0.0
 */
public class LoadBalancingChannelSelector<T> implements ChannelSelector<T> {

    private final ChannelPicker<T> picker;

    private List<Channel<T>> channels = new CopyOnWriteArrayList<>();

    public LoadBalancingChannelSelector(Policy policy) {
        try {
            picker = policy.getPolicyClass().newInstance();
            picker.setChannels(channels);
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IllegalArgumentException("Cannot instantiate policy class from policy enum "
                    + policy, ex);
        }
    }

    @Override
    public ChannelSelector addChannel(Channel<T> channel) {
        this.channels.add(channel);
        return this;
    }

    @Override
    public ChannelSelector addChannel(List<Channel<T>> channels) {
        if (null != channels) {
            this.channels.addAll(channels);
        }
        return this;
    }

    @Override
    public Channel<T> getChannel() {
        return picker.getChannel();
    }

    /**
     * Definitions for the various policy types
     */
    public enum Policy {
        ROUND_ROBIN(RoundRobinPolicy.class),
        RANDOM(RandomPolicy.class);

        private final Class<? extends ChannelPicker> clazz;

        Policy(Class<? extends ChannelPicker> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends ChannelPicker> getPolicyClass() {
            return clazz;
        }
    }

    private interface ChannelPicker<T> {
        Channel<T> getChannel();

        void setChannels(List<Channel<T>> channels);
    }

    /**
     * Selects channels in a round-robin fashion
     */
    private static class RoundRobinPolicy<T> implements ChannelPicker<T> {

        public RoundRobinPolicy() {}

        private final AtomicInteger next = new AtomicInteger(0);

        private List<Channel<T>> channels;

        @Override
        public Channel<T> getChannel() {
            return channels.get(next.getAndAccumulate(channels.size(), (x, y) -> ++x < y ? x : 0));
        }

        @Override
        public void setChannels(List<Channel<T>> channels) {
            this.channels = channels;
        }
    }

    /**
     * Selects a channel at random
     */
    private static class RandomPolicy<T> implements ChannelPicker<T> {

        public RandomPolicy() {}

        private final Random random = new Random(System.currentTimeMillis());

        private List<Channel<T>> channels;

        @Override
        public Channel<T> getChannel() {
            int size = channels.size();
            int pick = random.nextInt(size);
            return channels.get(pick);
        }

        @Override
        public void setChannels(List<Channel<T>> channels) {
            this.channels = channels;
        }

    }

}
