package io.github.stream.redis;

/**
 * 常量类
 * @author taowenwu
 * @date 2023-10-17 16:29:55
 * @since 1.0.0
 */
public interface Constants {
    // 连接模式：单机
    String MODE_SINGLE = "single";
    // 连接模式：集群
    String MODE_CLUSTER = "cluster";
    // 连接模式：主从
    String MODE_MASTER_SLAVE = "master-slave";
    // 连接模式：主从复制
    String MODE_REPLICATED = "replicated";

    String TOPIC_KEY = "topic";
}
