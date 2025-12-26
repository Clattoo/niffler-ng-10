package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.TestMethodContextExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestMethodContextExtension.class)
public class ProfileTest {
    private static final Config CFG = Config.getInstance();

    @User
    @Test
    @DisplayName("Редактирование имени профиля")
    void editProfileNameTest(UserJson user) {
        String newName = RandomDataUtils.randomName();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.getUsername(), user.getTestData().password())
                .openProfile()
                .setName(newName)
                .clickSaveChanges()
                .returnToMainPage()
                .openProfile()
                .checkNameInputValue(newName);
    }
}
