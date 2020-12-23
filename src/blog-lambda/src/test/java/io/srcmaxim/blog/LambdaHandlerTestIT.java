package io.srcmaxim.blog;

import io.quarkus.test.junit.NativeImageTest;
import io.srcmaxim.blog.repository.CreateDynamoDbTable;
import io.srcmaxim.blog.repository.DynamoDbLocalResource;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * You can:
 *
 * 1. Use Bash/PowerShell to start DynamoDB Local:
 * {@code docker run --rm --publish 8000:8000 amazon/dynamodb-local:1.11.477 -jar DynamoDBLocal.jar -inMemory -sharedDb}
 * If you are planning to build app inside Docker:
 * {@code mvn clean install -Pnative -Dquarkus.native.container-build=true -Dquarkus.dynamodb.endpoint-override=http://$(hostname -I | cut -d' ' -f1):8000
 * Or you can manually create DynamoDB Table with NoSql Workbench and provided in {@code Blog Data Model.json}.
 *
 * 2. Use classes {@link DynamoDbLocalResource} and {@link CreateDynamoDbTable}
 * (that are running during test phase) for creating:
 *      - DynamoDB Local -- if {@code -Dquarkus.dynamodb.endpoint-override} is not specified.
 *      - Blog Table -- if it doesn't exist in DynamoDB
 */
@NativeImageTest
@Tag("native")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LambdaHandlerTestIT extends AbstractLambdaHandlerTest {

    // Execute the same tests but in native mode.

}
