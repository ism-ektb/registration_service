package aleksey.registration.dto.response;

import aleksey.registration.model.RegistrationState;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResponsePatch {
    private Long registrationId;
    private RegistrationState state;
    private String description;
}
