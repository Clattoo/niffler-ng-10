package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.AuthUserEntity;
import guru.qa.niffler.data.entity.Authority;
import guru.qa.niffler.data.entity.AuthorityEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public void create(AuthorityEntity... authorities) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO authority (user_id, authority)" +
                        "VALUES(?,?)",
                PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            for (AuthorityEntity authority : authorities) {
                ps.setObject(1, authority.getUser().getId());
                ps.setString(2, authority.getAuthority().name());
                ps.addBatch();
                ps.clearParameters();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthorityEntity> findAuthoritiesByUserId(UUID id) {
        List<AuthorityEntity> authorities = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM authority WHERE user_id = ?"
        )) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setId(rs.getObject("id", UUID.class));
                    AuthUserEntity user = new AuthUserEntity();
                    user.setId(rs.getObject("user_id", UUID.class));
                    ae.setAuthority(Authority.valueOf(rs.getString("authority")));
                    authorities.add(ae);
                }
                return authorities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthorityEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM authority WHERE id = ?"
        )) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setId(rs.getObject("id", UUID.class));
                    AuthUserEntity user = new AuthUserEntity();
                    user.setId(rs.getObject("user_id", UUID.class));
                    ae.setAuthority(Authority.valueOf(rs.getString("authority")));
                    return Optional.of(ae);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(AuthorityEntity authority) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "DELETE FROM authority WHERE id = ?"
        )) {
            ps.setObject(1, authority.getId());
            ps.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}