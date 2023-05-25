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

package io.github.stream.core.interceptor;

import io.github.stream.core.Message;

/**
 * 消息拦截器，在放到channel之前执行
 * @author wendy512@yeah.net
 * @date 2023-05-18 16:37:21
 * @since 1.0.0
 */
public interface Interceptor<T> {
    Message<T> intercept(Message<T> message);
}
