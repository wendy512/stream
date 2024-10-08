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

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import io.github.stream.core.channel.ChannelContext;
import io.github.stream.core.configuration.ConfigurationProvider;
import io.github.stream.core.configuration.MaterializedConfiguration;
import io.github.stream.core.configuration.StreamBeanPostProcessor;
import io.github.stream.core.properties.CoreProperties;
import io.github.stream.core.utils.SpringUtil;

/**
 * springboot2 自动装配
 * @author wendy512@yeah.net
 * @date 2023-05-22 14:08:22
 * @since 1.0.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(CoreProperties.class)
@Configuration
@Import({StreamBeanPostProcessor.class, SpringUtil.class})
@ComponentScan("io.github.stream")
public class StreamAutoConfiguration {

    @Bean
    public ApplicationRunner runner(ConfigurationProvider configurationProvider) {
        MaterializedConfiguration configuration = configurationProvider.getConfiguration();

        StreamApplicationRunner runner = new StreamApplicationRunner();
        runner.setConfiguration(configuration);
        runner.start();
        return runner;
    }

    @Bean
    public ChannelContext channelContext(ConfigurationProvider configurationProvider) {
        return new ChannelContext(configurationProvider);
    }
}
