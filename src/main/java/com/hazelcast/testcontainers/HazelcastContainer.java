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

/**
 * Customizable Hazelcast test container.
 */
public final class HazelcastContainer extends GenericContainer<HazelcastContainer> {
  private final List<HazelcastInstance> clients = new ArrayList<>();
  private final Map<String, String> properties = new HashMap<>();
  private String configPath = "";

  /**
   * Creates a {@link HazelcastContainer} with latest Hazelcast version.
   */
  public HazelcastContainer() {
    this("latest");
  }

  /**
   * Creates a {@link HazelcastContainer} with specified Hazelcast version.
   *
   * @param version Hazelcast version
   */
  public HazelcastContainer(String version) {
    super("hazelcast/hazelcast:" + version);
    withExposedPorts(NetworkConfig.DEFAULT_PORT);
  }

  /**
   * Adds tiny Hazelcast configuration whereby container could start faster.
   *
   * @return a reference to HazelcastContainer itself, so the API can be used fluently
   */
  @SuppressWarnings("MagicNumber")
  public HazelcastContainer withTinyConfig() {
    configPath = "hazelcast-tiny.xml";
    return withProperty(new HazelcastProperty(GroupProperty.PARTITION_COUNT.getName(), 11))
        .withProperty(new HazelcastProperty(GroupProperty.PARTITION_OPERATION_THREAD_COUNT.getName(), 2))
        .withProperty(new HazelcastProperty(GroupProperty.GENERIC_OPERATION_THREAD_COUNT.getName(), 2))
        .withProperty(new HazelcastProperty(GroupProperty.EVENT_THREAD_COUNT.getName(), 1));
  }

  /**
   * Adds custom Hazelcast configuration file.
   *
   * @param path Configuration file path
   * @return a reference to HazelcastContainer itself, so the API can be used fluently
   */
  public HazelcastContainer withConfig(String path) {
    configPath = path;
    return this;
  }

  /**
   * Adds custom Hazelcast property to the container.
   *
   * @return a reference to HazelcastContainer itself, so the API can be used fluently
   */
  public HazelcastContainer withProperty(HazelcastProperty property) {
    properties.put(property.getName(), property.getDefaultValue());
    return this;
  }

  @Override
  public void stop() {
    super.stop();
    clients.forEach(client -> client.getLifecycleService().terminate());
  }

  /**
   * Creates and returns new Hazelcast client instance.
   *
   * @return client HazelcastInstance
   */
  public HazelcastInstance newClient() {
    return newClient(Collections.emptySet());
  }

  /**
   * Creates and returns new Hazelcast client instance with custom properties.
   *
   * @param clientProperties Custom Hazelcast client properties
   * @return client HazelcastInstance
   */
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

  @Override
  protected void configure() {
    withEnv("JAVA_OPTS", properties.entrySet().stream().map(e -> "-D" + e.getKey() + '=' + e.getValue()).collect(joining(" ")));
    if (!configPath.isEmpty()) {
      withClasspathResourceMapping(configPath, "/opt/hazelcast/hazelcast.xml", BindMode.READ_ONLY);
    }
  }
}