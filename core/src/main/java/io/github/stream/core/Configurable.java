package io.github.stream.core;

import io.github.stream.core.configuration.ConfigContext;

/**
 * 配置接口
 * @author wendy512@yeah.net
 * @date 2023-05-23 15:10:27
 * @since 1.0.0
 */
public interface Configurable {

    /**
     * 统一配置入口
     * @param context
     */
    void configure(ConfigContext context) throws Exception;
}
