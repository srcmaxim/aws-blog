package io.srcmaxim.blog.service;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class MetaServiceTest {

    @Inject
    MetaService metaService;

    @Test
    void getMeta_WhenConfigSet_ThenReturnValues() {
        Object meta = metaService.getMeta();

        assertThat(meta).isEqualTo(Map.of(
                "buildNumber", "BUILD_NUMBER",
                "sourceVersion", "SOURCE_VERSION",
                "tableName", "Blog"
        ));
    }

}
