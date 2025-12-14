package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.AuthUserEntity;
import guru.qa.niffler.data.entity.Authority;
import guru.qa.niffler.data.entity.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.RandomDataUtils;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

public class UserDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    @Override
    public UserJson createUser(String username, String password) {
        return xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(username);
                    authUser.setPassword(pe.encode(password));
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);
                    authUser.setAuthorities(Arrays.stream(Authority.values()).map(
                                    e -> {
                                        AuthorityEntity ae = new AuthorityEntity();
                                        ae.setUser(authUser);
                                        ae.setAuthority(e);
                                        return ae;
                                    }
                            ).toList()
                    );
                    authUserRepository.create(authUser);

                    UserEntity userdataUser = new UserEntity();
                    userdataUser.setUsername(username);
                    userdataUser.setFirstname(RandomDataUtils.randomName());
                    userdataUser.setSurname(RandomDataUtils.randomSurname());
                    userdataUser.setFullname(String.format("%s %s", userdataUser.getFirstname(), userdataUser.getSurname()));
                    userdataUser.setCurrency(CurrencyValues.RUB);

                    return UserJson.fromEntity(
                            userdataUserRepository.create(userdataUser)
                    );
                }
        );
    }

    @Override
    public void createIncomeInvitations(UserJson targetUser, int count) {
        for (int i = 0; i < count; i++) {
            UserJson friend = createUser(RandomDataUtils.randomUsername(), "12345");
            userdataUserRepository.sendInvitation(UserEntity.fromJson(friend), UserEntity.fromJson(targetUser));
        }
    }

    @Override
    public void createOutcomeInvitations(UserJson targetUser, int count) {
        for (int i = 0; i < count; i++) {
            UserJson friend = createUser(RandomDataUtils.randomUsername(), "12345");
            userdataUserRepository.sendInvitation(UserEntity.fromJson(targetUser), UserEntity.fromJson(friend));
        }
    }

    @Override
    public void createFriends(UserJson targetUser, int count) {
        for (int i = 0; i < count; i++) {
            UserJson friend = createUser(RandomDataUtils.randomUsername(), "12345");
            userdataUserRepository.addFriend(UserEntity.fromJson(targetUser), UserEntity.fromJson(friend));
        }
    }
}
