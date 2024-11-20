package aleksey.registration;


import aleksey.registration.dto.request.RegistrationRequestCreate;
import aleksey.registration.dto.request.RegistrationRequestDelete;
import aleksey.registration.dto.request.RegistrationRequestPatch;
import aleksey.registration.dto.request.RegistrationRequestUpdate;
import aleksey.registration.dto.response.RegistrationCountedStates;
import aleksey.registration.dto.response.RegistrationResponseGetStates;
import aleksey.registration.dto.response.RegistrationResponsePatch;
import aleksey.registration.model.Registration;
import aleksey.registration.model.RegistrationState;
import aleksey.registration.service.RegistrationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RegistrationServiceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15");


    @Autowired
    private RegistrationService registrationService;
    private final EntityManager em;


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

    @Test
    void changeState() {
        Registration registration = Registration.builder()
                .username("Ivan")
                .email("aaaa@ya.ru")
                .phone("bbbbbb")
                .eventId(1L)
                .password("QoDh")
                .state(RegistrationState.PENDING)
                .createdDateTime(LocalDateTime.now())
                .build();

        em.persist(registration);
        em.flush();

        TypedQuery<Registration> registrationQuery = em
                .createQuery("select r from Registration as r where r.username = :username", Registration.class);
        Registration registration1 = registrationQuery.setParameter("username", registration.getUsername())
                .getSingleResult();


        List<RegistrationResponsePatch> responses = registrationService.changeOfStateByResponsibleUser(1,
                RegistrationState.CANCELED,
                Collections.singletonList(registration1.getId()),
                new RegistrationRequestPatch("Вам отказано в доступе на мероприятие"));

        assertThat(responses.get(0).getRegistrationId(), equalTo(registration1.getId()));
        assertThat(responses.get(0).getState(), equalTo(registration.getState()));
        assertThat(responses.get(0).getDescription(), equalTo("Вам отказано в доступе на мероприятие"));
    }

    @Test
    void getWithStates() {
        Registration registration = Registration.builder()
                .username("Ivan")
                .email("aaaa@ya.ru")
                .phone("bbbbbb")
                .eventId(1L)
                .password("QoDh")
                .state(RegistrationState.PENDING)
                .createdDateTime(LocalDateTime.now())
                .build();

        em.persist(registration);
        em.flush();

        List<RegistrationResponseGetStates> registrations = registrationService.getWithStates(1L,
                Collections.singletonList(RegistrationState.PENDING), 1L);

        assertThat(registrations, notNullValue());
    }

    @Test
    void getWithCountedStates() {
        Registration registration = Registration.builder()
                .username("Ivan")
                .email("aaaa@ya.ru")
                .phone("bbbbbb")
                .eventId(1L)
                .password("QoDh")
                .state(RegistrationState.PENDING)
                .createdDateTime(LocalDateTime.now())
                .build();
        Registration registration1 = Registration.builder()
                .username("Bobo")
                .email("bobo@ya.ru")
                .phone("cccccc")
                .eventId(1L)
                .password("6666")
                .state(RegistrationState.APPROVED)
                .createdDateTime(LocalDateTime.now())
                .build();
        Registration registration2 = Registration.builder()
                .username("Caca")
                .email("caca@ya.ru")
                .phone("llllll")
                .eventId(1L)
                .password("7777")
                .state(RegistrationState.CANCELED)
                .createdDateTime(LocalDateTime.now())
                .build();
        Registration registration3 = Registration.builder()
                .username("jojo")
                .email("jojo@ya.ru")
                .phone("jojojo")
                .eventId(1L)
                .password("1111")
                .state(RegistrationState.WAITING)
                .createdDateTime(LocalDateTime.now())
                .build();

        em.persist(registration);
        em.persist(registration1);
        em.persist(registration2);
        em.persist(registration3);
        em.flush();

        RegistrationCountedStates registrationCountedStates = registrationService
                .getWithCountedStates(1L, 1L);

        assertThat(registrationCountedStates.getEventId(), equalTo(1L));
        assertThat(registrationCountedStates.getPendingStates(), greaterThan(0));
        assertThat(registrationCountedStates.getApprovedStates(), greaterThan(0));
        assertThat(registrationCountedStates.getCanceledStates(), greaterThan(0));
        assertThat(registrationCountedStates.getWaitingStates(), greaterThan(0));
    }
}
