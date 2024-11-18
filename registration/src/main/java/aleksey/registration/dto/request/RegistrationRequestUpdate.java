package aleksey.registration.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequestUpdate {
    @NotNull
    private Long registrationId;
    @NotBlank
    private String password;
    private String username;
    @Email
    private String email;
    private String phone;
}
