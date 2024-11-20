package aleksey.registration.mapper;

import aleksey.registration.dto.request.RegistrationRequestCreate;
import aleksey.registration.dto.request.RegistrationRequestUpdate;
import aleksey.registration.dto.response.RegistrationResponseCreate;
import aleksey.registration.dto.response.RegistrationResponseGet;
import aleksey.registration.dto.response.RegistrationResponseGetStates;
import aleksey.registration.dto.response.RegistrationResponseUpdate;
import aleksey.registration.model.Registration;
import org.apache.commons.lang3.RandomStringUtils;

public final class RegistrationMapper {

    public static Registration map(final RegistrationRequestCreate create, final Long eventId) {
        return Registration.builder()
                .username(create.getUsername())
                .email(create.getEmail())
                .phone(create.getPhone())
                .eventId(eventId)
                .password(RandomStringUtils.random(4, true, true))
                .build();
    }

    public static Registration map(final RegistrationRequestUpdate update, Registration reg) {
        if (update.getUsername() != null) {
            reg.setUsername(update.getUsername());
        }
        if (update.getEmail() != null) {
            reg.setEmail(update.getEmail());
        }
        if (update.getPhone() != null) {
            reg.setPhone(update.getPhone());
        }
        return reg;
    }

    public static RegistrationResponseGet map(final Registration registration) {
        return RegistrationResponseGet.builder()
                .username(registration.getUsername())
                .email(registration.getEmail())
                .phone(registration.getPhone())
                .eventId(registration.getEventId())
                .build();
    }

    public static RegistrationResponseCreate mapCreate(final Registration registration) {
        return RegistrationResponseCreate.builder()
                .id(registration.getId())
                .password(registration.getPassword())
                .build();
    }

    public static RegistrationResponseUpdate mapUpdate(final Registration registration) {
        return RegistrationResponseUpdate.builder()
                .username(registration.getUsername())
                .email(registration.getEmail())
                .phone(registration.getPhone())
                .build();
    }

    public static RegistrationResponseGetStates mapGetStatuses(final Registration registration) {
        return RegistrationResponseGetStates.builder()
                .id(registration.getId())
                .username(registration.getUsername())
                .email(registration.getEmail())
                .phone(registration.getPhone())
                .eventId(registration.getEventId())
                .state(registration.getState())
                .createdDateTime(registration.getCreatedDateTime())
                .build();
    }
}
