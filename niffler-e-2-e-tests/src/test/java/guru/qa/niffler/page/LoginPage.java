package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
  private final SelenideElement usernameInput = $("#username"),
          passwordInput = $("#password"),
          submitBtn = $("#login-button"),
          createNewAccountButton = $("#register-button");

  public final SelenideElement loginForm = $("#login-form"),
          errorMessageContainer = $(".form__error");

  @Step("Логин в систему пользователя под пользователем с логином {username} и паролем {password}")
  public MainPage login(String username, String password) {
    usernameInput.val(username);
    passwordInput.val(password);
    submitBtn.click();
    return new MainPage();
  }

  @Step("Переход на страницу регистрации нового пользователя")
  public RegistrationPage moveToRegistrationPage() {
    createNewAccountButton.click();
    return new RegistrationPage();
  }

  @Step("Проверить сообщение об ошибке при логине")
  public LoginPage checkErrorMessage(String errorMessage) {
    errorMessageContainer.shouldHave(text(errorMessage));
    return this;
  }
}
