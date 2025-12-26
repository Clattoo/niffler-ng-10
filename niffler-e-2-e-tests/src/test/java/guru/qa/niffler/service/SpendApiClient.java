package guru.qa.niffler.service;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Step;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.apache.hc.core5.http.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class SpendApiClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.spendUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final SpendApi spendApi = retrofit.create(SpendApi.class);

    @Override
    @Step("Найти spend по ID {id} пользователя {username}")
    public @Nullable SpendJson findSpendById(String id, String username) {
        final Response<SpendJson> response;
        try {
            response = spendApi.getSpend(id, username).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
        return response.body();
    }

    @Override
    @Nonnull
    @Step("Найти все spends пользователя {username} с валютой {currencyValues} в период с {from} по {to}")
    public List<SpendJson> findSpendsByUserName(String username, CurrencyValues currencyValues,
                                                String from, String to) {
        final Response<SpendJson[]> response;
        try {
            response = spendApi.getSpends(username, currencyValues, from, to).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
        return List.of(Objects.requireNonNullElseGet(response.body(), () -> new SpendJson[0]));
    }

    @Override
    @Step("Создать новый spend")
    public @Nullable SpendJson createSpend(SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.addSpend(spend).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_CREATED, response.code());
        return response.body();
    }

    @Override
    @Step("Обновить spend")
    public @Nullable SpendJson updateSpend(SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.updateSpend(spend).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
        return response.body();
    }

    @Override
    @Step("Удалить spends пользователя {username}")
    public void deleteSpends(String username, List<String> ids) {
        final Response<Void> response;
        try {
            response = spendApi.removeSpends(username, ids).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_ACCEPTED, response.code());
    }

    @Override
    @Nonnull
    @Step("Получить все категории пользователя {username}")
    public List<CategoryJson> findAllCategories(String username) {
        final Response<List<CategoryJson>> response;
        try {
            response = spendApi.getCategories(username, null).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
        return response.body() != null
                ? response.body()
                : Collections.emptyList();
    }

    @Override
    @Step("Создать новую категорию {category}")
    public @Nullable CategoryJson createCategory(CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.addCategory(category).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
        return response.body();
    }

    @Override
    @Step("Обновить категорию {category}")
    public @Nullable CategoryJson updateCategory(CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.updateCategory(category).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
        return response.body();
    }

    @Override
    public Optional<CategoryJson> findCategoryByNameAndUsername(String categoryName,
                                                                String username) {
        throw new UnsupportedOperationException("Not implemented :(");
    }
}
