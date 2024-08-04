package tom.patrzalek.lambdafunctionurlv2;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate awsSignedRestTemplate(AwsSigningInterceptor awsSigningInterceptor) {
        return new RestTemplateBuilder().interceptors(awsSigningInterceptor).build();
    }

}
