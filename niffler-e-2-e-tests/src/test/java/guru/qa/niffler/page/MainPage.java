package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
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
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {

    public final SpendingTable spendingTableComponent = new SpendingTable();
    public final Statistics statComponent = new Statistics();

    public final ElementsCollection tableRows;
    public final SelenideElement spendingTable,
            statisticsField,
            personIcon,
            profileLink,
            friendsLink,
            allPeopleLink,
            searchInput;
    private final SelenideElement menuBtn;
    private final ElementsCollection menuOptions;
    private final SelenideElement categoryName;
    private final ElementsCollection statisticsLegend;

    public MainPage(SelenideDriver driver) {
        super(driver);

        this.tableRows = driver.$$("#spendings tr");
        this.spendingTable = driver.$("#spendings");
        this.statisticsField = driver.$("#stat");
        this.personIcon = driver.$("[data-testid='PersonIcon']");
        this.profileLink = driver.$("a[href='/profile']");
        this.friendsLink = driver.$("a[href='/people/friends']");
        this.allPeopleLink = driver.$("a[href='/people/all']");
        this.searchInput = driver.$("input[aria-label='search']");
        this.menuBtn = driver.$("button[aria-label='Menu']");
        this.menuOptions = driver.$$("li a");
        this.categoryName = driver.$("#legend-container");
        this.statisticsLegend = driver.$$("div[id='legend-container'] ul");
    }

    public MainPage() {

        this.tableRows = Selenide.$$("#spendings tr");
        this.spendingTable = Selenide.$("#spendings");
        this.statisticsField = Selenide.$("#stat");
        this.personIcon = Selenide.$("[data-testid='PersonIcon']");
        this.profileLink = Selenide.$("a[href='/profile']");
        this.friendsLink = Selenide.$("a[href='/people/friends']");
        this.allPeopleLink = Selenide.$("a[href='/people/all']");
        this.searchInput = Selenide.$("input[aria-label='search']");
        this.menuBtn = Selenide.$("button[aria-label='Menu']");
        this.menuOptions = Selenide.$$("li a");
        this.categoryName = Selenide.$("#legend-container");
        this.statisticsLegend = Selenide.$$("div[id='legend-container'] ul");
    }

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
        BufferedImage actual = ImageIO.read(Selenide.$("canvas[role='img']").screenshot());
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