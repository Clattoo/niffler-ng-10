package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FriendsPage {
    public SelenideElement friendsTab = $("a[href='/people/friends']"),
            allPeopleTab = $("a[href='/people/all']"),
            emptylistOfFriends = $("#simple-tabpanel-friends"),
            listOfFriends = $("#friends"),
            listOfRequests = $("#requests"),
            searchInput = $("input[aria-label='search']"),
            friendRequestsTab = $("#requests"),
            myFriendsTable = $("#friends"),
            allPeopleList = $("#all"),
            acceptFriendsInviteButton = $(""),
            declineFriendsInviteButton = $("");
    private final ElementsCollection allPeopleTableRows = $$("tbody#all > tr");
    private final ElementsCollection allPeopleRows = allPeopleList.$$("tbody tr");
    private final ElementsCollection friendRequestsRow = friendRequestsTab.$$("tbody tr");
    private final ElementsCollection myFriendsRows = myFriendsTable.$$("tbody tr");

    public FriendsPage checkEmptyListOfFriends() {
        emptylistOfFriends.shouldBe(exist);
        return this;
    }

    public FriendsPage checkThatExactFriendExistInList(String username) {
        listOfFriends.shouldHave(text(username));
        return this;
    }

    public FriendsPage checkFriendsInviteStatus(String username, String expectedStatus) {
        SelenideElement targetRow = allPeopleTableRows.findBy(text(username));
        targetRow.$("td:nth-child(2)").shouldHave(text(expectedStatus));
        return this;
    }

    public FriendsPage checkFriendsRequest(String username) {
        listOfRequests.shouldHave(text(username));
        return this;
    }

    public FriendsPage checkFriendsListIsEmpty() {
        myFriendsRows.first().shouldNotBe(visible);
        return this;
    }

    public FriendsPage checkFriendsListIsNotEmpty() {
        myFriendsRows.first().shouldBe(visible);
        return this;
    }

    public FriendsPage checkIncomeInvitationShouldBeVisible(String username) {
        search(username);
        friendRequestsRow.findBy(text(username))
                .shouldBe(visible)
                .$("button[type='button']")
                .shouldHave(text("Accept"));
        return this;
    }

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

    public String acceptFriendsInvitation() {
        SelenideElement row = friendRequestsRow.filter(visible).first();
        String username = usernameInRow(row).text();
        acceptButtonInRow(row).shouldBe(clickable).click();

        return username;
    }

    public FriendsPage declineFriendsInviteButton() {
        SelenideElement row = friendRequestsRow.filter(visible).first();
        declineButtonInRow(row).shouldBe(clickable).click();

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
