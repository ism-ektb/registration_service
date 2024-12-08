package aleksey.registration.client.dto.event;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizerDto {
    @NotEmpty
    @NotNull
    private long userId;

    @NotEmpty
    @NotNull
    private Long eventId;

    @NotNull
    @NotEmpty
    private String roleName;
}
