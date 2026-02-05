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

import io.github.stream.core.lifecycle.LifecycleAware;

/**
 * 通道统一接口
 * @author wendy512@yeah.net
 * @date 2023-05-18 15:08:11
 * @since 1.0.0
 */
public interface Channel<T> extends LifecycleAware, Configurable {

    /**
     * 发送消息
     * @param message
     * @return
     */
    void put(Message<T> message);

    /**
     * 批量发送消息
     * @param messages
     * @return
     */
    void put(List<Message<T>> messages);
}
