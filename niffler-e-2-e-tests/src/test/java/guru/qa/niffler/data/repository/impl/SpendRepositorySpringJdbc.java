package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;

import java.util.Optional;
import java.util.UUID;

public class SpendRepositorySpringJdbc implements SpendRepository {

    private static final Config CFG = Config.getInstance();

    private static final SpendDao spendDao = new SpendDaoSpringJdbc();
    private static final CategoryDao categoryDao = new CategoryDaoJdbc();

    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    @Override
    public SpendEntity create(SpendEntity spend) {
        return xaTxTemplate.execute(() -> {
            SpendEntity resultSpend = spendDao.create(spend);
            if (spend.getCategory() != null) {
                categoryDao.findById(spend.getCategory().getId())
                        .orElseGet(() -> categoryDao.create(spend.getCategory()));
            }
            return resultSpend;
        });
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        return spendDao.update(spend);
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        return categoryDao.create(category);
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return categoryDao.findById(id);
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name) {
        return categoryDao.findCategoryByUsernameAndCategoryName(username, name);
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        return spendDao.findById(id);
    }

    @Override
    public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        return spendDao.findByUsernameAndSpendDescription(username, description);
    }

    @Override
    public void remove(SpendEntity spend) {
        spendDao.delete(spend);
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        categoryDao.delete(category);
    }
}
