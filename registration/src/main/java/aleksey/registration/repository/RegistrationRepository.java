package aleksey.registration.repository;

import aleksey.registration.model.Registration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    Optional<Registration> findById(Long id);
    List<Registration> findAllByEventId(Long eventId, Pageable pageable);
}
