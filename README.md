# Stream

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.wendy512/stream/badge.svg)](https://search.maven.org/search?q=g:io.github.wendy512%20AND%20stream-core)
[![License](https://img.shields.io/badge/license-Apache--2.0-green.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

English | [中文](README_zh_CN.md)

## Overview
Stream supports lightweight messaging within Spring-based applications and supports integration with external systems through declarative adapters. These adapters provide a higher level of abstraction than Spring's support for remote processing, messaging, and scheduling. The main goal of Stream is to provide a simple model for building enterprise integration solutions while maintaining separation of concerns, which is essential for producing maintainable and testable code.

Lightweight messaging within Stream-based applications, such applications are built by assembling fine-grained reusable components to form higher-level functions. Through careful design, these processes can be modularized and reused at a higher level. Stream also provides a variety of channel adapters and gateways for communicating with external systems. Channel adapters are used for one-way integration (send or receive), asynchronous message high-performance processing framework, fixed thread asynchronous processing of messages, and support for batch processing of messages.
## Features
- Supports Kafka, MQTT, Redis, RabbitMQ, Pulsar message sources and targets and supports expansion
- Message source processing supports two processing methods: round-robin and random
- Message processing supports extensible interceptors
- Support SpringBoot2 and SpringBoot3

## Requirements
Compilation requires JDK 8 and above, Maven 3.2.5 and above.

## Integrated
If you use Maven, you just need to add the following dependency in pom.xml

### Spring Boot2 version
```xml  
<dependency>
    <groupId>io.github.wendy512</groupId>
    <artifactId>stream-core</artifactId>
    <version>1.0.4</version>
</dependency>
``` 

### Spring Boot3 version
The jdk version must be 17 or above
```xml  
<dependency>
    <groupId>io.github.wendy512</groupId>
    <artifactId>stream-core-springboot3</artifactId>
    <version>1.0.4</version>
</dependency>
``` 

## How to use

[Reference Wiki](https://github.com/wendy512/stream/wiki)

## Examples
* [Local queue](https://github.com/wendy512/stream-samples/tree/master/local-sample)
* [Kafka](https://github.com/wendy512/stream-samples/tree/master/kafka-sample)
* [MQTT](https://github.com/wendy512/stream-samples/tree/master/mqtt-sample)
* [RabbitMQ](https://github.com/wendy512/stream-samples/tree/master/rabbitmq-sample)
* [Redis](https://github.com/wendy512/stream-samples/tree/master/redis-sample)
* [Pulsar](https://github.com/wendy512/stream-samples/tree/master/pulsar-sample)

## License
Stream is based on the [Apache License 2.0](./LICENSE) agreement, and Stream relies on some third-party components whose open source agreement is also Apache License 2.0.
## Contact

- Email：<wendy512@yeah.net>
