package aleksey.registration.service;

import aleksey.registration.client.EventClient;
import aleksey.registration.client.UserClient;
import aleksey.registration.client.dto.EventDto;
import aleksey.registration.client.dto.UserOutDto;
import aleksey.registration.client.dto.event.OrganizerDto;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static aleksey.registration.model.RegistrationState.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    @Autowired
    private UserClient userClient;
    @Autowired
    private EventClient eventClient;
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
            } else log.warn("Ошибка сохранения в User service");
        }
        try {
            eventClient.getEvent(1L, eventId);
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new NoFoundObjectException("Номер мероприятия не верен");
            } else {
                log.warn(e.status() + e.getMessage());
                throw new BaseRelationshipException("Невозможно проверить входные данные, " +
                        "повторите попытку");
            }
        }
        EventDto event = eventClient.getEvent(1L, eventId);
        if (event != null && !event.getRegistrationStatus().equals("открыта"))
            throw new NoFoundObjectException("Регистрация на мероприятие " + eventId + " не производится.");
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
        EventDto eventDto = new EventDto();
        try {
            eventDto = eventClient.getEvent(1L, registration.getEventId());
        } catch (FeignException e) {
            throw new BaseRelationshipException("Ошибка доступа к базе данных событий. Повторите попытку");
        }
        if (registration.getState() == APPROVED && eventDto.getStartDateTime().isBefore(LocalDateTime.now()))
            throw new BaseRelationshipException("Нельзя отменить заявку на уже начавшееся мероприятие");
        if (registration.getPassword().equals(registrationRequestDelete.getPassword())) {
            registrationRepository.deleteById(registrationRequestDelete.getId());
            List<Registration> registrations = registrationRepository
                    .findAllByEventIdAndStateOrderByCreatedDateTime(registration.getEventId(), WAITING);
            if (!registrations.isEmpty()) {
                Registration updatedRegistration = registrations.get(0);
                updatedRegistration.setState(PENDING);
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

    /**
     * Изменение статуса заявок организатором
     * @param userId Id организатора
     * @param status новый статус заявки
     * @param registrationIds Номера заявок
     * @param description Описание проблемы
     * @return
     */
    @Transactional
    public List<RegistrationResponsePatch> changeOfStateByResponsibleUser(long userId,
                                                                    RegistrationState status,
                                                                    List<Long> registrationIds,
                                                                    RegistrationRequestPatch description) {
        List<Registration> registrations = registrationRepository.findAllById(registrationIds);
        if (registrations.isEmpty()) throw new BaseRelationshipException("Номера заявок не валидны");
        long eventId = 0;
        for (int i = 0; i < registrations.size(); i++) {
            if (i == 0) eventId = registrations.get(i).getEventId();
            else {
                if (eventId != registrations.get(i).getEventId()) throw new BaseRelationshipException("Заявки на " +
                        "разные мероприятия");
            }
        }
        List<OrganizerDto> orgList = new ArrayList<>();
        try {
            orgList = eventClient.findOrganizer(1L, eventId);
        } catch (FeignException e) {
            throw new NoFoundObjectException("Нет доступа к списку организаторов");
        }
        List<Long> orgListId = orgList.stream().map(a -> a.getUserId()).collect(Collectors.toList());
        if (!(orgListId.contains(userId))) throw new BaseRelationshipException("Пользователь " +
                "не является организатором мероприятия");

        if (status.equals(CANCELED)) {
            if (description == null ||  description.getDescription().isEmpty()) {
                throw new RuntimeException("При отмене заявки нельзя не указывать причину");
            }
        }

        List<RegistrationResponsePatch> responses = new ArrayList<>();
        for (Registration registration : registrations) {
            registration.setState(status);
            switch (status) {
                case APPROVED -> responses.add(RegistrationResponsePatch.builder()
                        .registrationId(registration.getId())
                        .state(APPROVED)
                        .build());
                case WAITING -> responses.add(RegistrationResponsePatch.builder()
                        .registrationId(registration.getId())
                        .state(WAITING)
                        .build());
                case CANCELED -> responses.add(RegistrationResponsePatch.builder()
                        .registrationId(registration.getId())
                        .state(CANCELED)
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
