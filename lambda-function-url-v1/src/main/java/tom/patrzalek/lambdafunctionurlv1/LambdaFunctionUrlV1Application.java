package tom.patrzalek.lambdafunctionurlv1;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class LambdaFunctionUrlV1Application {

    private final RestTemplate awsSignedRestTemplate;

    private final AwsConfiguration awsConfiguration;

    public LambdaFunctionUrlV1Application(@Qualifier("awsSignedRestTemplate") RestTemplate awsSignedRestTemplate,
                                          AwsConfiguration awsConfiguration) {
        this.awsSignedRestTemplate = awsSignedRestTemplate;
        this.awsConfiguration = awsConfiguration;
    }

    public static void main(String[] args) {
        SpringApplication.run(LambdaFunctionUrlV1Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            ResponseEntity<String> lambdaResponse = awsSignedRestTemplate.exchange(
                    awsConfiguration.getLambdaFunctionUrl(),
                    HttpMethod.GET,
                    null,
                    String.class
            );
            System.out.println(lambdaResponse.getStatusCode());
            System.out.println(lambdaResponse.getBody());
        };
    }

}
