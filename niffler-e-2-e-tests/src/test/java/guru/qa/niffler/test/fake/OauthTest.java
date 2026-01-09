package guru.qa.niffler.test.fake;

import guru.qa.niffler.service.AuthApiClient;
import guru.qa.niffler.utils.OauthUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OauthTest {

    private final AuthApiClient authApiClient = new AuthApiClient();

    @Test
    void oauthFlowTest() throws IOException {
        String codeVerifier = OauthUtils.generateCodeVerifier();
        String codeChallenge = OauthUtils.generateCodeChallenge(codeVerifier);

        authApiClient.authorize(codeChallenge);
        String code = authApiClient.login("Mr_White", "white!Purple");
        String idToken = authApiClient.token(codeVerifier);

        assertNotNull(idToken, "id_token не должен быть null");
        Assertions.assertFalse(idToken.isEmpty(), "id_token не должен быть пустым");
        System.out.println("id_token = " + idToken);
    }
}
