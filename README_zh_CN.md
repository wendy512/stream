# Stream

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.wendy512/stream/badge.svg)](https://search.maven.org/search?q=g:io.github.wendy512%20AND%20stream-core)
[![License](https://img.shields.io/badge/license-Apache--2.0-green.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

中文 | [English](README.md)

## 概述
Stream支持基于Spring的应用程序内的轻量级消息传递，并通过声明式适配器支持与外部系统的集成。这些适配器提供了比Spring对远程处理、消息传递和调度支持的更高级别的抽象。Stream的主要目标是提供一个简单的模型来构建企业集成解决方案，同时保持关注点分离，这对于生成可维护、可测试的代码至关重要。

基于Stream的应用程序内的轻量级消息传递，这样的应用程序是通过组装细粒度的可重用组件来构建的，以形成更高级别的功能。通过精心设计，这些流程可以模块化，也可以在更高级别上重用。Stream还提供了多种通道适配器和网关供与外部系统通信。通道适配器用于单向集成（发送或接收），异步消息高性能处理框架，采用固定线程异步处理消息，支持批处理消息。

![flow](doc/_media/flow1.jpg)
## 功能特性
- 支持Kafka、MQTT、Redis、RabbitMQ、Pulsar消息源和目标并支持拓展
- 消息源处理支持轮训和随机两种处理方式
- 消息处理支持可拓展的拦截器
- 支持SpringBoot2和SpringBoot3

## 需要
编译需要 JDK 8 及以上、Maven 3.2.5 及以上。

## 集成
如果你使用 Maven，你只需要在 pom.xml 中添加下面的依赖：

### Spring Boot2 版本
```xml  
<dependency>
    <groupId>io.github.wendy512</groupId>
    <artifactId>stream-core</artifactId>
    <version>1.0.4</version>
</dependency>
``` 

根据你的需要，添加对应的组件依赖，示例：mqtt组件
```xml  
<dependency>
    <groupId>io.github.wendy512</groupId>
    <artifactId>stream-mqtt</artifactId>
    <version>1.0.4</version>
</dependency>
```

支持以下组件
- stream-mqtt
- stream-kafka
- stream-pulsar
- stream-rabbitmq
- stream-redis

### Spring Boot3 版本
jdk版本必须在17及以上
```xml  
<dependency>
    <groupId>io.github.wendy512</groupId>
    <artifactId>stream-core-springboot3</artifactId>
    <version>1.0.4</version>
</dependency>
``` 

根据你的需要，添加对应的组件依赖，示例：mqtt组件
```xml  
<dependency>
    <groupId>io.github.wendy512</groupId>
    <artifactId>stream-mqtt-springboot3</artifactId>
    <version>1.0.4</version>
</dependency>
```

支持以下组件
- stream-mqtt-springboot3
- stream-kafka-springboot3
- stream-pulsar-springboot3
- stream-rabbitmq-springboot3
- stream-redis-springboot3

## 如何使用

[参考Wiki](https://github.com/wendy512/stream/wiki)

## 示例
* [Local queue](https://github.com/wendy512/stream-samples/tree/master/local-sample)
* [Kafka](https://github.com/wendy512/stream-samples/tree/master/kafka-sample)
* [MQTT](https://github.com/wendy512/stream-samples/tree/master/mqtt-sample)
* [RabbitMQ](https://github.com/wendy512/stream-samples/tree/master/rabbitmq-sample)
* [Redis](https://github.com/wendy512/stream-samples/tree/master/redis-sample)
* [Pulsar](https://github.com/wendy512/stream-samples/tree/master/pulsar-sample)

## 开源许可
Stream 基于 [Apache License 2.0](./LICENSE) 协议，Stream 依赖了一些第三方组件，它们的开源协议也为 Apache License 2.0。

## 联系方式

- 邮箱：<wendy512@yeah.net>
