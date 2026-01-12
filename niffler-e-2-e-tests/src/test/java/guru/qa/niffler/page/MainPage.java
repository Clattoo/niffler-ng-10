package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.Bubble;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.page.component.Statistics;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {

    public final SpendingTable spendingTableComponent = new SpendingTable();
    public final Statistics statComponent = new Statistics();

    public final ElementsCollection tableRows = $$("#spendings tr");
    public final SelenideElement spendingTable = $("#spendings"),
            statisticsField = $("#stat"),
            personIcon = $("[data-testid='PersonIcon']"),
            profileLink = $("a[href='/profile']"),
            friendsLink = $("a[href='/people/friends']"),
            allPeopleLink = $("a[href='/people/all']"),
            searchInput = $("input[aria-label='search']");
    private final SelenideElement menuBtn = $("button[aria-label='Menu']");
    private final ElementsCollection menuOptions = $$("li a");
    private final SelenideElement categoryName = $("#legend-container");
    private final ElementsCollection statisticsLegend = $$("div[id='legend-container'] ul");

    @Step("Проверить загрузку главной страницы")
    public MainPage checkThatPageLoaded() {
        spendingTable.should(visible);
        statisticsField.should(visible);
        return this;
    }

    @Step("Переход на страницу редактирования spending")
    public EditSpendingPage editSpending(String description) {
        spendingTable.$$("tbody tr").find(text(description)).$$("td").get(5).click();
        return new EditSpendingPage();
    }

    @Step("Проверить наличие '{description}' в таблице")
    public MainPage checkThatTableContains(String description) {
        search(description);
        tableRows.find(text(description)).should(visible);
        return this;
    }

    @Step("Открыть раздел 'Profile'")
    public ProfilePage openProfile() {
        return header.toProfilePage();
    }

    @Step("Открыть раздел 'List of Friends'")
    public FriendsPage openListOfFriends() {
        personIcon.click();
        friendsLink.click();
        return new FriendsPage();
    }

    @Step("Открыть раздел 'Friends'")
    public FriendsPage openFriendsPage() {
        menuBtn.click();
        menuOptions.find(text("Friends")).click();
        return new FriendsPage();
    }

    @Step("Открыть раздел 'All People'")
    public FriendsPage openAllPeoplePage() {
        menuBtn.click();
        menuOptions.find(text("Friends")).click();
        allPeopleLink.click();
        return new FriendsPage();
    }

    @Step("Открыть страницу добавления нового spending")
    public EditSpendingPage openAddSpendingPage() {
        return header.addSpendingPage();
    }

    private MainPage search(String inputText) {
        searchInput.setValue(inputText).pressEnter();
        return this;
    }

    @Step("Удалить spending {categoryName} из таблицы пользователя")
    public MainPage deleteSpendingFromTable(String categoryName) {
        spendingTableComponent.deleteSpending(categoryName);
        return this;
    }

    @Step("Проверить количество записей в таблице spendings")
    public MainPage checkSpendingsTableSize(int expectedSize) {
        spendingTableComponent.checkTableSize(expectedSize);
        return this;
    }

    @Step("Проверить кол-во категорий в легенде под статистикой равно {count}")
    public MainPage checkLegendCount(int count) {
        Objects.equals(statisticsLegend.size(), count);
        return this;
    }

    @Step("Проверить изменение колеса статистики")
    public MainPage checkCategoryUpdate(String expectedCategory) {
        categoryName.$$("li").findBy(text(expectedCategory)).shouldBe(visible);
        return this;
    }

    @Step("Сравнение скриншотов профиля")
    public void assertStatisticsChartScreenshot(BufferedImage expected) throws IOException {
        Selenide.sleep(2000);
        BufferedImage actual = ImageIO.read($("canvas[role='img']").screenshot());
        ScreenDiffResult diffResult = new ScreenDiffResult(expected, actual);
        assertFalse(diffResult.getAsBoolean(), "Скриншоты отличаются");
    }

    @Step("Сравнение списка категорий под статистикой: {bubbles}")
    public MainPage assertStatBubble(Bubble... bubbles) {
        statComponent.checkBubbles(bubbles);
        return this;
    }

    @Step("Сравнение списка категорий под статистикой в любом порядке: {bubbles}")
    public MainPage assertStatBubbleInAnyOrder(Bubble... bubbles) {
        statComponent.checkBubblesInAnyOrder(bubbles);
        return this;
    }

    @Step("Проверить наличие категорий под статистикой: {bubbles}")
    public MainPage assertStatBubbleContains(Bubble... bubbles) {
        statComponent.checkBubblesContains(bubbles);
        return this;
    }

    @Step("Проверить корректность заполнения спендингов в таблице")
    public @Nonnull MainPage assertSpendingTable(List<SpendJson> spendings) {
        spendingTableComponent.checkSpendingTable(spendings);
        return this;
    }
}