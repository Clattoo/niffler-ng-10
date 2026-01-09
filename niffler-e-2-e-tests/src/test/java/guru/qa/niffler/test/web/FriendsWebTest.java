package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class FriendsWebTest {

    private static final Config CFG = Config.getInstance();

    private FriendsPage friendsPage;

    @BeforeEach
    void setUp() {
        friendsPage = new FriendsPage();
    }

    @Test
    @User(friends = 1)
    @ApiLogin
    @DisplayName("Должен отображаться список друзей")
    public void friendShouldBePresentInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), MainPage.class)
                .openFriendsPage()
                .checkFriendsListIsNotEmpty();
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Таблица друзей должна быть пустой")
    public void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(CFG.frontUrl(), MainPage.class)
                .openFriendsPage()
                .checkEmptyListOfFriends();
    }

    @Test
    @User(incomeInvitations = 2)
    @ApiLogin
    @DisplayName("Должен отображаться входящий запрос на добавление в друзья")
    public void incomeInvitationShouldBePresentInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), MainPage.class)
                .openFriendsPage()
                .checkFriendsRequest(user.getTestData().incomeInvitations().getFirst().getUsername());
    }

    @Test
    @User(outcomeInvitations = 1)
    @ApiLogin
    @DisplayName("Статус добавления в друзья должен быть в статусе Waiting...")
    public void outcomeInvitationShouldBePresentInAllPeoplesTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), MainPage.class)
                .openFriendsPage()
                .checkOutcomeInvitationShouldBeVisible(user.getTestData().outcomeInvitations().getFirst().getUsername());
    }

    @Test
    @User(incomeInvitations = 1)
    @ApiLogin
    @DisplayName("Добавление друга через входящий запрос на добавление в друзья")
    public void acceptIncomeInvitationInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), MainPage.class)
                .openFriendsPage();

        String invitationUsername = user.getTestData().incomeInvitations().getFirst().getUsername();
        friendsPage
                .checkFriendsRequest(invitationUsername)
                .acceptFriendsInvitation()
                .checkSnackbarText("Invitation of " + invitationUsername + " accepted")
                .checkThatExactFriendExistInList(invitationUsername);
    }

    @Test
    @User(incomeInvitations = 1)
    @ApiLogin
    @DisplayName("Отклонение входящей заявки в друзья")
    public void declineIncomeInvitationInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), MainPage.class)
                .openFriendsPage();

        String invitationUsername = user.getTestData().incomeInvitations().getFirst().getUsername();

        friendsPage.checkFriendsRequest(user.getTestData().incomeInvitations().getFirst().getUsername())
                .declineFriendsInviteButton()
                .checkSnackbarText("Invitation of " + invitationUsername + " is declined")
                .checkEmptyListOfFriends();
    }
}
