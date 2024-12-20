package tom.patrzalek.lambdafunctionurlv2;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws")
public class AwsConfiguration {

    private String region;
    private String lambdaFunctionUrl;
    private String accessKey;
    private String secretKey;

    //getters and setters

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getLambdaFunctionUrl() {
        return lambdaFunctionUrl;
    }

    public void setLambdaFunctionUrl(String lambdaFunctionUrl) {
        this.lambdaFunctionUrl = lambdaFunctionUrl;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
