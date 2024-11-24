package aleksey.registration;


import aleksey.registration.dto.request.RegistrationRequestCreate;
import aleksey.registration.dto.request.RegistrationRequestDelete;
import aleksey.registration.dto.request.RegistrationRequestUpdate;
import aleksey.registration.service.RegistrationService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.stream.IntStream;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RegistrationServiceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15");


    @Autowired
    private RegistrationService registrationService;


    @Test
    @Order(1)
    void createRegistrationTest() {
        var create = this.registrationService.create(
                new RegistrationRequestCreate("name", "email@email", "89999999"), 1L

        );
        Assertions.assertNotNull(create);
    }

    @Test
    @Order(2)
    void updateRegistrationTest() {
        var create = this.registrationService.create(
                new RegistrationRequestCreate("name2", "email@email2", "899999992"), 1L
        );

        var update = this.registrationService.update(
                new RegistrationRequestUpdate(create.getId(), create.getPassword(), "updateName", "update@email.ru", "99update"));

        Assertions.assertEquals(update.getUsername(), "updateName");
        Assertions.assertEquals(update.getEmail(), "update@email.ru");
        Assertions.assertEquals(update.getPhone(), "99update");
    }

    @Test
    @Order(3)
    void getRegistrationTest() {
        var create = this.registrationService.create(
                new RegistrationRequestCreate("name3", "email@email3", "899999993"), 1L
        );

        var getRegistration = this.registrationService.get(create.getId());
        Assertions.assertNotNull(getRegistration);
        Assertions.assertEquals(getRegistration.getUsername(), "name3");
        Assertions.assertEquals(getRegistration.getEmail(), "email@email3");
        Assertions.assertEquals(getRegistration.getPhone(), "899999993");
    }


    @Test
    @Order(4)
    void getAllRegistrationsTest() {
        var createsRegistration = IntStream.range(0, 5)
                .mapToObj(i -> this.registrationService.create(
                        new RegistrationRequestCreate("name3" + i, "email@email3" + i, "899999993" + i), 2L
                ))
                .toList();
        var getAllRegistrations = registrationService.getAll(2L, 1, 10);
        Assertions.assertNotNull(getAllRegistrations);
        Assertions.assertEquals(getAllRegistrations.size(), 5);
    }

    @Test
    @Order(5)
    void deleteRegistrationTest() {
        var create = this.registrationService.create(
                new RegistrationRequestCreate("name3d", "email@email3d", "899999993d"), 1L
        );
        registrationService.delete(
                new RegistrationRequestDelete(create.getId(), create.getPassword())
        );

        Assertions.assertThrows(RuntimeException.class, () -> registrationService.get(create.getId()), "registration not found");
    }


}
