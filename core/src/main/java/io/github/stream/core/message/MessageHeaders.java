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

package io.github.stream.core.message;

import org.apache.commons.collections4.MapUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * 消息header
 * @author wendy512@yeah.net
 * @date 2023-04-11 10:30:11
 * @since 1.0.0
 */
public class MessageHeaders implements Map<String, Object>, Serializable {

    public static final String TIMESTAMP = "timestamp";

    private static final long serialVersionUID = 7035068984263400920L;

    private final Map<String, Object> headers;

    public MessageHeaders(Map<String, Object> headers) {
        this(headers, null);
    }

    protected MessageHeaders(Map<String, Object> headers, Long timestamp) {
        this.headers = (headers != null ? new HashMap<>(headers) : new HashMap<>());

        if (timestamp == null) {
            this.headers.put(TIMESTAMP, System.currentTimeMillis());
        }
        else if (timestamp < 0) {
            this.headers.remove(TIMESTAMP);
        }
        else {
            this.headers.put(TIMESTAMP, timestamp);
        }
    }

    private MessageHeaders(MessageHeaders original, Set<String> keysToIgnore) {
        this.headers = new HashMap<>(original.headers.size());
        original.headers.forEach((key, value) -> {
            if (!keysToIgnore.contains(key)) {
                this.headers.put(key, value);
            }
        });
    }

    protected Map<String, Object> getRawHeaders() {
        return this.headers;
    }

    public Long getTimestamp() {
        return get(TIMESTAMP, Long.class);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Class<T> type) {
        Object value = this.headers.get(key);
        if (value == null) {
            return null;
        }
        if (!type.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Incorrect type specified for header '" +
                    key + "'. Expected [" + type + "] but actual type is [" + value.getClass() + "]");
        }
        return (T) value;
    }

    public String getString(String key) {
        return MapUtils.getString(headers, key);
    }

    public String getString(String key, String defaultValue) {
        return MapUtils.getString(headers, key, defaultValue);
    }

    public boolean getBooleanValue(String key) {
        return MapUtils.getBooleanValue(headers, key);
    }

    public boolean getBooleanValue(String key, boolean defaultValue) {
        return MapUtils.getBooleanValue(headers, key, defaultValue);
    }

    // Delegating Map implementation

    public boolean containsKey(Object key) {
        return this.headers.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return this.headers.containsValue(value);
    }

    public Set<Entry<String, Object>> entrySet() {
        return Collections.unmodifiableMap(this.headers).entrySet();
    }

    public Object get(Object key) {
        return this.headers.get(key);
    }

    public boolean isEmpty() {
        return this.headers.isEmpty();
    }

    public Set<String> keySet() {
        return Collections.unmodifiableSet(this.headers.keySet());
    }

    public int size() {
        return this.headers.size();
    }

    public Collection<Object> values() {
        return Collections.unmodifiableCollection(this.headers.values());
    }


    public Object put(String key, Object value) {
        throw new UnsupportedOperationException("MessageHeaders is immutable");
    }

    public void putAll(Map<? extends String, ? extends Object> map) {
        throw new UnsupportedOperationException("MessageHeaders is immutable");
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException("MessageHeaders is immutable");
    }

    public void clear() {
        throw new UnsupportedOperationException("MessageHeaders is immutable");
    }


    private void writeObject(ObjectOutputStream out) throws IOException {
        Set<String> keysToIgnore = new HashSet<>();
        this.headers.forEach((key, value) -> {
            if (!(value instanceof Serializable)) {
                keysToIgnore.add(key);
            }
        });

        if (keysToIgnore.isEmpty()) {
            out.defaultWriteObject();
        }
        else {
            out.writeObject(new MessageHeaders(this, keysToIgnore));
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }


    @Override
    public boolean equals(Object other) {
        return (this == other ||
                (other instanceof MessageHeaders && this.headers.equals(((MessageHeaders) other).headers)));
    }

    @Override
    public int hashCode() {
        return this.headers.hashCode();
    }

    @Override
    public String toString() {
        return this.headers.toString();
    }
}
