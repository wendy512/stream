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

import java.util.List;

/**
 * channel拦截器
 * @author wendy512@yeah.net
 * @date 2022-07-28 10:34:10:34
 * @since 1.0.0
 */
public interface InterceptableChannel<T> {

    /**
     * 添加拦截器（批量）
     * @param interceptors
     */
    void addInterceptors(List<Interceptor<T>> interceptors);

    /**
     * 添加拦截器
     * @param interceptor
     */
    void addInterceptor(Interceptor<T> interceptor);

    /**
     * 指定位置添加拦截器
     * @param index
     * @param interceptor
     */
    void addInterceptor(int index, Interceptor<T> interceptor);

    /**
     * 获取拦截器
     * @return
     */
    List<Interceptor<T>> getInterceptors();

    /**
     * 移除拦截器
     * @param interceptor
     * @return
     */
    boolean removeInterceptor(Interceptor<T> interceptor);

    /**
     * 指定index移除拦截器
     * @param index
     * @return
     */
    Interceptor<T> removeInterceptor(int index);
}
