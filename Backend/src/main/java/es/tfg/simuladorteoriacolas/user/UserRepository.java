package es.tfg.simuladorteoriacolas.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByNickname(String nick);
}
