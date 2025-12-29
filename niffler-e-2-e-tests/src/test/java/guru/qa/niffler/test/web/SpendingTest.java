package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class SpendingTest {

    private static final Config CFG = Config.getInstance();
    private static final String successfulCreatedSpendingMessage = "New spending is successfully created";

    @User(
            spendings = @Spending(
                    category = "Учеба",
                    amount = 89900,
                    currency = CurrencyValues.RUB,
                    description = "Обучение Niffler 2.0 юбилейный поток!"
            )
    )

    @Test
    void spendingDescriptionShouldBeEditedByTableAction(UserJson user) {
        final String spendDescription = user.getTestData().spendings().getFirst().description();
        final String newDescription = "Обучение Niffler Next Generation";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.getUsername(), user.getTestData().password())
                .editSpending(spendDescription)
                .setNewSpendingDescription(newDescription)
                .save()
                .checkThatTableContains(newDescription);
    }

    @Test
    @User(
            spendings = @Spending(
                    category = "Учеба",
                    amount = 89900,
                    currency = CurrencyValues.RUB,
                    description = "Обучение Niffler 2.0 юбилейный поток!"
            )
    )
    public void checkSpendingShouldBeInHistory(UserJson userJson) {
        String spendingDescription = userJson.getTestData().spendings().getFirst().description();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(userJson.getUsername(), userJson.getTestData().password())
                .checkThatPageLoaded()
                .checkThatTableContains(spendingDescription);
    }

    @Test
    @User
    public void addNewSpendingTest(UserJson user) {
        String newDescription = RandomDataUtils.randomSentence(1);

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.getUsername(), user.getTestData().password())
                .checkThatPageLoaded()
                .openAddSpendingPage()
                .setAmountSpending(RandomDataUtils.randomInteger())
                .setCategorySpending(RandomDataUtils.randomCategoryName())
                .setNewSpendingDescription(newDescription)
                .save()
                .checkSnackbarText(successfulCreatedSpendingMessage)
                .checkThatTableContains(newDescription);
    }
}
