package aleksey.registration.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequestUpdate {
    @NotBlank
    private Long registrationId;
    @NotBlank
    private String password;
    private String username;
    private String email;
    private String phone;
}
