package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.Bubble;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.component.Header;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
    @User(
            spendings = {
                    @Spending(
                            category = "Category 1",
                            amount = 1000,
                            currency = CurrencyValues.RUB,
                            description = "Description 1"
                    ),
                    @Spending(
                            category = "Category 2",
                            amount = 2000,
                            currency = CurrencyValues.RUB,
                            description = "Description 2"
                    ),
                    @Spending(
                            category = "Category 3",
                            amount = 500,
                            currency = CurrencyValues.RUB,
                            description = "Description 3"
                    ),
                    @Spending(
                            category = "Category 4",
                            amount = 1500,
                            currency = CurrencyValues.RUB,
                            description = "Description 4"
                    )
            }
    )
    @Test
    @DisplayName("Проверка бабблов статистики в точном порядке")
    public void checkStatBubblesExactOrder(UserJson user) {
       Selenide.open(CFG.frontUrl(), LoginPage.class)
               .login(user.getUsername(), user.getTestData().password())
               .assertStatBubble(
                new Bubble(Color.YELLOW, "Category 2 2000 ₽"),
                new Bubble(Color.GREEN, "Category 4 1500 ₽"),
                new Bubble(Color.BLUE100, "Category 1 1000 ₽"),
                new Bubble(Color.ORANGE, "Category 3 500 ₽")
        );
    }

    @User(
            spendings = {
                    @Spending(
                            category = "Cat A",
                            amount = 2500,
                            currency = CurrencyValues.RUB,
                            description = "Desc A"
                    ),
                    @Spending(
                            category = "Cat B",
                            amount = 1500,
                            currency = CurrencyValues.RUB,
                            description = "Desc B"
                    )
            }
    )
    @Test
    @DisplayName("Проверка бабблов статистики в любом порядке")
    public void checkStatBubblesAnyOrder(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.getUsername(), user.getTestData().password())
                .assertStatBubbleInAnyOrder(
                new Bubble(Color.GREEN, "Cat B 1500 ₽"),
                new Bubble(Color.YELLOW, "Cat A 2500 ₽")
        );
    }

    @User(
            spendings = {
                    @Spending(
                            category = "Alpha",
                            amount = 300,
                            currency = CurrencyValues.RUB,
                            description = "Desc Alpha"
                    ),
                    @Spending(
                            category = "Beta",
                            amount = 700,
                            currency = CurrencyValues.RUB,
                            description = "Desc Beta"
                    ),
                    @Spending(
                            category = "Gamma",
                            amount = 1200,
                            currency = CurrencyValues.RUB,
                            description = "Desc Gamma"
                    )
            }
    )
    @Test
    @DisplayName("Проверка наличия конкретных бабблов статистики")
    public void checkStatBubblesContains(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.getUsername(), user.getTestData().password())
                .assertStatBubbleContains(
                new Bubble(Color.GREEN, "Beta 700 ₽"),
                new Bubble(Color.YELLOW, "Gamma 1200 ₽")
        );
    }
}
