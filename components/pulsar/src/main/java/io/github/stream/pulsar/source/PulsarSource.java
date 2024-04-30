package io.github.stream.pulsar.source;

import java.util.*;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.client.api.*;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import io.github.stream.core.AbstractAutoRunnable;
import io.github.stream.core.StreamException;
import io.github.stream.core.configuration.ConfigContext;
import io.github.stream.core.message.MessageBuilder;
import io.github.stream.core.properties.BaseProperties;
import io.github.stream.core.source.AbstractSource;
import io.github.stream.pulsar.PulsarStateConfigure;
import lombok.extern.slf4j.Slf4j;

/**
 * pulsar-source
 * @author jujiale
 * @date 2024/04
 */
@Slf4j
public class PulsarSource  extends AbstractSource<String> {

    private final PulsarStateConfigure pulsarStateConfigure = new PulsarStateConfigure();

    private Consumer<String> pulsarConsumer;

    private PulsarClient pulsarClient;

    private PulsarPollingRunner runner;

    private Thread runnerThread;

    private int interval;


    @Override
    public void configure(ConfigContext context) throws Exception {
        pulsarStateConfigure.configure(context);
        pulsarClient = pulsarStateConfigure.newPulsarClient();
        this.initPulsarConsumer(context.getConfig());
    }

    @Override
    public void stop() {
        if (runnerThread != null) {
            runner.shutdown();
            //runnerThread.interrupt();

            while (runnerThread.isAlive()) {
                try {
                    log.debug("Waiting for runner thread to exit");
                    runnerThread.join(500);
                } catch (InterruptedException e) {
                    log.error("Interrupted while waiting for runner thread to exit. Exception follows.",
                            e);
                }
            }
        }
        try {
            pulsarClient.close();
        } catch (PulsarClientException e) {
            throw new StreamException(e);
        }
        super.stop();
    }

    public void unsubscribe() {
        try {
            pulsarConsumer.unsubscribe();
            pulsarConsumer.close();
        } catch (PulsarClientException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public void start() {
        this.runner = new PulsarPollingRunner();
        this.runnerThread = new Thread(runner, "pulsar-source-runner");

        this.runner.startup();
        this.runnerThread.start();
        super.start();
    }

    @SuppressWarnings("unchecked")
    private void initPulsarConsumer(BaseProperties properties) {
        Map<String, Object> consumerConfig = properties.getOriginal();
        Assert.notNull(consumerConfig, "pulsar sink consumer config cannot empty");
        Map<String, Object> loadConsumerConfig = initConsumerLoadConfig(consumerConfig);
        try {
            pulsarConsumer = pulsarClient.newConsumer(Schema.STRING).loadConf(loadConsumerConfig).subscribe();
        } catch (PulsarClientException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> initConsumerLoadConfig(Map<String, Object> config) {
        Map<String, Object> consumerConfig = new HashMap<>(config);

        // add topicName
        Map<String, String> topicNames = (Map<String, String>) config.get("topicNames");
        Set<String> topicNameSet = new HashSet<>(topicNames.size());
        topicNames.forEach((k, topicName) -> topicNameSet.add(topicName));
        consumerConfig.put("topicNames", topicNameSet);

        // add topicsPattern
        String topicsPattern = (String) config.get("topicsPattern");
        if (StringUtils.isNotBlank(topicsPattern)) {
            Pattern allTopicsInNamespace = Pattern.compile(topicsPattern);
            consumerConfig.put("topicsPattern", allTopicsInNamespace);
        }

        // add subscriptionType
        String subscriptionType = (String) config.get("subscriptionType");
        if (StringUtils.isNotBlank(subscriptionType)) {
            String lowerCase = subscriptionType.toLowerCase();
            switch (lowerCase) {
                case "exclusive":
                    consumerConfig.put("subscriptionType", SubscriptionType.Exclusive);
                    break;
                case "failover":
                    consumerConfig.put("subscriptionType", SubscriptionType.Failover);
                    break;
                case "shared":
                    consumerConfig.put("subscriptionType", SubscriptionType.Shared);
                    break;
                case "key_shared":
                    consumerConfig.put("subscriptionType", SubscriptionType.Key_Shared);
                    break;
            }
        }

        // add cryptoFailureAction
        String cryptoFailureAction = (String) config.get("cryptoFailureAction");
        if (StringUtils.isNotBlank(cryptoFailureAction)) {
            String lowerCase = subscriptionType.toLowerCase();
            switch (lowerCase) {
                case "fail":
                    consumerConfig.put("cryptoFailureAction", ConsumerCryptoFailureAction.FAIL);
                    break;
                case "discard":
                    consumerConfig.put("cryptoFailureAction", ConsumerCryptoFailureAction.DISCARD);
                    break;
                case "consume":
                    consumerConfig.put("cryptoFailureAction", ConsumerCryptoFailureAction.CONSUME);
                    break;
            }
        }


        // add properties
        Map<String, String> propertiesMap = (Map<String, String>) config.get("properties");
        if (!CollectionUtils.isEmpty(propertiesMap)) {
            SortedMap<String, String> sortMapProperties = new TreeMap<>(propertiesMap);
            consumerConfig.put("properties", sortMapProperties);

        }

        // add subscriptionInitialPosition
        String subscriptionInitialPosition = (String) config.get("subscriptionInitialPosition");
        if (StringUtils.isNotBlank(subscriptionInitialPosition)) {
            String lowerCase = subscriptionInitialPosition.toLowerCase();
            switch (lowerCase) {
                case "latest":
                    consumerConfig.put("subscriptionInitialPosition", SubscriptionInitialPosition.Latest);
                    break;
                case "earliest":
                    consumerConfig.put("subscriptionInitialPosition", SubscriptionInitialPosition.Earliest);
                    break;
            }
        }
        // add regexSubscriptionMode
        String regexSubscriptionMode = (String) config.get("regexSubscriptionMode");
        if (StringUtils.isNotBlank(regexSubscriptionMode)) {
            String lowerCase = regexSubscriptionMode.toLowerCase();
            switch (lowerCase) {
                case "persistentonly":
                    consumerConfig.put("regexSubscriptionMode", RegexSubscriptionMode.PersistentOnly);
                    break;
                case "nonpersistentonly":
                    consumerConfig.put("regexSubscriptionMode", RegexSubscriptionMode.NonPersistentOnly);
                    break;
                case "alltopics":
                    consumerConfig.put("regexSubscriptionMode", RegexSubscriptionMode.AllTopics);
                    break;
            }
        }
        return consumerConfig;
    }

    // 这里需要使用不同的消费策略去兼容各种各样的pulsar消费策略

    private class PulsarPollingRunner extends AbstractAutoRunnable {

        @Override
        public void runInternal() {

            while (isRunning()) {

                Messages<String> pulsarMessages;

                try {
                    pulsarMessages = pulsarConsumer.batchReceive();
                } catch (PulsarClientException e) {
                    throw new RuntimeException(e);
                }

                for (Message<String> pulsarMessage : pulsarMessages) {
                    io.github.stream.core.Message<String> message = MessageBuilder.withPayload(pulsarMessage.getValue()).build();
                    getChannelProcessor().send(message);
                    try {
                        pulsarConsumer.acknowledge(pulsarMessage);
                    } catch (PulsarClientException e) {
                        pulsarConsumer.negativeAcknowledge(pulsarMessage);
                        throw new StreamException(e);
                    }
                }
            }

            if (interval > 0) {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException ex) {
                    // 有可能调用interrupt会触发sleep interrupted异常
                    return;
                }
            }
            unsubscribe();
        }
    }
}
