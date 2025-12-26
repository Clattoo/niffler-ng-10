package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage {

    public final ElementsCollection tableRows = $$("#spendings tr");
    public final SelenideElement spendingTable = $("#spendings"),
            statisticsField = $("#stat"),
            personIcon = $("[data-testid='PersonIcon']"),
            profileLink = $("a[href='/profile']"),
            friendsLink = $("a[href='/people/friends']"),
            allPeopleLink = $("a[href='/people/all']"),
            searchInput = $("input[aria-label='search']");
    private final Header header = new Header();
    private final SelenideElement menuBtn = $("button[aria-label='Menu']");
    private final ElementsCollection menuOptions = $$("li a");

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

    private MainPage search(String inputText) {
        searchInput.setValue(inputText).pressEnter();
        return this;
    }
}