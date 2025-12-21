package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class JdbcTest {

    @Test
    void txTest() {
        SpendDbClient spendDbClient = new SpendDbClient();
        SpendJson spendJson = spendDbClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "test-6.1",
                                "Clatto",
                                false
                        ),
                        CurrencyValues.RUB,
                        10000.0,
                        "5.2-test",
                        "Clatto"
                )
        );
        System.out.println(spendJson);
    }

    @Test
    void userDaoTest() {
        UserDbClient userDbClient = new UserDbClient();
        UserJson user = userDbClient.createUser(
                "test99", "1234566");
        System.out.println(user);
    }

    @Test
    public void successTransactionTest() {
        UserDbClient userDbClient = new UserDbClient();
        UserJson user = userDbClient.createUser(
                "test100", "qwerty123");
        System.out.println(user);
    }
}
