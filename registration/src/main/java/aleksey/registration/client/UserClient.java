package aleksey.registration.client;

import aleksey.registration.client.dto.UserInDto;
import aleksey.registration.client.dto.UserOutDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "userClient", url = "http://51.250.41.207:8081")
public interface UserClient {
    @RequestMapping(method = RequestMethod.POST, value = "/users")
    UserOutDto createUser(@RequestBody UserInDto userInDto);

    @RequestMapping(method = RequestMethod.DELETE, value = "/users")
    void deleteUser(@RequestHeader long userId,
                    @RequestHeader String password);


}
