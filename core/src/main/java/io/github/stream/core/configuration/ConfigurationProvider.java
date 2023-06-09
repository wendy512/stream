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

package io.github.stream.core.configuration;

import io.github.stream.core.properties.CoreProperties;

/**
 * 加载stream配置统一接口
 * @author wendy512@yeah.net
 * @date 2023-05-19 15:11:30
 * @since 1.0.0
 */
public interface ConfigurationProvider {

    /**
     * 设置核心配置，必须在getConfiguration之前设置
     * @param coreProperties
     */
    void setCoreProperties(CoreProperties coreProperties);

    /**
     * 获取配置，第一次获取会先加载配置
     * @return
     */
    MaterializedConfiguration getConfiguration();
}
