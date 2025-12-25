package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class FriendsPage extends BasePage<FriendsPage> {
    public SelenideElement friendsTab = $("a[href='/people/friends']"),
            allPeopleTab = $("a[href='/people/all']"),
            emptylistOfFriends = $("#simple-tabpanel-friends"),
            listOfFriends = $("#friends"),
            listOfRequests = $("#requests"),
            searchInput = $("input[aria-label='search']"),
            friendRequestsTab = $("#requests"),
            myFriendsTable = $("#friends"),
            allPeopleList = $("#all"),
            declineFriendsInviteConfirmationButton = $$("div[role='dialog'] button").findBy(text("Decline"));
    private final ElementsCollection allPeopleTableRows = $$("tbody#all > tr");
    private final ElementsCollection allPeopleRows = allPeopleList.$$("tbody tr");
    private final ElementsCollection friendRequestsRow = friendRequestsTab.$$("tbody tr");
    private final ElementsCollection myFriendsRows = myFriendsTable.$$("tbody tr");

    @Step("Проверить наличие пустого списка друзей")
    public FriendsPage checkEmptyListOfFriends() {
        emptylistOfFriends.shouldBe(exist);
        return this;
    }

    @Step("Проверить наличие требуемого пользователя в списке друзей. Пользователь: {username}")
    public FriendsPage checkThatExactFriendExistInList(String username) {
        listOfFriends.shouldHave(text(username));
        return this;
    }

    @Step("Проверить статус инвайта")
    public FriendsPage checkFriendsInviteStatus(String username, String expectedStatus) {
        SelenideElement targetRow = allPeopleTableRows.findBy(text(username));
        targetRow.$("td:nth-child(2)").shouldHave(text(expectedStatus));
        return this;
    }

    @Step("Проверить наличие заявки в друзья")
    public FriendsPage checkFriendsRequest(String username) {
        listOfRequests.shouldHave(text(username));
        return this;
    }

    @Step("Проверить наличие каких-либо пользователей в списке друзей")
    public FriendsPage checkFriendsListIsNotEmpty() {
        myFriendsRows.first().shouldBe(visible);
        return this;
    }

    @Step("Проверить наличие исходящего приглашения в друзья")
    public FriendsPage checkOutcomeInvitationShouldBeVisible(String username) {
        allPeopleTab.click();
        allPeopleList.shouldBe(visible);
        search(username);
        allPeopleRows.findBy(text(username))
                .shouldBe(visible)
                .$(".MuiChip-label")
                .shouldHave(text("Waiting..."));
        return this;
    }

    @Step("Принять приглашение в друзья")
    public FriendsPage acceptFriendsInvitation() {
        SelenideElement row = friendRequestsRow.filter(visible).first();
        acceptButtonInRow(row).shouldBe(clickable).click();

        return this;
    }

    @Step("Отклонить приглашение в друзья")
    public FriendsPage declineFriendsInviteButton() {
        SelenideElement row = friendRequestsRow.filter(visible).first();
        declineButtonInRow(row).shouldBe(clickable).click();
        declineFriendsInviteConfirmationButton.click();
        return this;
    }


    private FriendsPage search(String keyword) {
        searchInput.setValue(keyword).pressEnter();
        return this;
    }

    private SelenideElement usernameInRow(SelenideElement row) {
        return row.$$("p").first();
    }

    private SelenideElement acceptButtonInRow(SelenideElement row) {
        return row.$$("button").findBy(text("Accept"));
    }

    private SelenideElement declineButtonInRow(SelenideElement row) {
        return row.$$("button").findBy(text("Decline"));
    }
}
