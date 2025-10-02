package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
  private final SelenideElement usernameInput = $("#username"),
          passwordInput = $("#password"),
          submitBtn = $("#login-button"),
          createNewAccountButton = $("#register-button");

  public final SelenideElement loginForm = $("#login-form"),
          errorMessageContainer = $(".form__error");

  public MainPage login(String username, String password) {
    usernameInput.val(username);
    passwordInput.val(password);
    submitBtn.click();
    return new MainPage();
  }

  public RegistrationPage moveToRegistrationPage() {
    createNewAccountButton.click();
    return new RegistrationPage();
  }

  public LoginPage checkErrorMessageWithIncorrectCredentials(String errorMessage) {
    errorMessageContainer.shouldHave(text(errorMessage));
    return this;
  }
}
