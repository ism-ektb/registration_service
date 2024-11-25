package aleksey.registration.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationCountedStates {
    private Long eventId;
    private int approvedStates;
    private int pendingStates;
    private int waitingStates;
    private int canceledStates;
}
