package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.AuthUserEntity;

import java.util.Optional;
import java.util.UUID;

public interface AuthUserRepository {
    AuthUserEntity create(AuthUserEntity user);

    Optional<AuthUserEntity> findByUsername(String username);

    Optional<AuthUserEntity> findById(UUID id);

    void delete(AuthUserEntity user);
}
