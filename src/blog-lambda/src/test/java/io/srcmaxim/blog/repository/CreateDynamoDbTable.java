package io.srcmaxim.blog.repository;

import io.srcmaxim.blog.config.DynamoDbConfig;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CreateDynamoDbTable {

    private static final Logger LOGGER = Logger.getLogger(CreateDynamoDbTable.class);

    @Inject
    DynamoDbClient dynamoDb;

    @Inject
    DynamoDbConfig dynamoDbConfig;

    @ConfigProperty(name = "quarkus.dynamodb.endpoint-override")
    String dynamoDbEndpointUrl;

    @PostConstruct
    void onStart() {
        LOGGER.info("Getting DynamoDB resource...");
        LOGGER.info(DynamoDbLocalResource.LOGGER);
        LOGGER.infov("DynamoDB Endpoint URL: [{0}]", dynamoDbEndpointUrl);
        LOGGER.info("Checking DynamoDB Table...");
        try {
            DescribeTableResponse describeTableResponse = dynamoDb.describeTable(describeTableRequest());
            LOGGER.info("DynamoDB Table exists");
            describeTable(describeTableResponse.table());
        } catch (ResourceNotFoundException e) {
            LOGGER.info("DynamoDB Table does not exist");
            LOGGER.info("Creating DynamoDB Table...");
            CreateTableResponse createTableResponse = dynamoDb.createTable(createTableRequest());
            describeTable(createTableResponse.tableDescription());
        }
    }

    private DescribeTableRequest describeTableRequest() {
        return  DescribeTableRequest.builder()
                .tableName(dynamoDbConfig.tableName)
                .build();
    }

    private void describeTable(TableDescription tableDescription) {
        LOGGER.infov("DynamoDB Table: [{0}] at [{1}]",
                tableDescription.tableArn(), tableDescription.creationDateTime());
        tableDescription.globalSecondaryIndexes().forEach(globalSecondaryIndexDescription -> {
            LOGGER.infov("DynamoDB Table GSI: [{0}] at [{1}]",
                    tableDescription.tableArn(), tableDescription.creationDateTime());
        });
    }

    private CreateTableRequest createTableRequest() {
        AttributeDefinition pk = AttributeDefinition.builder().attributeName("PK").attributeType(ScalarAttributeType.S).build();
        AttributeDefinition sk = AttributeDefinition.builder().attributeName("SK").attributeType(ScalarAttributeType.S).build();
        AttributeDefinition gsi1Pk = AttributeDefinition.builder().attributeName("GSI1PK").attributeType(ScalarAttributeType.S).build();
        AttributeDefinition gsi1Sk = AttributeDefinition.builder().attributeName("GSI1SK").attributeType(ScalarAttributeType.S).build();
        KeySchemaElement pkKey = KeySchemaElement.builder().attributeName("PK").keyType(KeyType.HASH).build();
        KeySchemaElement skKey = KeySchemaElement.builder().attributeName("SK").keyType(KeyType.RANGE).build();
        ProvisionedThroughput provisionedThroughput = ProvisionedThroughput.builder().readCapacityUnits(1L).writeCapacityUnits(1L).build();
        return CreateTableRequest.builder()
                .tableName(dynamoDbConfig.tableName)
                .attributeDefinitions(pk, sk, gsi1Pk, gsi1Sk)
                .keySchema(pkKey, skKey)
                .provisionedThroughput(provisionedThroughput)
                .globalSecondaryIndexes(createGsi1Request())
                .build();
    }

    private GlobalSecondaryIndex createGsi1Request() {
        KeySchemaElement gsi1PkKey = KeySchemaElement.builder().attributeName("GSI1PK").keyType(KeyType.HASH).build();
        KeySchemaElement gsi1SkKey = KeySchemaElement.builder().attributeName("GSI1SK").keyType(KeyType.RANGE).build();
        ProvisionedThroughput provisionedThroughput = ProvisionedThroughput.builder().readCapacityUnits(1L).writeCapacityUnits(1L).build();
        return GlobalSecondaryIndex.builder()
                .indexName("GSI1")
                .keySchema(gsi1PkKey, gsi1SkKey)
                .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                .provisionedThroughput(provisionedThroughput)
                .build();
    }

}
