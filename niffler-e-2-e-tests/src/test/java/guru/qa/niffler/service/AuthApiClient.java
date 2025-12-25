package guru.qa.niffler.service;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

@ParametersAreNonnullByDefault
public final class AuthApiClient extends RestClient {

    private final AuthApi authApi;

    public AuthApiClient() {
        super("https://auth.niffler-stage.qa.guru/", true);
        this.authApi = create(AuthApi.class);
    }

    @Step("Регистрация пользователя: {username}")
    public Response<Void> register(String username, String password) throws IOException {
        authApi.requestRegisterForm().execute();
        return authApi.register(
                username,
                password,
                password,
                ThreadSafeCookieStore.INSTANCE.xsrfCookie()
        ).execute();
    }
}
