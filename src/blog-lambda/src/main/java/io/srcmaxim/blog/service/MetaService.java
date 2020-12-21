package io.srcmaxim.blog.service;

import io.srcmaxim.blog.config.DynamoDbConfig;
import io.srcmaxim.blog.config.MetaConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;

@ApplicationScoped
public class MetaService {

    @Inject
    MetaConfig metaConfig;

    @Inject
    DynamoDbConfig dynamoDbConfig;

    public Object getMeta() {
        return Map.of(
                "buildNumber", metaConfig.buildNumber,
                "sourceVersion", metaConfig.sourceVersion,
                "tableName", dynamoDbConfig.tableName
        );
    }

}
