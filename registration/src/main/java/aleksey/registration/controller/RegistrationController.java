package aleksey.registration.controller;

import aleksey.registration.dto.request.RegistrationRequestCreate;
import aleksey.registration.dto.request.RegistrationRequestDelete;
import aleksey.registration.dto.request.RegistrationRequestPatch;
import aleksey.registration.dto.request.RegistrationRequestUpdate;
import aleksey.registration.dto.response.*;
import aleksey.registration.model.RegistrationState;
import aleksey.registration.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/{eventId}")
    public RegistrationResponseCreate create(@RequestBody @Valid RegistrationRequestCreate registrationRequestCreate,
                                             @PathVariable Long eventId) {

        return registrationService.create(registrationRequestCreate, eventId);
    }

    @PatchMapping
    public RegistrationResponseUpdate update(@RequestBody @Valid RegistrationRequestUpdate update) {

        return registrationService.update(update);
    }

    @GetMapping("/{id}")
    public RegistrationResponseGet get(@PathVariable Long id) {
        return registrationService.get(id);
    }

    @GetMapping
    public List<RegistrationResponseGet> getAll(@RequestParam Long eventId,
                                                @RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "100") Integer size) {
        return registrationService.getAll(eventId, page, size);
    }

    @DeleteMapping
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
