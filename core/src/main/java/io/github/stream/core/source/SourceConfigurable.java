package io.github.stream.core.source;

import io.github.stream.core.properties.SourceProperties;

/**
 * source配置接口
 * @author wendy512@yeah.net
 * @date 2023-05-23 15:05:11
 * @since 1.0.0
 */
public interface SourceConfigurable {
    void configure(SourceProperties properties);
}
