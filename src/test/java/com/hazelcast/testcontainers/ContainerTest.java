package com.hazelcast.testcontainers;

import com.hazelcast.core.IMap;
import org.junit.Assert;
import org.junit.Test;

public abstract class ContainerTest {

  @Test
  public final void mapStoresKeyValue() {
    // given
    final IMap<String, String> map = container().newClient().getMap("test map");

    // when
    final String key = "test key";
    final String value = "test value";
    map.put(key, value);

    // then
    Assert.assertEquals(map.get(key), value);
  }

  protected abstract HazelcastContainer container();
}
