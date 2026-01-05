package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.converter.Browser;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Driver;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.NonStaticBrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

//@ExtendWith(BrowserExtension.class)
public class LoginTest {

  @RegisterExtension
  private final NonStaticBrowserExtension browserExtension = new NonStaticBrowserExtension();

  private static final Config CFG = Config.getInstance();

  @EnumSource(value = Browser.class, names = {"CHROME", "FIREFOX"})
  @ParameterizedTest
  @User
  void mainPageShouldBeDisplayedAfterSuccessLogin(@Driver SelenideDriver driver, UserJson user) {
    browserExtension.drivers().add(driver);
    driver.open(CFG.frontUrl());
    new LoginPage(driver)
        .login(user.getUsername(), user.getTestData().password());
    new MainPage(driver)
            .checkThatPageLoaded();
  }
}
