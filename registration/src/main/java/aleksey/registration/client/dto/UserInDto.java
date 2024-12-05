package aleksey.registration.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на регистрацию")
public class UserInDto {

    @Schema(description = "Имя", example = "Вова")
    @Size(min = 3, max = 50, message = "Имя пользователя должно содержать от 3 до 50 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    private String name;
    @Schema(description = "Электронная почта", example = "q@1.1")
    @Email(message = "Неверный формат электронной почты")
    @NotBlank(message = "Электронная почта пользователя не может быть пустой")
    private String email;
    @Schema(description = "Пароль", example = "password")
    @Size(min = 3, max = 50, message = "Пароль пользователя должно содержать от 5 до 50 символов")
    @NotBlank(message = "Пароль пользователя не может быть пустыми")
    private String password;
    @Schema(description = "Описание", example = "Очень крутой")
    @Size(min = 5, max = 200, message = "Пароль пользователя должно содержать от 5 до 200 символов")
    private String aboutMe;
}
