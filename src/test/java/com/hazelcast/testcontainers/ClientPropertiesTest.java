package com.hazelcast.testcontainers;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.impl.clientside.HazelcastClientProxy;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spi.properties.HazelcastProperty;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import java.time.Duration;
import java.util.Collections;

import static com.hazelcast.client.spi.properties.ClientProperty.HEARTBEAT_INTERVAL;

public final class ClientPropertiesTest extends ContainerTest {

  @ClassRule
  public static final HazelcastContainer hazelcast = new HazelcastContainer("3.12.12");

  @Test
  public void propertyIsPassedToClient() {
    // given
    final long heartbeatIntervalMs = Duration.ofSeconds(10).toMillis();
    final HazelcastProperty property = new HazelcastProperty(HEARTBEAT_INTERVAL.getName(), (int) heartbeatIntervalMs);

    // when
    final HazelcastInstance client = hazelcast.newClient(Collections.singleton(property));

    //then
    final ClientConfig clientConfig = ((HazelcastClientProxy) client).getClientConfig();
    Assert.assertEquals(heartbeatIntervalMs, Long.parseLong(clientConfig.getProperty(HEARTBEAT_INTERVAL.getName())));
  }

  @Override
  protected HazelcastContainer container() {
    return hazelcast;
  }
}
