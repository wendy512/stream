package io.github.stream.pulsar;

import io.github.stream.core.Configurable;
import io.github.stream.core.properties.AbstractProperties;
import org.apache.pulsar.client.api.ClientBuilder;
import org.apache.pulsar.client.api.PulsarClient;

import java.io.IOException;
import java.util.Map;

/**
 * pulsar配置加载
 * @author jujiale
 * @date 2024/04
 */
public class PulsarStateConfigure implements Configurable {

    ClientBuilder builder;


    @Override
    public void configure(AbstractProperties properties) {
        builder = createPulsarClientBuilder(properties);
    }

    public PulsarClient newPulsarClient() throws IOException {
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private ClientBuilder createPulsarClientBuilder(AbstractProperties properties) {
        Map<String, Object> config = properties.getConfig();
        if (null == config) {
            throw new IllegalArgumentException("pulsar sink config cannot empty");
        }
        Map<String, Object> clientConfig = (Map<String, Object>) config.get("client");
        return PulsarClient.builder().loadConf(clientConfig);
    }
}
