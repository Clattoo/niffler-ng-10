package guru.qa.niffler.test.fake;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;

public class ApiExtensionTest {

    private static final Config CFG = Config.getInstance();

    @Test
    @User(incomeInvitations = 1, outcomeInvitations = 1, friends = 1,
            spendings = {@Spending(
                    category = "Test category 1",
                    amount = 213,
                    currency = CurrencyValues.RUB,
                    description = "Test description 1"
            ), @Spending(
                    category = "Test category 2",
                    amount = 3210,
                    currency = CurrencyValues.RUB,
                    description = "Test description 2"
            ),
            },
            categories = {@Category(
                    name = "Test ext",
                    archived = false
            )}
    )
    @ApiLogin
    public void printUserInfo(UserJson user) {
        Selenide.open(CFG.frontUrl(), MainPage.class);
        System.out.println("=== USER INFO ===");
        System.out.println("Username: " + user.getUsername());

        System.out.println("\n--- Friends ---");
        user.getTestData().friends().forEach(f ->
                System.out.println(f.getUsername() + " (" + f.getFriendshipStatus() + ")")
        );

        System.out.println("\n--- Income Invitations ---");
        user.getTestData().incomeInvitations().forEach(f ->
                System.out.println(f.getUsername() + " (" + f.getFriendshipStatus() + ")")
        );

        System.out.println("\n--- Outcome Invitations ---");
        user.getTestData().outcomeInvitations().forEach(f ->
                System.out.println(f.getUsername() + " (" + f.getFriendshipStatus() + ")")
        );

        System.out.println("\n--- Categories ---");
        user.getTestData().categories().forEach(c ->
                System.out.println(c.name() + " (archived: " + c.archived() + ")")
        );

        System.out.println("\n--- Spendings ---");
        user.getTestData().spendings().forEach(s ->
                System.out.println(
                        s.description() +
                                " | amount: " + s.amount() +
                                " | currency: " + s.currency() +
                                " | category: " + s.category()
                )
        );
    }
}
