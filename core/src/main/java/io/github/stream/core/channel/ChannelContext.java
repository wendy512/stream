package io.github.stream.core.channel;

import io.github.stream.core.configuration.ConfigurationProvider;

/**
 * 上下文对象
 * @author taowenwu
 * @date 2023-10-19 15:42:29
 * @since 1.0.0
 */
public class ChannelContext {

    private final ConfigurationProvider configurationProvider;

    public ChannelContext(ConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    public ChannelProcessor getChannelProcessor(String name) {
        return configurationProvider.getConfiguration().getChannelProcessors().get(name);
    }
}
