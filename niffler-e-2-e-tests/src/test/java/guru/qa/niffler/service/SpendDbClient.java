package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.Optional;

public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final CategoryDao categoryDao = new CategoryDaoJdbc();
    private final SpendDao spendDao = new SpendDaoJdbc();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    @Override
    public SpendJson findSpendById(String id, String username) {
        throw new UnsupportedOperationException("Not implemented :(");
    }

    @Override
    public List<SpendJson> findSpendsByUserName(String username, CurrencyValues currencyValues,
                                                String from, String to) {
        throw new UnsupportedOperationException("Not implemented :(");
    }

    @Override
    public SpendJson createSpend(SpendJson spend) {
        return jdbcTxTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(
                            spendDao.create(spendEntity)
                    );
                }
        );
    }

    @Override
    public SpendJson updateSpend(SpendJson spendJson) {
        throw new UnsupportedOperationException("Not implemented :(");
    }

    @Override
    public void deleteSpends(String username, List<String> ids) {
        throw new UnsupportedOperationException("Not implemented :(");
    }

    @Override
    public List<CategoryJson> findAllCategories(String username) {
        throw new UnsupportedOperationException("Not implemented :(");
    }

    @Override
    public CategoryJson createCategory(CategoryJson category) {
        return jdbcTxTemplate.execute(() -> {
                    return CategoryJson.fromEntity(
                            categoryDao.create(
                                    CategoryEntity.fromJson(category)
                            )
                    );
                }
        );
    }

    @Override
    public CategoryJson updateCategory(CategoryJson categoryJson) {
        throw new UnsupportedOperationException("Not implemented :(");
    }

    @Override
    public Optional<CategoryJson> findCategoryByNameAndUsername(String categoryName,
                                                                String username) {
        throw new UnsupportedOperationException("Not implemented :(");
    }
}