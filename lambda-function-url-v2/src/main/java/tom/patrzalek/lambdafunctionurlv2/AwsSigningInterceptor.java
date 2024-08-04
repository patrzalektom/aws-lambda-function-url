package tom.patrzalek.lambdafunctionurlv2;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4FamilyHttpSigner;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;

import java.io.IOException;
import java.net.URI;


@Component
public class AwsSigningInterceptor implements ClientHttpRequestInterceptor {

    private final AwsConfiguration awsConfiguration;

    public AwsSigningInterceptor(AwsConfiguration awsConfiguration) {
        this.awsConfiguration = awsConfiguration;
    }


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        SdkHttpRequest sdkHttpRequest = SdkHttpRequest.builder()
                .uri(request.getURI())
                .method(SdkHttpMethod.fromValue(request.getMethod().name()))
                .putHeader("Content-Type", "application/json")
                .build();


        AwsV4HttpSigner awsSigner = AwsV4HttpSigner.create();
        AwsCredentialsIdentity credentialsIdentity = AwsCredentialsIdentity.create(
                awsConfiguration.getAccessKey(),
                awsConfiguration.getSecretKey()
        );
        String serviceName = "lambda";
        String region = awsConfiguration.getRegion();

        SdkHttpRequest signedRequest = awsSigner.sign(r -> r.identity(credentialsIdentity)
                        .request(sdkHttpRequest)
                        .payload(ContentStreamProvider.fromByteArray(body))
                        .putProperty(AwsV4FamilyHttpSigner.SERVICE_SIGNING_NAME, serviceName)
                        .putProperty(AwsV4HttpSigner.REGION_NAME, region))
                .request();

        HttpRequest signedHttpRequest = convertToHttpRequest(signedRequest);
        return execution.execute(signedHttpRequest, body);
    }

    private HttpRequest convertToHttpRequest(SdkHttpRequest request) {
        return new HttpRequest() {
            @Override
            public HttpMethod getMethod() {
                return HttpMethod.valueOf(request.method().name());
            }

            @Override
            public URI getURI() {
                return request.getUri();
            }

            @Override
            public HttpHeaders getHeaders() {
                var headers = new HttpHeaders();
                request.headers().forEach(headers::addAll);
                return headers;
            }
        };
    }
}
