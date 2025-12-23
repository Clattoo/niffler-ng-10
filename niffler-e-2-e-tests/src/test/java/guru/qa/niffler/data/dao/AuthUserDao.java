package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.AuthUserEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface AuthUserDao {
    AuthUserEntity create(AuthUserEntity user);

    @Nonnull
    AuthUserEntity updateUser(AuthUserEntity user);

    @Nonnull
    Optional<AuthUserEntity> findById(UUID id);

    @Nonnull
    List<AuthUserEntity> findAll();

    @Nonnull
    Optional<AuthUserEntity> findByUsername(String username);

    void delete(AuthUserEntity user);
}
