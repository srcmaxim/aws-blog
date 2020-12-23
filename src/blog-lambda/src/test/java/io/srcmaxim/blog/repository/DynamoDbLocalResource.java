package io.srcmaxim.blog.repository;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;
import java.util.function.Consumer;

public class DynamoDbLocalResource implements QuarkusTestResourceLifecycleManager {

    public static final StringBuffer LOGGER = new StringBuffer();

    private GenericContainer dynamoDbLocal;

    @Override
    public Map<String, String> start() {
        String endpointUrl = System.getProperty("quarkus.dynamodb.endpoint-override");
        if (endpointUrl == null) {
            LOGGER.append("DynamoDb endpoint URL was not found\n");
            LOGGER.append("Creating DynamoDb Local...\n");
            endpointUrl = createDynamoDbLocalResource();
        } else {
            LOGGER.append("DynamoDb endpoint URL was found\n");
        }
        return Map.of("quarkus.dynamodb.endpoint-override", endpointUrl);
    }

    private String createDynamoDbLocalResource() {
        dynamoDbLocal = new GenericContainer(DockerImageName.parse("amazon/dynamodb-local:1.11.477"))
                .withExposedPorts(8000)
                .withCommand("-jar DynamoDBLocal.jar -inMemory -sharedDb")
                .withLogConsumer((Consumer<OutputFrame>) this::staticLogConsumer);
        dynamoDbLocal.start();
        String host = dynamoDbLocal.getHost();
        Integer port = dynamoDbLocal.getMappedPort(8000);
        return String.format("http://%s:%d", host, port);
    }

    private void staticLogConsumer(OutputFrame outputFrame) {
        String utf8String = outputFrame.getUtf8String()
                .replaceAll("((\\r?\\n)|(\\r))$", "");
        LOGGER.append(utf8String).append('\n');
    }

    @Override
    public void stop() {
        if (dynamoDbLocal != null) {
            dynamoDbLocal.stop();
        }
    }

}
