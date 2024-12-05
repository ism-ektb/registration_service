package aleksey.registration.controller;

import aleksey.registration.dto.request.RegistrationRequestCreate;
import aleksey.registration.dto.request.RegistrationRequestDelete;
import aleksey.registration.dto.request.RegistrationRequestPatch;
import aleksey.registration.dto.request.RegistrationRequestUpdate;
import aleksey.registration.dto.response.*;
import aleksey.registration.model.RegistrationState;
import aleksey.registration.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/registrations")
@Tag(name = "Регистрация на событие")
public class RegistrationController {

    private final RegistrationService registrationService;

    @Operation(description = "создание регистрации (в ответ возвращается номер заявки и четырехзначный пароль доступа)")
    @PostMapping("/{eventId}")
    public RegistrationResponseCreate create(@RequestBody @Valid RegistrationRequestCreate registrationRequestCreate,
                                             @PathVariable(name = "eventId") long eventId) {

        return registrationService.create(registrationRequestCreate, eventId);
    }

    @Operation(description = "обновление заявки (в dto приходит номер и пароль, обновление происходит, если они " +
            "введены корректно. Обновить можно только username, email, phone)")
    @PatchMapping
    public RegistrationResponseUpdate update(@RequestBody @Valid RegistrationRequestUpdate update) {

        return registrationService.update(update);
    }

    @Operation(description = "получение регистрации по id (не возвращается номер заявки и пароль)")
    @GetMapping("/{id}")
    public RegistrationResponseGet get(@PathVariable Long id) {
        return registrationService.get(id);
    }

    @Operation(description = "получение списка регистраций с пагинацией и с обязательным указанием id события" +
            " (не возвращается номер заявки и пароль)")
    @GetMapping
    public List<RegistrationResponseGet> getAll(@RequestParam long eventId,
                                                @RequestParam(name = "page", defaultValue = "1") Integer page,
                                                @RequestParam(name = "size", defaultValue = "100") Integer size) {
        return registrationService.getAll(eventId, page, size);
    }

    @DeleteMapping
    @Operation(description = "удаление регистрации (по связке номера + пароля из dto)")
    public void delete(@RequestBody RegistrationRequestDelete registrationRequestDelete) {
        registrationService.delete(registrationRequestDelete);
    }

    @PatchMapping("/states")
    public List<RegistrationResponsePatch> changeOfStateByResponsibleUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam("state") RegistrationState status,
                                                   @RequestParam("ids") List<Long> registrationIds,
                                                   @Valid @RequestBody(required = false) RegistrationRequestPatch description) {
        return registrationService.changeOfStateByResponsibleUser(userId,
                status,
                registrationIds,
                description);
    }

    @GetMapping("/states")
    public List<RegistrationResponseGetStates> getWithStates(@RequestHeader("X-Sharer-User-Id") long userId,
                                                             @RequestParam("states") List<RegistrationState> statuses,
                                                             @RequestParam("eventId") Long eventId) {
        return registrationService.getWithStates(userId, statuses, eventId);
    }

    @GetMapping("/states/{eventId}")
    public RegistrationCountedStates getWithCountedStates(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @PathVariable("eventId") Long eventId) {
        return registrationService.getWithCountedStates(userId, eventId);
    }
}
