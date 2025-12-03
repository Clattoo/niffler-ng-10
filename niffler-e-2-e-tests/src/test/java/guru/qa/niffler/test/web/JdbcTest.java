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
                                "test-5.2",
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
                new UserJson(
                        null,
                        "Clatto_Test_2",
                        "Matthew",
                        "Dyson",
                        "Matthew Dyson",
                        CurrencyValues.RUB,
                        "123467890",
                        "123467890"
                )
        );
        System.out.println(user);
    }

    @Test
    public void successTransactionTest() {
        UserDbClient dbClient = new UserDbClient();
        UserJson user = dbClient.createUser(
                new UserJson(
                        null,
                        "Jdbc_1",
                        "Petr",
                        "Petrovich",
                        "test",
                        CurrencyValues.RUB,
                        "fwaf",
                        "nfdjsnj"
                )
        );
        System.out.println(user);
    }
}
