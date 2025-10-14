package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FriendsPage {
    public SelenideElement friendsTab = $("a[href='/people/friends']"),
            allPeopleTab = $("a[href='/people/all']"),
            emptylistOfFriends = $("#simple-tabpanel-friends"),
            listOfFriends = $("#friends"),
            listOfRequests = $("#requests");

    private ElementsCollection allPeopleTableRows = $$("tbody#all > tr");

    public FriendsPage checkEmptyListOfFriends() {
        emptylistOfFriends.shouldBe(exist);
        return this;
    }

    public FriendsPage checkThatExactFriendExistInList(String userName) {
        listOfFriends.shouldHave(text(userName));
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
}
