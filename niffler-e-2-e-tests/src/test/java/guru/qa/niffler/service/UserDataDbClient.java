package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.dao.impl.UserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.UserJson;

import java.util.Optional;
import java.util.UUID;

public class UserDataDbClient {

    private final UserDao userDao = new UserDaoJdbc();

    private static final Config CFG = Config.getInstance();

    public UserJson createUser(UserJson user) {
        UserEntity userEntity = UserEntity.fromJson(user);
        UserEntity createdUser = userDao.createUser(userEntity);
        return UserJson.fromEntity(createdUser);
    }

    public Optional<UserEntity> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return userDao.findById(id);
    }

    public Optional<UserEntity> findByUsername(String username) {
        if (username == null) {
            return Optional.empty();
        }
        return userDao.findByUsername(username);
    }

    public void deleteUser(UserJson user) {
        UserEntity userEntity = UserEntity.fromJson(user);
        userDao.delete(userEntity);
    }
}
