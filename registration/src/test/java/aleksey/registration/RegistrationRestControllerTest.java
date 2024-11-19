package aleksey.registration;


import aleksey.registration.controller.RegistrationController;
import aleksey.registration.dto.request.RegistrationRequestCreate;
import aleksey.registration.dto.request.RegistrationRequestDelete;
import aleksey.registration.dto.request.RegistrationRequestUpdate;
import aleksey.registration.dto.response.RegistrationResponseCreate;
import aleksey.registration.dto.response.RegistrationResponseGet;
import aleksey.registration.dto.response.RegistrationResponseUpdate;
import aleksey.registration.service.RegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.stream.IntStream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(RegistrationController.class)
public class RegistrationRestControllerTest {


    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private RegistrationService service;


    @Test
    @SneakyThrows
    void createRegistrationTest() {
        var request = new RegistrationRequestCreate("name", "email@email.ru", "password");
        var response = new RegistrationResponseCreate(1L, "pwd");
        when(service.create(request, 1L)).thenReturn(response);
        mvc.perform(post("/registrations/1").contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(mapper.writeValueAsString(request))).andExpect(status().isOk());

    }


    @Test
    @SneakyThrows
    void updateRegistrationTest() {
        var request = new RegistrationRequestUpdate(1L, "pwd", "name", "email@email.ru", "password");
        var response = new RegistrationResponseUpdate("name", "email@email.ru", "password");
        when(service.update(request)).thenReturn(response);
        mvc.perform(patch("/registrations").contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(mapper.writeValueAsString(request))).andExpect(status().isOk());

    }

    @Test
    @SneakyThrows
    void getRegistrationTest() {
        var response = new RegistrationResponseGet("name", "email", "phone", 1L);
        when(service.get(1L)).thenReturn(response);
        mvc.perform(get("/registrations/1").contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())).andExpect(status().isOk());

    }


    @Test
    @SneakyThrows
    void getAllRegistrationTest() {
        var response = new RegistrationResponseGet("name", "email", "phone", 1L);
        when(service.getAll(1L, 1, 10)).thenReturn(
                IntStream.range(0, 5)
                        .mapToObj(o1 -> new RegistrationResponseGet("ev" + o1, "em@ml.ru" + o1, "pass" + o1, 1L))
                        .toList()
        );
        mvc.perform(get("/registrations?eventId=1&page=1&size=10").contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())).andExpect(status().isOk());

    }

    @Test
    @SneakyThrows
    void deleteRegistrationTest() {
        var request = new RegistrationRequestDelete(1L, "pwd");
        mvc.perform(delete("/registrations").contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8.name()).content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());

    }


}
