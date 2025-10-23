package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDbClient {

    private final SpendDao spendDao = new SpendDaoJdbc();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();

    private static final Config CFG = Config.getInstance();

    public SpendJson createSpend(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        if (spendEntity.getCategory().getId() == null) {
            CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
            spendEntity.setCategory(categoryEntity);
        }

        return SpendJson.fromEntity(
                spendDao.create(spendEntity)
        );
    }

    public Optional<SpendEntity> findSpendById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return spendDao.findSpendById(id);
    }

    public List<SpendJson> findAllSpendsByUsername(String username) {
        return spendDao.findAllByUsername(username).stream()
                .map(SpendJson::fromEntity)
                .toList();
    }

    public void deleteSpend(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        spendDao.deleteSpend(spendEntity);
    }

    public CategoryJson createCategory(CategoryJson category) {
        CategoryEntity entity = CategoryEntity.fromJson(category);
        CategoryEntity created = categoryDao.create(entity);
        return CategoryJson.fromEntity(created);
    }

    public CategoryJson updateCategory(CategoryJson category) {
        CategoryEntity entity = CategoryEntity.fromJson(category);
        CategoryEntity updated = categoryDao.update(entity);
        return CategoryJson.fromEntity(updated);
    }


    public Optional<CategoryEntity> findCategoryById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return categoryDao.findCategoryById(id);
    }

    public Optional<CategoryJson> findCategoryByUsernameAndCategoryName(CategoryJson category) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(category);

        return categoryDao.findCategoryByUsernameAndCategoryName(
                        categoryEntity.getUsername(),
                        categoryEntity.getName()
                )
                .map(CategoryJson::fromEntity);
    }

    public List<CategoryJson> findAllCategoriesByUsername(String username) {
        return categoryDao.findAllByUsername(username).stream()
                .map(CategoryJson::fromEntity)
                .toList();
    }

    public void deleteCategory(CategoryJson category) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
        categoryDao.deleteCategory(categoryEntity);
    }
}
