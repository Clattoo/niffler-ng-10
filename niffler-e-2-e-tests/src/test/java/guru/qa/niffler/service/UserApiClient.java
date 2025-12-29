package guru.qa.niffler.service;

import com.google.common.base.Stopwatch;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
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
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@ParametersAreNonnullByDefault
public final class UserApiClient extends RestClient implements UsersClient {

    private final UserdataApi userdataApi;
    private final AuthApi authApi;

    public UserApiClient() {
        super(CFG.userdataUrl());
        this.userdataApi = create(UserdataApi.class);
        this.authApi = create(AuthApi.class);
    }

    @Step("Создать пользователя {username}")
    @Override
    @SneakyThrows
    public UserJson createUser(String username, String password) {
        authApi.requestRegisterForm().execute();
        authApi.register(
                username,
                password,
                password,
                ThreadSafeCookieStore.INSTANCE.xsrfCookie()
        ).execute();

        Stopwatch sw = Stopwatch.createStarted();
        long maxWaitTime = 10_000;

        while (sw.elapsed(TimeUnit.MILLISECONDS) < maxWaitTime) {
            try {
                UserJson userJson = userdataApi.currentUser(username).execute().body();
                if (userJson != null && userJson.getId() != null) {
                    return userJson;
                } else {
                    Thread.sleep(100);
                }
            } catch (IOException e) {
                // Временно недоступен сервер — продолжаем ожидание
                System.out.println("Waiting for user creation due to temporary IO error: " + e.getMessage());
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        throw new AssertionError("User was not created in userdata within timeout");
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
