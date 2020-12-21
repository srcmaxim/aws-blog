package io.srcmaxim.blog.config;

import io.quarkus.arc.config.ConfigProperties;
import io.srcmaxim.blog.model.dynamodb.DynamoDb;

@ConfigProperties(prefix = "dynamodb")
public class DynamoDbConfig {

    public String tableName = DynamoDb.Blog.TABLE_NAME;

}
