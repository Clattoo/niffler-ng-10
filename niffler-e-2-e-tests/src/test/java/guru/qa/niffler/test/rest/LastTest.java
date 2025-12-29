package guru.qa.niffler.test.rest;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserApiClient;
import io.qameta.allure.Step;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.Isolated;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Isolated
@DisplayName("Последний тест: проверка наличия пользователей в таблице")
public class LastTest {

    private final UserApiClient userApiClient = new UserApiClient();

    @Test
    @DisplayName("Проверка, что список пользователей не пустой после прохождения всех тестов")
    public void userListShouldNotBeEmpty() {
        List<UserJson> users = userApiClient.getAll("");
        assertFalse(
                users.isEmpty(),
                "Users list should not be empty after all tests"
        );
    }
}
