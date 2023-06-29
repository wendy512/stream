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
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;

import io.github.stream.core.*;
import io.github.stream.core.channel.ChannelProcessor;
import io.github.stream.core.channel.ChannelType;
import io.github.stream.core.channel.LoadBalancingChannelSelector;
import io.github.stream.core.properties.ChannelProperties;
import io.github.stream.core.properties.CoreProperties;
import io.github.stream.core.properties.SinkProperties;
import io.github.stream.core.properties.SourceProperties;
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
                throw new StreamException("source " + name + "Not found  type " + properties.getType());
            }

            ChannelSelector channelSelector =
                new LoadBalancingChannelSelector(LoadBalancingChannelSelector.Policy.ROUND_ROBIN);
            String channel = properties.getChannel();
            if (StringUtils.isBlank(channel)) {
                throw new StreamException("source " + name + " channel cannot be empty ");
            }

            List<Channel> channels = configuration.getChannels().get(channel);
            if (null == channels) {
                throw new StreamException("source " + name + "Not found channel " + channel);
            }
            channelSelector.addChannel(channels);
            ChannelProcessor channelProcessor = new ChannelProcessor(channelSelector);
            configuration.addChannelProcessor(channel, channelProcessor);

            try {
                Class<?> clazz = Class.forName(sourceClassName.getClassName());
                Source source = (Source) clazz.newInstance();
                source.setChannelProcessor(channelProcessor);
                source.configure(properties);
                configuration.addSource(name, source);
            } catch (Exception e) {
                throw new StreamException(e);
            }
        }
    }

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
                throw new StreamException("sink " + name + " Not found type " + properties.getType());
            }

            String channelName = properties.getChannel();
            ChannelProperties channelProperties = channelMap.get(channelName);
            ComponentWithClassName channelClassName;

            try {
                channelClassName = ChannelType.valueOf(channelProperties.getType().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new StreamException("Not found channel type " + channelProperties.getType());
            }

            Constructor<?> channelConstructor;
            try {
                channelConstructor = Class.forName(channelClassName.getClassName()).getConstructor(int.class);
            } catch (Exception e) {
                throw new StreamException(e);
            }

            Constructor<?> sinkConstructor;
            try {
                Class<?> clazz = Class.forName(sinkClassName.getClassName());
                sinkConstructor = clazz.getConstructor();
            } catch (Exception e) {
                throw new StreamException(e);
            }

            Sink sink;
            try {
                sink = (Sink)sinkConstructor.newInstance();
            } catch (Exception e) {
                throw new StreamException(e);
            }
            sink.configure(properties);
            configuration.addSink(name, sink);

            for (int i = 1; i <= properties.getThreads(); i++) {
                SinkProcessor sinkProcessor = new DefaultSinkProcessor(properties.getCacheSize());

                try {
                    Channel channel = (Channel)channelConstructor.newInstance(channelProperties.getCapacity());
                    channel.configure(channelProperties);

                    sinkProcessor.setChannel(channel);
                    sinkProcessor.setSinks(Arrays.asList(sink));
                    configuration.addChannel(channelName, channel);
                } catch (Exception e) {
                    throw new StreamException(e);
                }

                SinkRunner sinkRunner = new SinkRunner(sinkProcessor, properties.getInterval(), "sink-runner-" + i);
                configuration.addSinkRunner(name, sinkRunner);
            }
        }
    }
}
