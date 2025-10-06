package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.RegistrationPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.utils.RandomDataUtils.getRandomPassword;
import static guru.qa.niffler.utils.RandomDataUtils.getRandomUserName;

@ExtendWith(BrowserExtension.class)
public class RegistrationTest {

    private static final Config CFG = Config.getInstance();
    private LoginPage loginPage;
    private MainPage mainPage;
    private RegistrationPage registrationPage;
    String existingUsername = "test";
    String username = getRandomUserName();
    String password = getRandomPassword();
    String differentPassword = getRandomPassword();
    String differentPasswordsErrorText = "Passwords should be equal";
    String userAlreadyExistsErrorText = "Username `" + existingUsername + "` already exists";
    String loginWithIncorrectCredentialsErrorText = "Неверные учетные данные пользователя";

    @BeforeEach
    void setUp() {
        loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
        registrationPage = new RegistrationPage();
        mainPage = new MainPage();
    }

    @Test
    @DisplayName("Регистрация нового пользователя")
    void shouldRegisterNewUserTest() {
        loginPage.moveToRegistrationPage();
        registrationPage.checkRegistrationElements()
                .submitRegistration(username, password, password)
                .checkCongratulationsMessage();
    }

    @Test
    @DisplayName("Главная страница должна отображаться после успешного логина")
    void mainPageShouldBeDisplayAfterSuccessLoginTest() {
        loginPage.moveToRegistrationPage();
        registrationPage.checkRegistrationElements()
                .submitRegistration(username, password, password)
                .finishNewUserRegistration();
        loginPage.login(username, password);
        mainPage.checkThatPageLoaded();
    }

    @Test
    @DisplayName("Запрет регистрации пользователя под существующим логином")
    void shouldNotRegisterUserWithExistingUsernameTest() {
        loginPage.moveToRegistrationPage();
        registrationPage.checkRegistrationElements()
                .submitRegistration(existingUsername, password, password)
                .checkExistingUserErrorMessage(userAlreadyExistsErrorText);
    }

    @Test
    @DisplayName("Ошибка при несовпадении паролей во время регистрации нового пользователя")
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqualTest() {
        loginPage.moveToRegistrationPage();
        registrationPage.checkRegistrationElements()
                .submitRegistration(username, password, differentPassword)
                .checkDifferentPasswordsErrorMessage(differentPasswordsErrorText);
    }

    @Test
    @DisplayName("Пользователь остается на странице логина после попытки входа с неверными данными")
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        loginPage.moveToRegistrationPage();
        registrationPage.checkRegistrationElements()
                .submitRegistration(username, password, password)
                .finishNewUserRegistration();
        loginPage.login(username, differentPassword);
        loginPage.checkErrorMessageWithIncorrectCredentials(loginWithIncorrectCredentialsErrorText);
    }
}
