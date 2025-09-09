package br.uesb.cipec.loja_automatica.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

// Says this is a base class for other tests
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers // Will use docker containers
public abstract class AbstractIntegrationTest {
    

    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
        new PostgreSQLContainer<>("postgres:16") // Use the image of postgres 16
        .withDatabaseName("testeBd") // set the name of data base
        .withUsername("testUser") // set the user name of database
        .withPassword("testPassword"); // set a passowrf for the database
    
    @Container
    private static final ElasticsearchContainer elasticsearchContainer =
    new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.14.0")
            .withEnv("xpack.security.enabled", "false")
            .withEnv("discovery.type", "single-node");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);

        registry.add("spring.elasticsearch.uris", elasticsearchContainer::getHttpHostAddress);
    }
}
