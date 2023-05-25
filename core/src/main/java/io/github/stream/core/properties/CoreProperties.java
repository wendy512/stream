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

package io.github.stream.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 核心配置
 *
 * @author wendy512@yeah.net
 * @date 2023-05-19 15:19:56
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = CoreProperties.PREFIX)
public class CoreProperties {

    public static final String PREFIX = "stream";

    private Map<String, ChannelProperties> channel;
    private Map<String, SourceProperties> source;
    private Map<String, SinkProperties> sink;
}
