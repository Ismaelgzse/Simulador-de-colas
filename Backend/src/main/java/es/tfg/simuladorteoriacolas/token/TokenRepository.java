package es.tfg.simuladorteoriacolas.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Integer> {

    @Query(value = """
      select token from Token token, User user
      where user.id=token.user.id and user.id = :id and (token.expired = false or token.revoked = false)\s
      """)
    List<Token> findAllValidTokens(Integer id);

    Optional<Token> findByToken(String token);
}
