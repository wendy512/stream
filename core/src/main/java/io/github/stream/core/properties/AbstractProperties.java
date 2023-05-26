package io.github.stream.core.properties;

import lombok.Data;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;

/**
 * 配置抽象
 * @author wendy512@yeah.net
 * @date 2023-05-23 14:14:04
 * @since 1.0.0
 */
@Data
public class AbstractProperties {
    private Map config;

    public Object get(String key) {
        return config.get(key);
    }

    public String getString(String key) {
        return MapUtils.getString(config, key);
    }

    public String getString(String key, String defaultValue) {
        return MapUtils.getString(config, key, defaultValue);
    }

    public boolean getBooleanValue(String key) {
        return MapUtils.getBooleanValue(config, key);
    }

    public boolean getBooleanValue(String key, Boolean defaultValue) {
        return MapUtils.getBooleanValue(config, key, defaultValue);
    }

    public int getInt(String key) {
        return MapUtils.getIntValue(config, key);
    }

    public int getInt(String key, int defaultValue) {
        return MapUtils.getIntValue(config, key, defaultValue);
    }

    public Integer getInteger(String key) {
        return MapUtils.getInteger(config, key);
    }

    public Integer getInteger(String key, int defaultValue) {
        return MapUtils.getInteger(config, key, defaultValue);
    }

}
