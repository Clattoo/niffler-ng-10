package guru.qa.niffler.service;

import com.github.jknack.handlebars.internal.lang3.StringUtils;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.CodeInterceptor;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.utils.OauthUtils;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import retrofit2.Response;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@ParametersAreNonnullByDefault
public final class AuthApiClient extends RestClient {

    private final AuthApi authApi;

    private static final String RESPONSE_TYPE = "code";
    private static final String CLIENT_ID = "client";
    private static final String SCOPE = "openid";
    private static final String CODE_CHALLENGE_METHOD = "S256";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String REDIRECT_URL = CFG.frontUrl() + "authorized";

    public AuthApiClient() {
        super(CFG.authUrl(), true, new CodeInterceptor());
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

    public void authorize(String codeChallenge) throws IOException {
        authApi.authorize(
                RESPONSE_TYPE,
                CLIENT_ID,
                SCOPE,
                REDIRECT_URL,
                codeChallenge,
                CODE_CHALLENGE_METHOD
        ).execute();
    }

    public String login(String username, String password) throws IOException {
        var response = authApi.login(username,
                        password,
                        ThreadSafeCookieStore.INSTANCE.xsrfCookie())
                .execute();
        return StringUtils.substringAfter(response.raw().request().url().toString(), "code=");
    }

    public String token(String codeVerifier) throws IOException {
        var response = authApi.token(
                ApiLoginExtension.getCode(),
                REDIRECT_URL,
                CLIENT_ID,
                codeVerifier,
                GRANT_TYPE
        ).execute();
        return response.body().get("id_token").asText();
    }

    @SneakyThrows
    public String loginUser(String username, String password) {
        final String codeVerifier = OauthUtils.generateCodeVerifier();
        final String codeChallenge = OauthUtils.generateCodeChallenge(codeVerifier);

        authorize(codeChallenge);
        login(username, password);
        return token(codeVerifier);
    }
}
