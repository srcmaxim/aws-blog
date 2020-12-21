package io.srcmaxim.blog.config;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class DynamoDbConfigTest {

    @Inject
    DynamoDbConfig dynamoDbConfig;

    @Test
    void fields_WhenConfigSet_ThenReturnValues() {
        assertThat(dynamoDbConfig.tableName).isEqualTo("Blog");
    }

}
