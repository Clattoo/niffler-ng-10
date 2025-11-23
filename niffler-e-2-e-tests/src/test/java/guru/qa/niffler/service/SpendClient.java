package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.Optional;

public interface SpendClient {

    SpendJson findSpendById(String id, String username);

    List<SpendJson> findSpendsByUserName(String username, CurrencyValues currencyValues, String from,
                                         String to);

    SpendJson createSpend(SpendJson spend);

    SpendJson updateSpend(SpendJson spendJson);

    void deleteSpends(String username, List<String> ids);

    List<CategoryJson> findAllCategories(String username);

    CategoryJson createCategory(CategoryJson category);

    CategoryJson updateCategory(CategoryJson categoryJson);

    Optional<CategoryJson> findCategoryByNameAndUsername(String categoryName, String username);
}