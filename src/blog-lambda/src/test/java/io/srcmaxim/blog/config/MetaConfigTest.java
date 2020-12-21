package io.srcmaxim.blog.config;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class MetaConfigTest {

    @Inject
    MetaConfig metaConfig;

    @Test
    void fields_WhenConfigSet_ThenReturnValues() {
        assertThat(metaConfig.buildNumber).isEqualTo("BUILD_NUMBER");
        assertThat(metaConfig.sourceVersion).isEqualTo("SOURCE_VERSION");
    }

}
