package aleksey.registration.config;

import aleksey.registration.client.EventClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {


    @Value("${client.uri}")
    private String baseUri;


    @Bean
    public EventClientImpl eventClient() {
        return new EventClientImpl(WebClient.builder()
                .baseUrl(this.baseUri)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build());
    }


}
