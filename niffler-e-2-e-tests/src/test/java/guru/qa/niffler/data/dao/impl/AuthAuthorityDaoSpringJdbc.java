package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthorityEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthAuthorityDaoSpringJdbc implements AuthAuthorityDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public void create(AuthorityEntity... authority) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        template.batchUpdate(
                "INSERT INTO authority (user_id, authority) VALUES (? , ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, authority[i].getUserId());
                        ps.setString(2, authority[i].getAuthority().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return authority.length;
                    }
                }
        );
    }

    @Override
    public List<AuthorityEntity> findAuthoritiesByUserId(UUID userId) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return template.query(
                "SELECT * FROM authority WHERE user_id = ?",
                AuthorityEntityRowMapper.instance,
                userId
        );
    }

    @Override
    public Optional<AuthorityEntity> findById(UUID id) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return template.query(
                "SELECT * FROM authority WHERE id = ?",
                AuthorityEntityRowMapper.instance,
                id
        ).stream().findFirst();
    }

    @Override
    public void delete(AuthorityEntity authority) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        template.update(
                "DELETE FROM authority WHERE id = ?",
                authority.getId()
        );
    }
}
