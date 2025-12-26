package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.AuthorityEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface AuthAuthorityDao {
    void create(AuthorityEntity... authorities);

    @Nonnull
    List<AuthorityEntity> findAuthoritiesByUserId(UUID id);

    @Nonnull
    Optional<AuthorityEntity> findById(UUID id);

    void delete(AuthorityEntity authority);
}
