package com.lopes.order.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class MongoTestConfig {

    @Bean(destroyMethod = "stop")
    public MongoDBContainer mongoDBContainer() {
        MongoDBContainer container = new MongoDBContainer(DockerImageName.parse("mongo:6.0.8"));
        container.start();
        System.setProperty("MONGODB_URI", container.getReplicaSetUrl());
        return container;
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory(MongoDBContainer mongoDBContainer) {
        return new SimpleMongoClientDatabaseFactory(mongoDBContainer.getReplicaSetUrl());
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}
