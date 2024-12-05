package aleksey.registration.service;

import aleksey.registration.client.UserClient;
import aleksey.registration.client.dto.UserOutDto;
import aleksey.registration.dto.request.RegistrationRequestCreate;
import aleksey.registration.dto.request.RegistrationRequestDelete;
import aleksey.registration.dto.request.RegistrationRequestPatch;
import aleksey.registration.dto.request.RegistrationRequestUpdate;
import aleksey.registration.dto.response.*;
import aleksey.registration.exception.exception.BaseRelationshipException;
import aleksey.registration.exception.exception.NoFoundObjectException;
import aleksey.registration.mapper.RegistrationMapper;
import aleksey.registration.mapper.UserMapper;
import aleksey.registration.model.Registration;
import aleksey.registration.model.RegistrationState;
import aleksey.registration.repository.RegistrationRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    @Autowired
    private UserClient userClient;
    private final UserMapper userMapper;


    @Transactional
    public RegistrationResponseCreate create(final RegistrationRequestCreate registrationRequestCreate,
                                             final Long eventId) {
        Registration registration = RegistrationMapper.map(registrationRequestCreate, eventId);
        UserOutDto newUser = UserOutDto.builder().build();
        try {
            newUser = userClient.createUser(userMapper.registrationToUser(registration));
        } catch (FeignException e) {
            if (e.status() == 409) {
                log.warn("Пользователь с логином {} или email {} уже зарегистрирован в user-service",
                        registration.getUsername(), registration.getEmail());
            } else throw new RuntimeException("Ошибка сохранения в User service");
        }
        registration.setUserId(newUser.getId());
        Registration registration1 = registrationRepository.save(registration);
        return RegistrationMapper.mapCreate(
                registration1);
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
        Registration registration = registrationRepository.findById(registrationRequestDelete.getId())
                .orElseThrow(() -> new NoFoundObjectException("registration not found"));
        if (registration.getPassword().equals(registrationRequestDelete.getPassword())) {
            registrationRepository.deleteById(registrationRequestDelete.getId());
            List<Registration> registrations = registrationRepository
                    .findAllByEventIdAndStateOrderByCreatedDateTime(registration.getEventId(), RegistrationState.WAITING);
            if (!registrations.isEmpty()) {
                Registration updatedRegistration = registrations.get(0);
                updatedRegistration.setState(RegistrationState.PENDING);
                registrationRepository.save(updatedRegistration);
            }
        } else {
            throw new RuntimeException("The floggings don't match");
        }
        if (registration.getUserId() != null) {
            try {
                userClient.deleteUser(registration.getUserId(), registration.getPassword());
                log.info("Пользователь с userId= {} удален из user-service", registration.getId());
            } catch (BaseRelationshipException e) {
                log.warn("Пользователь с userId= {} не удален из user-service", registration.getId());
            }
        }
    }

    @Transactional
    public List<RegistrationResponsePatch> changeOfStateByResponsibleUser(long userId,
                                                                    RegistrationState status,
                                                                    List<Long> registrationIds,
                                                                    RegistrationRequestPatch description) {
        if (status.equals(RegistrationState.CANCELED)) {
            if (description == null ||  description.getDescription().isEmpty()) {
                throw new RuntimeException("При отмене заявки нельзя не указывать причину");
            }
        }
        List<Registration> registrations = registrationRepository.findAllById(registrationIds);
        List<RegistrationResponsePatch> responses = new ArrayList<>();
        for (Registration registration : registrations) {
            registration.setState(status);
            switch (status) {
                case APPROVED -> responses.add(RegistrationResponsePatch.builder()
                        .registrationId(registration.getId())
                        .state(RegistrationState.APPROVED)
                        .build());
                case WAITING -> responses.add(RegistrationResponsePatch.builder()
                        .registrationId(registration.getId())
                        .state(RegistrationState.WAITING)
                        .build());
                case CANCELED -> responses.add(RegistrationResponsePatch.builder()
                        .registrationId(registration.getId())
                        .state(RegistrationState.CANCELED)
                        .description(description.getDescription())
                        .build());
            }
        }
        registrationRepository.saveAll(registrations);
        return responses;
    }

    public List<RegistrationResponseGetStates> getWithStates(long userId, List<RegistrationState> states, Long eventId) {
        return registrationRepository.findAllByEventIdAndStateInOrderByCreatedDateTime(eventId, states).stream()
                .map(RegistrationMapper::mapGetStatuses)
                .collect(Collectors.toList());
    }

    public RegistrationCountedStates getWithCountedStates(long userId, Long eventId) {
        List<Registration> registrations = registrationRepository.findAllByEventId(eventId);
        if (registrations.isEmpty()) {
            throw new RuntimeException("Registrations not found on event with id=" + eventId);
        }
        RegistrationCountedStates registrationCountedStates = new RegistrationCountedStates();
        registrationCountedStates.setEventId(eventId);
        for (Registration registration : registrations) {
            switch (registration.getState()) {
                case PENDING -> registrationCountedStates
                        .setPendingStates(registrationCountedStates.getPendingStates() + 1);
                case APPROVED -> registrationCountedStates
                        .setApprovedStates(registrationCountedStates.getApprovedStates() + 1);
                case CANCELED -> registrationCountedStates
                        .setCanceledStates(registrationCountedStates.getCanceledStates() + 1);
                case WAITING -> registrationCountedStates
                        .setWaitingStates(registrationCountedStates.getWaitingStates() + 1);
            }
        }
        return registrationCountedStates;
    }
}
