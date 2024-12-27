package com.lopes.order;

import com.lopes.order.config.MongoTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(MongoTestConfig.class)
class OrderApplicationTests {

    @Test
    void contextLoads() {
    }

}
