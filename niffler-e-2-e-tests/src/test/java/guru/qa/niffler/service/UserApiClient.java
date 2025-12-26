package guru.qa.niffler.service;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.UserExtension;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@ParametersAreNonnullByDefault
public class UserApiClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final CookieManager cm = new CookieManager(null, CookiePolicy.ACCEPT_ALL);

    private final Retrofit userdataRetrofit = new Retrofit.Builder()
            .baseUrl(CFG.userdataUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final Retrofit authRetrofit = new Retrofit.Builder()
            .baseUrl(CFG.authUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .client(new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(
                            cm
                    ))
                    .build())
            .build();

    private final UserdataApi userdataApi = userdataRetrofit.create(UserdataApi.class);
    private final AuthApi authApi = authRetrofit.create(AuthApi.class);

    @Step("Создать пользователя {username}")
    @Override
    @SneakyThrows
    public UserJson createUser(String username, String password) {
        authApi.requestRegisterForm().execute();
        authApi.register(
                username,
                password,
                password,
                cm.getCookieStore().getCookies()
                        .stream()
                        .filter(c -> c.getName().equals("XSRF-TOKEN"))
                        .findFirst()
                        .get()
                        .getValue()
        ).execute();

        var createdUser = new UserJson();
        createdUser.setUsername(username);
        createdUser.setCurrency(CurrencyValues.RUB);
        return createdUser;
    }

    @Step("Создать входящие приглашения для {targetUser.username}, количество: {count}")
    @Override
    public List<UserJson> createIncomeInvitations(UserJson targetUser, int count) {
        final List<UserJson> result = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final UserJson user = createUser(randomUsername(), UserExtension.DEFAULT_PASSWORD);
                result.add(user);
                userdataApi.sendInvitation(user.getUsername(), targetUser.getUsername());
            }
        }
        return result;
    }

    @Step("Создать исходящие приглашения для {targetUser.username}, количество: {count}")
    @Override
    public List<UserJson> createOutcomeInvitations(UserJson targetUser, int count) {
        final List<UserJson> result = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final UserJson user = createUser(randomUsername(), UserExtension.DEFAULT_PASSWORD);
                result.add(user);
                userdataApi.sendInvitation(targetUser.getUsername(), user.getUsername());
            }
        }
        return List.of();
    }

    @Step("Создать друзей для {targetUser.username}, количество: {count}")
    @Override
    public List<UserJson> createFriends(UserJson targetUser, int count) {
        final List<UserJson> result = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final UserJson user = createUser(randomUsername(), UserExtension.DEFAULT_PASSWORD);
                result.add(user);
                userdataApi.sendInvitation(targetUser.getUsername(), user.getUsername());
                userdataApi.acceptInvitation(user.getUsername(), targetUser.getUsername());
            }
        }
        return result;
    }
}
