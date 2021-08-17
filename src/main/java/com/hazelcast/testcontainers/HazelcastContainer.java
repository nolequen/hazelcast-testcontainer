package com.hazelcast.testcontainers;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperty;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;

import java.util.*;

import static java.util.stream.Collectors.joining;

public final class HazelcastContainer extends GenericContainer<HazelcastContainer> {
  private final List<HazelcastInstance> clients = new ArrayList<>();
  private final Map<String, String> properties = new HashMap<>();
  private String configPath = "";

  public HazelcastContainer() {
    this("latest");
  }

  public HazelcastContainer(String version) {
    super("hazelcast/hazelcast:" + version);
    withExposedPorts(NetworkConfig.DEFAULT_PORT);
  }

  @SuppressWarnings("MagicNumber")
  public HazelcastContainer withTinyConfig() {
    configPath = "hazelcast-tiny.xml";
    return withProperty(new HazelcastProperty(GroupProperty.PARTITION_COUNT.getName(), 11))
        .withProperty(new HazelcastProperty(GroupProperty.PARTITION_OPERATION_THREAD_COUNT.getName(), 2))
        .withProperty(new HazelcastProperty(GroupProperty.GENERIC_OPERATION_THREAD_COUNT.getName(), 2))
        .withProperty(new HazelcastProperty(GroupProperty.EVENT_THREAD_COUNT.getName(), 1));
  }

  public HazelcastContainer withConfig(String path) {
    configPath = path;
    return this;
  }

  @Override
  public void stop() {
    super.stop();
    clients.forEach(client -> client.getLifecycleService().terminate());
  }

  public HazelcastInstance newClient() {
    return newClient(Collections.emptySet());
  }

  public HazelcastInstance newClient(Iterable<HazelcastProperty> clientProperties) {
    final ClientConfig config = new ClientConfig();
    for (HazelcastProperty property : clientProperties) {
      config.setProperty(property.getName(), property.getDefaultValue());
    }
    config.getNetworkConfig().addAddress(getContainerIpAddress() + ':' + getFirstMappedPort());
    final HazelcastInstance client = HazelcastClient.newHazelcastClient(config);
    clients.add(client);
    return client;
  }

  public HazelcastContainer withProperty(HazelcastProperty property) {
    properties.put(property.getName(), property.getDefaultValue());
    return this;
  }

  @Override
  protected void configure() {
    withEnv("JAVA_OPTS", properties.entrySet().stream().map(e -> "-D" + e.getKey() + '=' + e.getValue()).collect(joining(" ")));
    if (!configPath.isEmpty()) {
      withClasspathResourceMapping(configPath, "/opt/hazelcast/hazelcast.xml", BindMode.READ_ONLY);
    }
  }
}