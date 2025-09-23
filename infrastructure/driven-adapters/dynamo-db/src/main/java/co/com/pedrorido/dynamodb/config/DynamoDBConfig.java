package co.com.pedrorido.dynamodb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;

import java.net.URI;

@Configuration
public class DynamoDBConfig {

    // === 2) Ambientes reales de AWS: SIN endpointOverride, SIN mocks ===
    @Bean
    @Profile({"dev", "cer", "pdn"})
    public DynamoDbAsyncClient dynamoDbAwsClient(
            MetricPublisher publisher,
            @Value("${aws.region}") String region
    ) {
        return DynamoDbAsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .overrideConfiguration(o -> o.addMetricPublisher(publisher))
                .build();
    }

    // === 3) Enhanced client común ===
    @Bean
    public DynamoDbEnhancedAsyncClient dynamoDbEnhanced(DynamoDbAsyncClient client) {
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(client)
                .build();
    }
}
