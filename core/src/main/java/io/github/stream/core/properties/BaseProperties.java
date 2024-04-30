package io.github.stream.core.properties;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 配置抽象
 * @author wendy512@yeah.net
 * @date 2023-05-23 14:14:04
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseProperties {
    private Map original;

    public Object get(String key) {
        return original.get(key);
    }

    public String getString(String key) {
        return MapUtils.getString(original, key);
    }

    public String getString(String key, String defaultValue) {
        return MapUtils.getString(original, key, defaultValue);
    }

    public boolean getBooleanValue(String key) {
        return MapUtils.getBooleanValue(original, key);
    }

    public boolean getBooleanValue(String key, boolean defaultValue) {
        return MapUtils.getBooleanValue(original, key, defaultValue);
    }

    public int getInt(String key) {
        return MapUtils.getIntValue(original, key);
    }

    public int getInt(String key, int defaultValue) {
        return MapUtils.getIntValue(original, key, defaultValue);
    }

    public Integer getInteger(String key) {
        return MapUtils.getInteger(original, key);
    }

    public Integer getInteger(String key, Integer defaultValue) {
        return MapUtils.getInteger(original, key, defaultValue);
    }

    public String removeString(String key) {
        String value = getString(key);
        original.remove(key);
        return value;
    }

    public int removeInt(String key, int defaultValue) {
        int value = getInt(key, defaultValue);
        original.remove(key);
        return value;
    }

    public BaseProperties getProperties(String key) {
        Map childConfig = MapUtils.getMap(this.original, key);
        if (null == childConfig) {
            return null;
        }
        BaseProperties properties = new BaseProperties();
        properties.setOriginal(childConfig);
        return properties;
    }
}
