package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.component.Header;
import org.junit.jupiter.api.DisplayName;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ScreenshotTest {

    private static final Config CFG = Config.getInstance();

    @User
    @ScreenShotTest(
            value = "img/profile_icon.png"
    )
    @DisplayName("Проверка иконки профиля")
    void uploadNewProfilePictureShouldBeVisible(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.getUsername(), user.getTestData().password())
                .openProfile()
                .uploadProfileImage("img/profile_icon.png")
                .assertProfilePicScreenshot(expected);
    }

    @User(
            spendings = @Spending(
                    category = "Test chart without change",
                    amount = 2500,
                    currency = CurrencyValues.RUB,
                    description = "Test"
            )
    )
    @ScreenShotTest(
            value = "img/chart_without_change.png"
    )
    @DisplayName("Проверка диаграммы при добавлении нового spending без редактирования")
    void checkStatisticsChartWithoutChange(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.getUsername(), user.getTestData().password())
                .checkCategoryUpdate(user.getTestData().spendings().getFirst().category().name())
                .checkLegendCount(user.getTestData().spendings().size())
                .assertStatisticsChartScreenshot(expected);
    }

    @User(
            spendings = @Spending(
                    category = "Test chart for delete",
                    amount = 2500,
                    currency = CurrencyValues.RUB,
                    description = "Test chart for delete"
            )
    )
    @ScreenShotTest(
            value = "img/chart_after_delete.png"
    )
    @DisplayName("Проверка диаграммы после удаления добавленого spending")
    void checkStatisticsChartAfterDelete(UserJson user, BufferedImage expected) throws IOException {
        int legendCount = user.getTestData().spendings().size();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.getUsername(), user.getTestData().password())
                .checkCategoryUpdate(user.getTestData().spendings().getFirst().category().name())
                .checkLegendCount(legendCount)
                .deleteSpendingFromTable(user.getTestData().spendings().getFirst().category().name())
                .checkLegendCount(legendCount - 1)
                .assertStatisticsChartScreenshot(expected);
    }

    @User(
            spendings = {
                    @Spending(
                            category = "Test change category 1",
                            amount = 2500,
                            currency = CurrencyValues.RUB,
                            description = "Test 1"
                    ),
                    @Spending(
                            category = "Test change category 2",
                            amount = 1750,
                            currency = CurrencyValues.RUB,
                            description = "Test 2"
                    )
            }
    )
    @ScreenShotTest(
            value = "img/chart_after_archive_category.png"
    )
    @DisplayName("Проверка диаграммы при добавлении одной из категорий в archived")
    void checkStatisticsChartAfterArchiveCategory(UserJson user, BufferedImage expected) throws IOException {
        Header header = new Header();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.getUsername(), user.getTestData().password())
                .checkCategoryUpdate(user.getTestData().spendings().getFirst().category().name())
                .checkLegendCount(user.getTestData().spendings().size());
        header.toProfilePage()
                .archiveCategory(user.getTestData().spendings().getFirst().category().name())
                .returnToMainPage()
                .checkCategoryUpdate("Archived")
                .assertStatisticsChartScreenshot(expected);
    }

    @User(
            spendings = {
                    @Spending(
                            category = "Test change spending 1",
                            amount = 2500,
                            currency = CurrencyValues.RUB,
                            description = "Test 1"
                    ),
                    @Spending(
                            category = "Test change spending 2",
                            amount = 1750,
                            currency = CurrencyValues.RUB,
                            description = "Test 2"
                    )
            }
    )
    @ScreenShotTest(
            value = "img/chart_after_edit_spending.png"
    )
    @DisplayName("Проверка диаграммы при изменении одного из spending")
    void checkStatisticsChartAfterEditingOneOfSpending(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.getUsername(), user.getTestData().password())
                .checkCategoryUpdate(user.getTestData().spendings().getFirst().category().name())
                .checkLegendCount(user.getTestData().spendings().size())
                .editSpending(user.getTestData().spendings().getFirst().category().name())
                .setAmountSpending(5500)
                .save()
                .assertStatisticsChartScreenshot(expected);
    }
}
