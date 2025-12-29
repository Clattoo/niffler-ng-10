package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProfileTest {
    private static final Config CFG = Config.getInstance();
    private static final String successfulChangeInProfileMessage = "Profile successfully updated";

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
                .checkSnackbarText(successfulChangeInProfileMessage)
                .returnToMainPage()
                .openProfile()
                .checkNameInputValue(newName);
    }
}
