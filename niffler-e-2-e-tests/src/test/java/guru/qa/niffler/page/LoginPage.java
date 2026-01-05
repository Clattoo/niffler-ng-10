package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;

@ParametersAreNonnullByDefault
public class LoginPage extends BasePage<LoginPage> {
  private final SelenideElement usernameInput,
          passwordInput,
          submitBtn,
          createNewAccountButton;

  public final SelenideElement loginForm,
          errorMessageContainer;

  public LoginPage(SelenideDriver driver) {
    super(driver);

    this.usernameInput = driver.$("#username");
    this.passwordInput = driver.$("#password");
    this.submitBtn = driver.$("#login-button");
    this.createNewAccountButton = driver.$("#register-button");
    this.loginForm = driver.$("#login-form");
    this.errorMessageContainer = driver.$(".form__error");

  }

  public LoginPage() {
    this.usernameInput = Selenide.$("#username");
    this.passwordInput = Selenide.$("#password");
    this.submitBtn = Selenide.$("#login-button");
    this.createNewAccountButton = Selenide.$("#register-button");
    this.loginForm = Selenide.$("#login-form");
    this.errorMessageContainer = Selenide.$(".form__error");

  }

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
