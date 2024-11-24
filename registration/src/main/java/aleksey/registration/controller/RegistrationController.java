package aleksey.registration.controller;


import aleksey.registration.dto.request.RegistrationRequestCreate;
import aleksey.registration.dto.request.RegistrationRequestDelete;
import aleksey.registration.dto.request.RegistrationRequestUpdate;
import aleksey.registration.dto.response.RegistrationResponseCreate;
import aleksey.registration.dto.response.RegistrationResponseGet;
import aleksey.registration.dto.response.RegistrationResponseUpdate;
import aleksey.registration.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
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


}
