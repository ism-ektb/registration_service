package aleksey.registration.client.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdDateTime;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocationDto location;
    private Long ownerId;
}
