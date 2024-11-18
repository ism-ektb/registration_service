package aleksey.registration.service;

import aleksey.registration.client.EventClientImpl;
import aleksey.registration.dto.request.RegistrationRequestCreate;
import aleksey.registration.dto.request.RegistrationRequestDelete;
import aleksey.registration.dto.request.RegistrationRequestUpdate;
import aleksey.registration.dto.response.RegistrationResponseCreate;
import aleksey.registration.dto.response.RegistrationResponseGet;
import aleksey.registration.dto.response.RegistrationResponseUpdate;
import aleksey.registration.mapper.RegistrationMapper;
import aleksey.registration.model.Registration;
import aleksey.registration.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventClientImpl eventClient;


    @Transactional
    public RegistrationResponseCreate create(final RegistrationRequestCreate registrationRequestCreate,
                                             final Long eventId) {
        return RegistrationMapper.mapCreate(
                registrationRepository.save(RegistrationMapper.map(registrationRequestCreate, eventId))
        );
    }


    @Transactional
    public RegistrationResponseUpdate update(final RegistrationRequestUpdate update) {
        Registration registration = registrationRepository.findById(update.getRegistrationId())
                .orElseThrow(() -> new RuntimeException("registration not found"));
        if (
                registration.getPassword().equals(update.getPassword())
        ) {
            return RegistrationMapper.mapUpdate(registrationRepository.save(RegistrationMapper.map(update, registration)));
        } else {
            throw new RuntimeException("The floggings don't match");
        }
    }


    public RegistrationResponseGet get(final Long id) {
        return RegistrationMapper.map(
                registrationRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("registration not found"))
        );
    }


    public List<RegistrationResponseGet> getAll(final Long eventId,
                                                final Integer page,
                                                final Integer size) {
        return registrationRepository.findAllByEventId(eventId, PageRequest.of(page / size, size)).stream()
                .map(RegistrationMapper::map)
                .toList();
    }


    @Transactional
    public void delete(final RegistrationRequestDelete registrationRequestDelete) {
        if (
                registrationRepository.findById(registrationRequestDelete.getId())
                        .orElseThrow(() -> new RuntimeException("registration not found"))
                        .getPassword().equals(registrationRequestDelete.getPassword())
        ) {
            registrationRepository.deleteById(registrationRequestDelete.getId());
        } else {
            throw new RuntimeException("The floggings don't match");
        }


    }
}
