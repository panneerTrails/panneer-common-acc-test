package com.mrll.javelin.common.test.embededMQ;

import org.apache.qpid.server.SystemLauncher;
import org.apache.qpid.server.model.SystemConfig;
import org.junit.rules.ExternalResource;

import java.util.HashMap;
import java.util.Map;


public class EmbeddedAmqpBroker extends ExternalResource {

    private final SystemLauncher broker = new SystemLauncher();

    @Override
    public void before() throws Throwable {
        startQpidBroker();
    }

    @Override
    public void after() {
        broker.shutdown();
    }

    private void startQpidBroker() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("type", "Memory");
        attributes.put(SystemConfig.INITIAL_CONFIGURATION_LOCATION, findResourcePath("qpid-config.json"));
        broker.startup(attributes);
    }

    private String findResourcePath(final String fileName) {
        return EmbeddedAmqpBroker.class.getClassLoader().getResource(fileName).toExternalForm();
    }
}