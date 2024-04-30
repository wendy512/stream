package io.github.stream.core.configuration;

import io.github.stream.core.properties.BaseProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 配置上下文
 *
 * @author wendy512@yeah.net
 * @date 2024-04-29 17:40:23
 * @since 1.0.0
 */
@AllArgsConstructor
@Data
public class ConfigContext {
    /**
     * source或sink的config配置
     */
    private BaseProperties config;
    /**
     * 客户端实例配置
     */
    private BaseProperties instance;
}
