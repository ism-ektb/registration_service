package aleksey.registration.repository;

import aleksey.registration.model.Registration;
import aleksey.registration.model.RegistrationState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findAllByEventId(Long eventId, Pageable pageable);

    List<Registration> findAllByEventIdAndStateOrderByCreatedDateTime(Long eventId, RegistrationState state);

    List<Registration> findAllByEventIdAndStateInOrderByCreatedDateTime(Long eventId, List<RegistrationState> states);

    List<Registration> findAllByEventId(Long eventId);
}
