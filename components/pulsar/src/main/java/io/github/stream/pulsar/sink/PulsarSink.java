package io.github.stream.pulsar.sink;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.client.api.*;
import org.apache.pulsar.shade.org.apache.avro.data.Json;
import org.springframework.util.Assert;

import io.github.stream.core.Message;
import io.github.stream.core.StreamException;
import io.github.stream.core.configuration.ConfigContext;
import io.github.stream.core.properties.BaseProperties;
import io.github.stream.core.sink.AbstractSink;
import io.github.stream.pulsar.PulsarStateConfigure;
import lombok.extern.slf4j.Slf4j;

/**
 * pulsar-sink
 * @author jujiale
 * @date 2024/04
 */
@Slf4j
public class PulsarSink extends AbstractSink<Object> {

    private PulsarStateConfigure pulsarStateConfigure;
    private Producer<byte[]> pulsarProducer;
    private PulsarClient pulsarClient;

    @Override
    public void configure(ConfigContext context) throws Exception {
        this.pulsarStateConfigure = PulsarStateConfigure.getInstance(context.getInstanceName());
        this.pulsarStateConfigure.configure(context);
        this.pulsarClient = pulsarStateConfigure.getClient();
        this.initPulsarProducer(context.getConfig());
    }

    @SuppressWarnings("unchecked")
    private void initPulsarProducer(BaseProperties properties) throws PulsarClientException {
        Map<String, Object> producerConfig = properties.getOriginal();
        Assert.notNull(producerConfig, "pulsar sink producer config cannot empty");
        Map<String, Object> loadProducerConfig = initProducerLoadConfig(producerConfig);
        this.pulsarProducer = pulsarClient.newProducer().loadConf(loadProducerConfig).create();
    }

    private Map<String, Object> initProducerLoadConfig(Map<String, Object> config) {
        Map<String, Object> producerConfig = new HashMap<>(config);

        // add messageRoutingMode
        String messageRoutingMode = (String) config.get("messageRoutingMode");
        if (StringUtils.isNotBlank(messageRoutingMode)) {
            String lowerCase = messageRoutingMode.toLowerCase();
            switch (lowerCase) {
                case "roundrobinpartition":
                    producerConfig.put("messageRoutingMode", MessageRoutingMode.RoundRobinPartition);
                    break;
                case "custompartition":
                    producerConfig.put("messageRoutingMode", MessageRoutingMode.CustomPartition);
                    break;
                case "singlepartition":
                    producerConfig.put("messageRoutingMode", MessageRoutingMode.SinglePartition);
                    break;
            }
        }

        // add hashingScheme
        String hashingScheme = (String) config.get("hashingScheme");
        if (StringUtils.isNotBlank(hashingScheme)) {
            String lowerCase = hashingScheme.toLowerCase();
            switch (lowerCase) {
                case "javastringhash":
                    producerConfig.put("hashingScheme", HashingScheme.JavaStringHash);
                    break;
                case "murmur3_32hash":
                    producerConfig.put("messageRoutingMode", HashingScheme.Murmur3_32Hash);
                    break;
            }
        }

        // add cryptoFailureAction
        String cryptoFailureAction = (String) config.get("cryptoFailureAction");
        if (StringUtils.isNotBlank(cryptoFailureAction)) {
            String lowerCase = cryptoFailureAction.toLowerCase();
            switch (lowerCase) {
                case "fail":
                    producerConfig.put("cryptoFailureAction", ProducerCryptoFailureAction.FAIL);
                    break;
                case "send":
                    producerConfig.put("cryptoFailureAction", ProducerCryptoFailureAction.SEND);
                    break;
            }
        }

        // add compressionType
        String compressionType = (String) config.get("compressionType");
        if (StringUtils.isNotBlank(compressionType)) {
            String lowerCase = compressionType.toLowerCase();
            switch (lowerCase) {
                case "lz4":
                    producerConfig.put("compressionType", CompressionType.LZ4);
                    break;
                case "zlib":
                    producerConfig.put("compressionType", CompressionType.ZLIB);
                    break;
                case "zstd":
                    producerConfig.put("compressionType", CompressionType.ZSTD);
                    break;
                case "snappy":
                    producerConfig.put("compressionType", CompressionType.SNAPPY);
                    break;
            }
        }

        return producerConfig;
    }

    @Override
    public void stop() {
        try {
            pulsarClient.close();
        } catch (PulsarClientException e) {
            throw new StreamException(e);
        }
        super.stop();
    }

    @Override
    public void process(List<Message<Object>> messages) {
        for (Message<Object> message : messages) {
            String sendType = message.getHeaders().getString("sendType");
            Object payload = message.getPayload();
            try {
                if (StringUtils.isNotBlank(sendType) && sendType.equalsIgnoreCase("async")) {
                    pulsarProducer.sendAsync(Json.toString(payload).getBytes(StandardCharsets.UTF_8));
                } else {
                    pulsarProducer.send(Json.toString(payload).getBytes(StandardCharsets.UTF_8));
                }
            } catch (PulsarClientException e) {
                throw new StreamException(e);
            }
        }
    }
}
