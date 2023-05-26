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

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ReflectionUtils;

import io.github.stream.core.Consumer;
import io.github.stream.core.StreamException;
import io.github.stream.core.annotation.Channel;
import io.github.stream.core.annotation.Sink;
import io.github.stream.core.channel.ChannelProcessor;
import io.github.stream.core.properties.CoreProperties;

/**
 * bean 装配之前
 * @author wendy512@yeah.net
 * @date 2023-05-22 15:13:40
 * @since 1.0.0
 */
public class StreamBeanPostProcessor implements BeanPostProcessor {

    private final ConfigurableApplicationContext context;

    private final CoreProperties properties;

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    private MaterializedConfiguration configuration;

    public StreamBeanPostProcessor(ConfigurableApplicationContext context, CoreProperties properties) {
        this.context = context;
        this.properties = properties;
        ConfigurationProvider configurationProvider = getConfigurationProvider();
        this.configuration = configurationProvider.getConfiguration();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        if (clazz.isInstance(ConfigurationProvider.class)) {
            return bean;
        }

        autoWriteChannelProcessor(bean, configuration);
        bindConsumerToSink(bean, configuration);
        return bean;
    }

    private void bindConsumerToSink(Object bean, MaterializedConfiguration configuration) {
        Class<?> clazz = bean.getClass();

        if (bean instanceof Consumer) {
            Sink sinkAnno = clazz.getAnnotation(Sink.class);
            if (null == sinkAnno) {
                throw new StreamException(clazz.getName() + " Consumer not bind sink");
            }

            String name = sinkAnno.value();
            List<io.github.stream.core.Sink> sinks = configuration.getSinks().get(name);
            if (null == sinks) {
                throw new StreamException(clazz.getName() + " Consumer not found " + name + " sink");
            }

            sinks.forEach(sink -> sink.addConsumer((Consumer) bean));
        }
    }

    private void autoWriteChannelProcessor(Object bean, MaterializedConfiguration configuration) {
        Field[] fields = bean.getClass().getDeclaredFields();

        Stream.of(fields).filter(f -> null != f.getAnnotation(Channel.class)).forEach(field -> {
            ReflectionUtils.makeAccessible(field);
            Channel annotation = field.getAnnotation(Channel.class);
            String value = annotation.value();
            ChannelProcessor channelProcessor = configuration.getChannelProcessors().get(value);
            try {
                field.set(bean, channelProcessor);
            } catch (IllegalAccessException e) {
                throw new StreamException(e);
            }
        });
    }

    public ConfigurationProvider getConfigurationProvider() {
        if (initialized.compareAndSet(false, true)) {
            BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(DefaultConfigurationProvider.class)
                    .addPropertyValue("coreProperties", properties)
                    .getRawBeanDefinition();
            BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) context.getBeanFactory();
            beanFactory.registerBeanDefinition(DefaultConfigurationProvider.class.getName(), beanDefinition);
        }

        return context.getBean(ConfigurationProvider.class);
    }

}
