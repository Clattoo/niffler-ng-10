package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoSpringJdbc implements CategoryDao {

    private final DataSource dataSource;

    public CategoryDaoSpringJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public CategoryEntity create(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("INSERT INTO category (name, username, archived)" + "VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, category.getName());
            ps.setString(2, category.getUsername());
            ps.setBoolean(3, category.isArchived());
            return ps;
        }, kh);

        final UUID generatedKey = (UUID) Objects.requireNonNull(kh.getKeys()).get("id");
        category.setId(generatedKey);
        return category;
    }

    @Override
    public Optional<CategoryEntity> findById(UUID id) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template.query(
                "SELECT * FROM category WHERE id = ?",
                CategoryEntityRowMapper.instance,
                id
        ).stream().findFirst();
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template.query(
                "SELECT * FROM category WHERE username = ? AND name = ?",
                CategoryEntityRowMapper.instance,
                username, categoryName
        ).stream().findFirst();
    }

    @Override
    public List<CategoryEntity> findAllByUsername(String username) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template.query(
                "SELECT * FROM category WHERE username = ?",
                CategoryEntityRowMapper.instance,
                username
        );
    }

    @Override
    public void delete(CategoryEntity category) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.update(
                "DELETE FROM category WHERE id = ?",
                category.getId()
        );
    }

    @Override
    public List<CategoryEntity> findAll() {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template.query(
                "SELECT * FROM category",
                CategoryEntityRowMapper.instance
        );
    }
}
