Log4j-QueuedConsoleAppender
===========================

[![Travis](https://img.shields.io/travis/DjDCH/Log4j-QueuedConsoleAppender.svg)](https://travis-ci.org/DjDCH/Log4j-QueuedConsoleAppender)
[![Maven Central](https://img.shields.io/maven-central/v/com.djdch.log4j/Log4j-QueuedConsoleAppender.svg)](http://mvnrepository.com/artifact/com.djdch.log4j/Log4j-QueuedConsoleAppender)
[![MIT License](https://img.shields.io/badge/license-MIT-8469ad.svg)](https://tldrlegal.com/license/mit-license)

Provide a QueuedConsoleAppender for Log4j 2 designed to queue log messages.

Requirements
------------

* Java and Maven installed

Build your project
------------------

Add the dependency in your `pom.xml` file:

```xml
<dependency>
    <groupId>com.djdch.log4j</groupId>
    <artifactId>log4j-queuedconsoleappender</artifactId>
    <version>1.0.0</version>
</dependency>
```

Use the following to get the queue instance:

```java
BlockingQueue<String> queue = QueuedConsoleAppender.getOutputQueue();
```

Use the following to disable the queue:

```java
QueuedConsoleAppender.setRunning(false);
```
