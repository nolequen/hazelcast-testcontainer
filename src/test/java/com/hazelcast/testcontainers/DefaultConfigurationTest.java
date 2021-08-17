package com.hazelcast.testcontainers;

import org.junit.ClassRule;

public final class DefaultConfigurationTest extends ContainerTest {

  @ClassRule
  public static final HazelcastContainer hazelcast = new HazelcastContainer("3.12.12");

  @Override
  protected HazelcastContainer container() {
    return hazelcast;
  }
}
