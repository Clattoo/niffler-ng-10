package guru.qa.niffler.test.rest;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;


@Order(1)
@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("Первый тест: пустой список юзеров перед всем тестами")
public class FirstTest {
    private final UserApiClient userApiClient = new UserApiClient();


    @Test
    @DisplayName("Проверка, что список пользователей пустой перед прохождением всех тестов")
    public void userListShouldNotBeEmpty() {
        List<UserJson> users = userApiClient.getAll("");
        assertTrue(
                users.isEmpty(),
                "Users list should be empty before all tests"
        );
    }
}
