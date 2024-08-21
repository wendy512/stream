# Stream

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.wendy512/stream/badge.svg)](https://search.maven.org/search?q=g:io.github.wendy512%20AND%20stream)
[![License](https://img.shields.io/badge/license-Apache--2.0-green.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

中文 | [English](README.md)

## 概述
Stream异步消息高性能处理框架，类似go chan，多线程异步处理消息，支持批处理消息。

## 功能特性
- 异步多线程（固定线程），支持指定线程数
- 支持Kafka、MQTT、RabbitMQ消息源并支持拓展
- 消息处理支持训和随机两种处理方式
- 消息处理支持拦截器
- 支持SpringBoot

## 需要
编译需要 JDK 8 及以上、Maven 3.2.5 及以上。

## 集成
如果你使用 Maven，你只需要在 pom.xml 中添加下面的依赖：

### Spring Boot2 版本
```xml  
<dependency>
    <groupId>io.github.wendy512</groupId>
    <artifactId>stream-core</artifactId>
    <version>1.0.3</version>
</dependency>
``` 

### Spring Boot3 版本
jdk版本必须在17及以上
```xml  
<dependency>
    <groupId>io.github.wendy512</groupId>
    <artifactId>stream-core-springboot3</artifactId>
    <version>1.0.3</version>
</dependency>
``` 

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
