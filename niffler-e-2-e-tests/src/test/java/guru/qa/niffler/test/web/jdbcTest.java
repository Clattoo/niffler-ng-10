package guru.qa.niffler.test.web;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserDataDbClient;
import org.junit.jupiter.api.Test;

import java.util.*;

public class jdbcTest {

    @Test
    void createSpendTest() {
        SpendDbClient client = new SpendDbClient();

        SpendJson spend = client.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "test-4.2",
                                "Clatto",
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "4.2-new-method",
                        "Clatto"
                )
        );

        System.out.println(spend);
    }

    @Test
    void test1() {
        SpendDbClient client = new SpendDbClient();

        CategoryJson testCategory = new CategoryJson(
                null,
                "Учеба",
                "Clatto",
                false
        );

        Optional<CategoryJson> found = client.findCategoryByUsernameAndCategoryName(testCategory);

        System.out.println(found);
    }

    @Test
    void findSpendByIdTest() {
        SpendDbClient client = new SpendDbClient();
        UUID spendId = UUID.fromString("e4070728-f45b-4521-bc19-309654890ac0");

        Optional<SpendEntity> spend = client.findSpendById(spendId);

        spend.ifPresent(s -> System.out.println(
                String.format(
                        "ID: %s, Username: %s, Date: %s, Currency: %s, Amount: %s, Description: %s, " +
                                "Category ID: %s, Category Name: %s, Category Username: %s, Category Archived: %s",
                        s.getId(),
                        s.getUsername(),
                        s.getSpendDate(),
                        s.getCurrency(),
                        s.getAmount(),
                        s.getDescription(),
                        s.getCategory() != null ? s.getCategory().getId() : null,
                        s.getCategory() != null ? s.getCategory().getName() : null,
                        s.getCategory() != null ? s.getCategory().getUsername() : null,
                        s.getCategory() != null && s.getCategory().isArchived()
                )
        ));
    }

    @Test
    void testFindAllSpendsByUsername() {
        SpendDbClient client = new SpendDbClient();

        // Если метод возвращает список
        List<SpendJson> allSpends = client.findAllSpendsByUsername("Clatto");
        allSpends.forEach(System.out::println);
    }

    @Test
    void deleteSpendTest() {
        SpendDbClient client = new SpendDbClient();

        UUID spendId = UUID.fromString("2552c688-c17e-4d0c-ad9b-1b64000001cc");


        SpendJson spendToDelete = new SpendJson(
                spendId,
                null,
                null,
                null,
                null,
                null,
                null
        );

        try {
            client.deleteSpend(spendToDelete);
            System.out.println("Spend deleted successfully: " + spendId);
        } catch (RuntimeException e) {
            System.out.println("Failed to delete spend: " + e.getMessage());
        }
    }

    @Test
    void findCategoryByIdTest() {
        SpendDbClient client = new SpendDbClient();
        UUID categoryId = UUID.fromString("bb9f8035-9058-4056-8e3a-2e4975046231");

        Optional<CategoryEntity> categoryOpt = client.findCategoryById(categoryId);

        // Если категория найдена — выводим имя
        categoryOpt.ifPresent(c -> System.out.println(
                String.format("ID: %s, Name: %s, Username: %s, Archived: %s",
                        c.getId(), c.getName(), c.getUsername(), c.isArchived())
        ));
    }

    @Test
    void testFindAllCategoriesByUsername() {
        SpendDbClient client = new SpendDbClient();

        // Если метод возвращает список
        List<CategoryJson> allCategories = client.findAllCategoriesByUsername("Clatto");
        allCategories.forEach(System.out::println);
    }

    @Test
    void deleteCategoryTest() {
        SpendDbClient client = new SpendDbClient();

        UUID categoryId = UUID.fromString("ba4893a4-54ea-46ca-9d2b-6105c675853a");


        CategoryJson categoryToDelete = new CategoryJson(
                categoryId,
                null,
                null,
                true
        );

        try {
            client.deleteCategory(categoryToDelete);
            System.out.println("Category deleted successfully: " + categoryId);
        } catch (RuntimeException e) {
            System.out.println("Failed to delete category: " + e.getMessage());
        }
    }

    @Test
    void createNewUserTest() {
        UserDataDbClient userData = new UserDataDbClient();

        UserJson user = new UserJson(
                null,
                "Create_Test_User2",
                "Petr",
                "Petrovich",
                "test",
                CurrencyValues.KZT,
                "fwaf",
                "nfdjsnj"
        );

        UserJson createdUser = userData.createUser(user);

        // Вывод информации о созданном пользователе
        System.out.println("New user created:");
        System.out.println("ID: " + createdUser.id());
        System.out.println("Username: " + createdUser.username());
        System.out.println("First Name: " + createdUser.firstname());
        System.out.println("Surname: " + createdUser.surname());
        System.out.println("Full Name: " + createdUser.fullname());
        System.out.println("Currency: " + createdUser.currency());
    }

    @Test
    void findUserByIdTest() {
        UserDataDbClient userData = new UserDataDbClient();

        UUID userId = UUID.fromString("af03544f-56d6-4d37-abff-28286a0621cb");

        Optional<UserEntity> userOpt = userData.findById(userId);

        userOpt.ifPresent(u -> System.out.println(
                String.format("ID: %s, Firstname: %s, Username: %s, Photo: %s",
                        u.getId(), u.getFirstname(), u.getUsername(), Arrays.toString(u.getPhoto()))
        ));
    }

    @Test
    void findUserByUsernameTest() {
        UserDataDbClient userData = new UserDataDbClient();

        Optional<UserEntity> userOpt = userData.findByUsername("Test_for_Delete");

        userOpt.ifPresent(u -> System.out.println(
                String.format("ID: %s, Firstname: %s, Username: %s, Photo: %s",
                        u.getId(), u.getFirstname(), u.getUsername(), Arrays.toString(u.getPhoto()))
        ));
    }

    @Test
    void deleteUserTest() {
        UserDataDbClient userData = new UserDataDbClient();
        UUID userId = UUID.fromString("f4947822-ad05-11f0-a080-a6da7190eac4");
        UserJson userToDelete = new UserJson(
                userId,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        try {
            userData.deleteUser(userToDelete);
            System.out.println("User deleted successfully: " + userId);
        } catch (RuntimeException e) {
            System.out.println("Failed to delete user: " + e.getMessage());
        }
    }
}
