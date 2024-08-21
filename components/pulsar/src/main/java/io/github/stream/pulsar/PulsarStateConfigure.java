package io.github.stream.pulsar;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.pulsar.client.api.ClientBuilder;
import org.apache.pulsar.client.api.PulsarClient;

import io.github.stream.core.Configurable;
import io.github.stream.core.configuration.ConfigContext;
import lombok.Getter;

/**
 * pulsar配置加载
 * @author jujiale
 * @date 2024/04
 */
@Getter
public final class PulsarStateConfigure implements Configurable {

    private static final Map<String, PulsarStateConfigure> instances = new ConcurrentHashMap<>();

    private PulsarClient client;

    private PulsarStateConfigure() {}

    /**
     * 获取客户端实例，保证同一实例名只创建一个客户端，节省资源
     * @param name 实例名称
     * @return 创建后的客户端实例
     */
    public static PulsarStateConfigure getInstance(String name) {
        PulsarStateConfigure instance = instances.get(name);
        if (instance != null) {
            return instance;
        }

        synchronized (PulsarStateConfigure.class) {
            // double check
            if (!instances.containsKey(name)) {
                instance = new PulsarStateConfigure();
                instances.put(name, instance);
            } else {
                instance = instances.get(name);
            }
        }
        return instance;
    }

    @Override
    public void configure(ConfigContext context) throws IOException {
        ClientBuilder builder = createPulsarClientBuilder(context);
        this.client = builder.build();
    }

    @SuppressWarnings("unchecked")
    private ClientBuilder createPulsarClientBuilder(ConfigContext context) {
        Map<String, Object> config = context.getInstance().getOriginal();
        if (null == config) {
            throw new IllegalArgumentException("pulsar sink config cannot empty");
        }
        Map<String, Object> clientConfig = context.getInstance().getOriginal();
        return PulsarClient.builder().loadConf(clientConfig);
    }
}
