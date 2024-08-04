package tom.patrzalek.lambdafunctionurlv1;

import com.amazonaws.DefaultRequest;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.HttpMethodName;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

@Component
public class AwsSigningInterceptor implements ClientHttpRequestInterceptor {

    private final AwsConfiguration awsConfiguration;

    public AwsSigningInterceptor(AwsConfiguration awsConfiguration) {
        this.awsConfiguration = awsConfiguration;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        DefaultRequest<?> awsRequest = new DefaultRequest<>("lambda");
        awsRequest.setHttpMethod(HttpMethodName.valueOf(request.getMethod().name()));
        awsRequest.setEndpoint(request.getURI());
        awsRequest.setContent(new ByteArrayInputStream(body));
        awsRequest.addHeader("Content-Type", "application/json");

        AWSCredentials awsCredentials = new BasicAWSCredentials(
                awsConfiguration.getAccessKey(),
                awsConfiguration.getSecretKey()
        );

        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName("lambda");
        signer.setRegionName(awsConfiguration.getRegion());
        signer.sign(awsRequest, awsCredentials);

        HttpRequest signedHttpRequest = convertToHttpRequest(awsRequest, request);

        return execution.execute(signedHttpRequest, body);
    }

    private HttpRequest convertToHttpRequest(DefaultRequest<?> awsRequest, HttpRequest originalRequest) {
        return new HttpRequest() {
            @Override
            public HttpMethod getMethod() {
                return originalRequest.getMethod();
            }

            @Override
            public URI getURI() {
                return originalRequest.getURI();
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                awsRequest.getHeaders().forEach(headers::add);
                return headers;
            }
        };
    }

}
