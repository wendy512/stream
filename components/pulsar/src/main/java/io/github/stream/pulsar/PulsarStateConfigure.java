package io.github.stream.pulsar;

import java.io.IOException;
import java.util.Map;

import org.apache.pulsar.client.api.ClientBuilder;
import org.apache.pulsar.client.api.PulsarClient;

import io.github.stream.core.Configurable;
import io.github.stream.core.configuration.ConfigContext;

/**
 * pulsar配置加载
 * @author jujiale
 * @date 2024/04
 */
public class PulsarStateConfigure implements Configurable {

    ClientBuilder builder;


    @Override
    public void configure(ConfigContext context) {
        builder = createPulsarClientBuilder(context);
    }

    public PulsarClient newPulsarClient() throws IOException {
        return builder.build();
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
