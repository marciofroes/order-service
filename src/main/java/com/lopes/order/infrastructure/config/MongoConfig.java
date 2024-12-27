package com.lopes.order.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.lopes.order.infrastructure.persistence.repository")
public class MongoConfig {
}
