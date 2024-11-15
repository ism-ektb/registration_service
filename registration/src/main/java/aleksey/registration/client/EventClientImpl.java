package aleksey.registration.client;

import aleksey.registration.client.dto.EventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@RequiredArgsConstructor
public class EventClientImpl implements EventClient {

    private final WebClient webClient;

    public Optional<EventDto> getEvent(Long id) {
        return Optional.ofNullable(
                webClient.get().uri("/events/{id}", id)
                        .retrieve()
                        .bodyToMono(EventDto.class)
                        .block()
        );
    }

}
