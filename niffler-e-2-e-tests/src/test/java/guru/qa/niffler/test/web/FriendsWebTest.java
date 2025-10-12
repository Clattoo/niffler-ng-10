package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.*;

@ExtendWith({BrowserExtension.class, UsersQueueExtension.class})
public class FriendsWebTest {

    private static final Config CFG = Config.getInstance();

    private MainPage mainPage;
    private FriendsPage friendsPage;

    @BeforeEach
    void setUp() {
        mainPage = new MainPage();
        friendsPage = new FriendsPage();
    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    @DisplayName("Проверка наличия друга у пользователя")
    void friendsShouldBePresentInFriendsTable(@UserType(WITH_FRIEND) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .checkThatPageLoaded();

        mainPage.openListOfFriends();
        friendsPage.checkThatExactFriendExistInList(user.friend());
    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    @DisplayName("Проверка пустого списка друзей у пользователя")
    void friendsTableShouldBeEmptyForNewUser(@UserType(EMPTY) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .checkThatPageLoaded();

        mainPage.openListOfFriends();
        friendsPage.checkEmptyListOfFriends();
    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    @DisplayName("Проверка исходяшего приглашения в друзья")
    void incomeInvitationBePresentInFriendsTable(@UserType(WITH_INCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .checkThatPageLoaded();

        mainPage.openListOfFriends();
        friendsPage.allPeopleTab.click();
        friendsPage.checkFriendsInviteStatus(user.income(), "Waiting");
    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    @DisplayName("Проверка входящего приглашения в друзья")
    void outcomeInvitationBePresentInAllPeoplesTable(@UserType(WITH_OUTCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .checkThatPageLoaded();

        mainPage.openListOfFriends();
        friendsPage.checkFriendsRequest(user.outcome());
    }
}
