# Stream

English | [中文](README_zh_CN.md)
## Overview
Stream asynchronous message high-performance processing framework, similar to go chan, multi-threaded asynchronous processing of messages, supports batch processing of messages.

## Features
- Asynchronous multithreading (fixed thread), support for specifying the number of threads
- Support Kafka, MQTT, RabbitMQ message source and support expansion
- Message processing supports training and random processing
- Message processing support interceptor
- Support Spring Boot

## Requirements
Compilation requires JDK 8 and above, Maven 3.2.5 and above.

## Integrated
If you use Maven, you just need to add the following dependency in pom.xml:
```xml  
<dependency>
    <groupId>io.github</groupId>
    <artifactId>stream-core</artifactId>
    <version>1.0.0</version>
</dependency>
``` 

## License
Stream is based on the [Apache License 2.0](./LICENSE) agreement, and Stream relies on some third-party components whose open source agreement is also Apache License 2.0.
## Contact

- Email：<wendy512@yeah.net>
- QQ：214566407
