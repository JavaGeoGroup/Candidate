package ge.project.demo.repository;

import ge.project.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
  Optional<User> findByEmailEquals(String email);
  Optional<User> findByPersonalNumberEquals(String personalNumber);

  Optional<User> findByKeycloakIdEquals(String keycloakId);
}
