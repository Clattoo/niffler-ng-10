package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;



public class RegistrationPage {

    private LoginPage loginPage;

    private final SelenideElement usernameInput = $("#username"),
            passwordInput = $("#password"),
            passwordSubmitInput = $("#passwordSubmit"),
            registerBtn = $("#register-button"),
            successSighInBtn = $(".form_sign-in"),
            errorRegistrationMessage = $(".form__error"),
            congratulationsMessage = $(".form__paragraph_success");

    @Step("Set username: '{username}'")
    public RegistrationPage setUsername(String username) {
        usernameInput.val(username);
        return this;
    }

    @Step("Set password: '{password}'")
    public RegistrationPage setPassword(String password) {
        passwordInput.val(password);
        return this;
    }

    @Step("Set confirmation password: '{password}'")
    public RegistrationPage setPasswordSubmit(String password) {
        passwordSubmitInput.val(password);
        return this;
    }

    @Step("Check congratulations message when registration was successful")
    public RegistrationPage checkCongratulationsMessage() {
        congratulationsMessage.shouldBe(visible);
        return this;
    }

    @Step("Proceed new user registration")
    public RegistrationPage proceedRegistration() {
        registerBtn.click();
        return this;
    }

    @Step("Fill registration forms on registration page")
    public RegistrationPage submitRegistration(String username, String password, String confirmPassword) {
        setUsername(username)
                .setPassword(password)
                .setPasswordSubmit(confirmPassword)
                .proceedRegistration();
        return this;
    }

    @Step("Check error message when passwords in both forms are incorrect")
    public RegistrationPage checkDifferentPasswordsErrorMessage(String errorMessage) {
        errorRegistrationMessage.shouldHave(text(errorMessage));
        return this;
    }

    @Step("Check error message when user already exists")
    public RegistrationPage checkExistingUserErrorMessage(String errorMessage) {
        errorRegistrationMessage.shouldHave(text(errorMessage));
        return this;
    }

    @Step("Check elements on registration page")
    public RegistrationPage checkRegistrationElements() {
        usernameInput.shouldBe(visible);
        passwordInput.shouldBe(visible);
        passwordSubmitInput.shouldBe(visible);
        registerBtn.shouldBe(visible);
        return this;
    }

    @Step("Finish registration and click 'Sign In' button")
    public RegistrationPage finishNewUserRegistration() {
        successSighInBtn.click();
        return this;
    }

}
