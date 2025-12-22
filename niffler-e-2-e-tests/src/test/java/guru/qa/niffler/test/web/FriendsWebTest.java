package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.TestMethodContextExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(TestMethodContextExtension.class)
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
    @User(friends = 1)
    @DisplayName("Должен отображаться список друзей")
    public void friendShouldBePresentInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.getUsername(), user.getTestData().password())
                .openFriendsPage()
                .checkFriendsListIsNotEmpty();
    }

    @Test
    @User
    @DisplayName("Таблица друзей должна быть пустой")
    public void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.getUsername(), user.getTestData().password())
                .openFriendsPage()
                .checkFriendsListIsEmpty();
    }

    @Test
    @User(incomeInvitations = 2)
    @DisplayName("Должен отображаться входящий запрос на добавление в друзья")
    public void incomeInvitationShouldBePresentInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.getUsername(), user.getTestData().password())
                .openFriendsPage()
                .checkIncomeInvitationShouldBeVisible(user.getTestData().incomeInvitations().getFirst().getUsername());
    }

    @Test
    @User(outcomeInvitations = 1)
    @DisplayName("Статус добавления в друзья должен быть в статусе Waiting...")
    public void outcomeInvitationShouldBePresentInAllPeoplesTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.getUsername(), user.getTestData().password())
                .openFriendsPage()
                .checkOutcomeInvitationShouldBeVisible(user.getTestData().outcomeInvitations().getFirst().getUsername());
    }
}
