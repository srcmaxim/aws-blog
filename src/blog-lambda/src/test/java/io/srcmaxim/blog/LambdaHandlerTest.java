package io.srcmaxim.blog;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.srcmaxim.blog.repository.CreateDynamoDbTable;
import io.srcmaxim.blog.repository.DynamoDbLocalResource;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestMethodOrder;

import javax.inject.Inject;


@QuarkusTest
@Tag("integration")
@QuarkusTestResource(DynamoDbLocalResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LambdaHandlerTest extends AbstractLambdaHandlerTest {

    @Inject
    CreateDynamoDbTable createDynamoDbTable;

}
