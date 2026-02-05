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

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.lmax.disruptor.LiteBlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.Util;

import io.github.stream.core.*;
import io.github.stream.core.channel.ChannelProcessor;
import io.github.stream.core.channel.ChannelType;
import io.github.stream.core.message.GenericMessage;
import io.github.stream.core.properties.*;
import io.github.stream.core.sink.DefaultSinkProcessor;
import io.github.stream.core.sink.SinkType;
import io.github.stream.core.source.SourceType;

/**
 * 加载stream配置默认实现
 * @author wendy512@yeah.net
 * @date 2023-05-19 17:17:58
 * @since 1.0.0
 */
public class DefaultConfigurationProvider implements ConfigurationProvider {

    private MaterializedConfiguration configuration;

    private CoreProperties coreProperties;

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    @Override
    public void setCoreProperties(CoreProperties coreProperties) {
        this.coreProperties = coreProperties;
    }

    @Override
    public MaterializedConfiguration getConfiguration() {
        if (null == coreProperties) {
            throw new IllegalArgumentException("coreProperties is null");
        }

        if (initialized.compareAndSet(false, true)) {
            this.configuration = doInitialize(coreProperties);
        }
        return configuration;
    }

    private MaterializedConfiguration doInitialize(CoreProperties coreProperties) {
        MaterializedConfiguration configuration = new SimpleMaterializedConfiguration();
        loadSinkAndChannel(configuration, coreProperties);
        loadSource(coreProperties, configuration);
        return configuration;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void loadSource(CoreProperties coreProperties, MaterializedConfiguration configuration) {
        Map<String, SourceProperties> sourceMap = coreProperties.getSource();
        if (null == sourceMap) {
            return;
        }

        for (Map.Entry<String, SourceProperties> entry : sourceMap.entrySet()) {
            String name = entry.getKey();
            SourceProperties properties = entry.getValue();

            ComponentWithClassName sourceClassName;
            try {
                sourceClassName = SourceType.valueOf(properties.getType().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new StreamException("Source '" + name + "' type value no match");
            }

            String channelName = properties.getChannel();
            if (StringUtils.isBlank(channelName)) {
                throw new StreamException("Source '" + name + "' channelName cannot be empty ");
            }

            Channel channel = configuration.getChannels().get(channelName);
            if (null == channel) {
                throw new StreamException("Source '" + name + "' not found channelName " + channelName);
            }
            ChannelProcessor channelProcessor = new ChannelProcessor(channel);
            configuration.addChannelProcessor(channelName, channelProcessor);
            // 获取instance配置
            BaseProperties instanceProperties = getInstanceProperties(coreProperties, properties.getInstance());

            try {
                Class<?> clazz = Class.forName(sourceClassName.getClassName());
                Source source = (Source) clazz.newInstance();
                source.setChannelProcessor(channelProcessor);
                source.configure(new ConfigContext(new BaseProperties(properties.getConfig()), properties.getInstance(),
                    instanceProperties));
                configuration.addSource(name, source);
            } catch (Exception e) {
                throw new StreamException(e);
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void loadSinkAndChannel(MaterializedConfiguration configuration, CoreProperties coreProperties) {
        Map<String, ChannelProperties> channelMap = coreProperties.getChannel();
        Map<String, SinkProperties> sinkMap = coreProperties.getSink();
        if (null == channelMap || null == sinkMap) {
            return;
        }

        for (Map.Entry<String, SinkProperties> entry : sinkMap.entrySet()) {
            String name = entry.getKey();
            SinkProperties properties = entry.getValue();

            ComponentWithClassName sinkClassName;
            try {
                sinkClassName = SinkType.valueOf(properties.getType().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new StreamException("Sink '" + name + "' type value no match");
            }

            String channelName = properties.getChannel();
            ChannelProperties channelProperties = channelMap.get(channelName);
            ComponentWithClassName channelClassName;

            try {
                channelClassName = ChannelType.valueOf(channelProperties.getType().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new StreamException("Not found channel type '" + channelProperties.getType() + "'");
            }

            Sink sink;
            BaseProperties sinkInstanceProperties = getInstanceProperties(coreProperties, properties.getInstance());
            try {
                Class<?> clazz = Class.forName(sinkClassName.getClassName());
                Constructor<?> sinkConstructor = clazz.getConstructor();
                sink = (Sink)sinkConstructor.newInstance();
                sink.configure(new ConfigContext(new BaseProperties(properties.getConfig()), properties.getInstance(),
                    sinkInstanceProperties));
            } catch (Exception e) {
                throw new StreamException(e);
            }
            configuration.addSink(name, sink);

            int bufferSize = Util.ceilingNextPowerOfTwo(properties.getCacheSize());
            ThreadFactory namedThreadFactory = new ThreadFactory() {
                private final AtomicInteger counter = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    // 在这里设置线程名称
                    return new Thread(r, "sink-" + name + "-runner-" + counter.getAndIncrement());
                }
            };

            Disruptor<Message> disruptor = new Disruptor<>(GenericMessage::new, bufferSize, namedThreadFactory,
                ProducerType.MULTI, new LiteBlockingWaitStrategy());
            SinkProcessor[] processors = new SinkProcessor[properties.getThreads()];

            // 创建channel
            try {
                Constructor<?> channelConstructor = Class.forName(channelClassName.getClassName()).getConstructor(int.class, Disruptor.class);
                Channel channel = (Channel)channelConstructor.newInstance(channelProperties.getCapacity(), disruptor);
                configuration.addChannel(channelName, channel);
            } catch (Exception e) {
                throw new StreamException(e);
            }

            for (int i = 0; i < properties.getThreads(); i++) {
                SinkProcessor sinkProcessor = new DefaultSinkProcessor(properties.getCacheSize(), i, properties.getThreads());
                processors[i] = sinkProcessor;

                sinkProcessor.setSinks(Arrays.asList(sink));
                SinkRunner sinkRunner = new SinkRunner(sinkProcessor);
                configuration.addSinkRunner(name, sinkRunner);
            }

            disruptor.handleEventsWith(processors);
            configuration.addDisruptor(disruptor);
        }
    }

    private static BaseProperties getInstanceProperties(CoreProperties coreProperties, String instanceName) {
        // 获取instance配置
        Map<String, Object> instanceConfig =
                coreProperties.getInstance().getOrDefault(instanceName, Collections.emptyMap());
        return new BaseProperties(instanceConfig);
    }
}
