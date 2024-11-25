package aleksey.registration.dto.response;

import aleksey.registration.model.RegistrationState;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResponseGetStates {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private Long eventId;
    private RegistrationState state;
    private LocalDateTime createdDateTime;
}
