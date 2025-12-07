package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserdataUserRepository {
    UserEntity create(UserEntity user);

    Optional<UserEntity> findById(UUID id);

    Optional<UserEntity> findByUsername(String username);

    void delete(UserEntity user);

    void AddIncomeInvitation(UserEntity requester, UserEntity addressee);

    void AddOutcomeInvitation(UserEntity requester, UserEntity addressee);

    void AddFriend(UserEntity requester, UserEntity addressee);
}
