package aleksey.registration.client;

import aleksey.registration.client.dto.EventDto;
import aleksey.registration.client.dto.event.OrganizerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "eventClient", url = "${event_service}")
public interface EventClient {
    @RequestMapping(method = RequestMethod.GET, value = "/events/{eventId}")
    EventDto getEvent(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("eventId") Long eventId);

    @RequestMapping(method = RequestMethod.GET, value = "/events/{eventId}/organizers")
    List<OrganizerDto> findOrganizer(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable("eventId") long eventId);


}
