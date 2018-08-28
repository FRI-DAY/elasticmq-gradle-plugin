# ElasticMQ Gradle Plugin [![Build Status](https://travis-ci.org/FRI-DAY/elasticmq-gradle-plugin.svg?branch=master)](https://travis-ci.org/FRI-DAY/elasticmq-gradle-plugin)

This plugin allows you to easily integrate [ElasticMQ] in
your Gradle builds! With it you can easily start and stop
queue instances with different configurations at any
point of your build.

## Usage

To apply the plugin you just need to add it to the plugin
list!

```groovy
plugins {
    id 'de.friday.elasticmq' version '1.0.0-SNAPSHOT'
}
``` 

> **NOTE:** Currently the plugin is not yet published
to the Gradle Plugin Portal, however you can still apply
it as above (to make it as compatible as possible with
when it does get published) by building the Shadow Jar
and managing your plugins in the settings, as can be
seen in the [Integration Tests].

After applying the plugin you have access to a custom
DSL that allows you to easily configure several ElasticMQ
instances. An example with the Groovy DSL:

```groovy
elasticmq {
    instances {
        local {
            protocol = 'http'
            host = 'localhost'
            port = 9324
            contextPath = 'path'

            limits = 'relaxed'

            queues {
                sample {
                    attribute 'DelaySeconds', '0'
                }
            }
        }
    }
}
```

In the above example you can see that a single ElasticMQ
instance was configured, named `local`, and that it is
using the `http` protocol on the `localhost`, exposing
itself in port `9324`. Furthermore, this instance is
configured to use the `relaxed` option for the SQS Limits
instead of the default `strict` mode. It also overrides
the default blank Context Path to be `path`. The meaning
of these configuration options can be consulted on the
official ElasticMQ repository since they are exposed one
to one. The instance is also pre-configured with a queue
named `sample` that just specifies one attribute,
`DelaySeconds`, to be `0`. Note that instead of adding
attributes one on one with the `attribute` method
you can also just override the `attributes` variable
directly.

The above configuration will automatically generate two
tasks: `startLocalElasticMq` and `stopLocalElasticMq`,
which start and stop the configured ElasticMQ instance
respectively. Note that these tasks are not automatically
wired to any other task, giving you the complete control
on how to manage the instances.

## Motivation

This plugin is mainly an exercise of learning how to 
develop Gradle plugins, from architecture design to complete
testing, while trying to use best practices as much as
possible. By focusing the center piece of the plugin in
management of server instances we have a use case to learn
how to develop a configurationn DSL as well as tests that
integrate and validate that said instances were actually
started, configured and stopped as told.

Since the main focus of the project is to learn we welcome
experimenting with new ways of achieving results. We also
aim at achieving a base architecture that can be applied
to new plugins, so improvements over the existing architecture,
implementation of best practices or tests are also highly
welcome.

[ElasticMQ]: https://github.com/adamw/elasticmq
[Integration Tests]: integration-test/settings.gradle.kts
