package aleksey.registration.client;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
public class EventClientImpl implements EventClient {

    private final WebClient webClient;



}
