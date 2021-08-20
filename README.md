# Hazelcast Testcontainer #

[![Maven Central](https://img.shields.io/maven-central/v/su.nlq/hazelcast-testcontainer)](https://maven-badges.herokuapp.com/maven-central/su.nlq/hazelcast-testcontainer)
[![Build Status](https://travis-ci.com/nolequen/hazelcast-testcontainer.svg?branch=main)](https://travis-ci.com/nolequen/hazelcast-testcontainer)
[![codecov](https://codecov.io/gh/nolequen/hazelcast-testcontainer/branch/main/graph/badge.svg?token=WMU2ULSQCS)](https://codecov.io/gh/nolequen/hazelcast-testcontainer)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/a306b153da6e42af9dab6f1e06f11a0a)](https://www.codacy.com/gh/nolequen/hazelcast-testcontainer/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=nolequen/hazelcast-testcontainer&amp;utm_campaign=Badge_Grade)

## Usage

Simple usage example:

```java
@ClassRule
public static HazelcastContainer hazelcast = new HazelcastContainer();

@Test
public void test(){
    HazelcastInstance client = hazelcast.newClient();

    // do anything you want
}
```
