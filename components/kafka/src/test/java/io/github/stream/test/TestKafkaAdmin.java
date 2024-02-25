package io.github.stream.test;

import java.util.Collection;
import java.util.Properties;

import org.apache.kafka.clients.admin.*;
import org.junit.Before;
import org.junit.Test;

/**
 * @author taowenwu
 * @date 2023-10-12 10:44:01
 * @since 1.0.0
 */
public class TestKafkaAdmin {

    private AdminClient adminClient;

    @Before
    public void init() {
        Properties properties = new Properties();
        properties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.6.196:9092");
        properties.put(AdminClientConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 10000);
        properties.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000);
        this.adminClient = AdminClient.create(properties);
    }

    @Test
    public void test1() throws Exception {
        // 是否查看internal选项
        ListTopicsOptions options = new ListTopicsOptions();
        // 设置我们是否应该列出内部topic。
        options.listInternal(false);
        ListTopicsResult topicResult = adminClient.listTopics(options);
        Collection<TopicListing> topicListing = topicResult.listings().get();
        topicListing.stream().forEach(t -> {
            System.out.println("topic: " + t.name());
        });
    }
}
