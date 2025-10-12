package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

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
            allPeopleLink = $("a[href='/people/all']");

    public MainPage checkThatPageLoaded() {
        spendingTable.should(visible);
        statisticsField.should(visible);
        return this;
    }

    public EditSpendingPage editSpending(String description) {
        spendingTable.$$("tbody tr").find(text(description)).$$("td").get(5).click();
        return new EditSpendingPage();
    }

    public MainPage checkThatTableContains(String description) {
        tableRows.find(text(description)).should(visible);
        return this;
    }

    public ProfilePage editProfile() {
        personIcon.click();
        profileLink.click();
        return new ProfilePage();
    }

    public FriendsPage openListOfFriends() {
        personIcon.click();
        friendsLink.click();
        return new FriendsPage();
    }
}