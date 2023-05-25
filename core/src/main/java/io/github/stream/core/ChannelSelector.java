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

package io.github.stream.core;

import java.util.List;

/**
 * channel 选择器
 *
 * @author wendy512@yeah.net
 * @date 2023-05-18 17:38:07
 * @since 1.0.0
 */
public interface ChannelSelector<T> {

    /**
     * 添加通道
     * @param channel
     */
    ChannelSelector<T> addChannel(Channel<T> channel);

    /**
     * 添加通道（多个）
     * @param channels
     */
    ChannelSelector<T> addChannel(List<Channel<T>> channels);

    /**
     * 获取channel，支持轮训和随机两种策略
     * @return
     */
    Channel<T> getChannel();
}
