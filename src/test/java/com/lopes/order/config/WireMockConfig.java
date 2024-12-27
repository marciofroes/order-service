package com.lopes.order.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.TestPropertySource;

@TestConfiguration
@Profile("test")
@TestPropertySource(properties = {
    "product.service.url=http://localhost:${wiremock.server.port}",
    "de.flapdoodle.mongodb.embedded.version=6.0.1"
})
public class WireMockConfig {

    private static final WireMockServer wireMockServer;

    static {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        System.setProperty("wiremock.server.port", String.valueOf(wireMockServer.port()));
    }

    @Bean(destroyMethod = "stop")
    @Primary
    public WireMockServer wireMockServer() {
        return wireMockServer;
    }
}
