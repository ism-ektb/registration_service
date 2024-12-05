package aleksey.registration.mapper;

import aleksey.registration.client.dto.UserInDto;
import aleksey.registration.model.Registration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "name", source = "registration.username")
    @Mapping(target = "aboutMe",  source = "registration.username")
    UserInDto registrationToUser(Registration registration);
}
