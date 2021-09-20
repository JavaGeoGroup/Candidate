package ge.project.demo.repository;

import ge.project.demo.entity.Token;
import ge.project.demo.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
  Optional<Token> findByValueEquals(UUID value);
  Optional<Token> findByValueEqualsAndTokenTypeEquals(UUID value, TokenType tokenType);
}
