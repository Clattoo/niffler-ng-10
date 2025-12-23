package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserdataUserEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UserdataUserDaoSpringJdbc implements UserdataUserDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity create(UserEntity user) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
                            "VALUES (?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getCurrency().name());
            statement.setString(3, user.getFirstname());
            statement.setString(4, user.getSurname());
            statement.setBytes(5, user.getPhoto());
            statement.setBytes(6, user.getPhotoSmall());
            statement.setString(7, user.getFullname());
            return statement;
        }, keyHolder);

        final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        user.setId(generatedKey);
        return user;
    }

    @Override
    public UserEntity update(UserEntity user) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

        template.update(con -> {
            PreparedStatement usersPs = con.prepareStatement(
                    "UPDATE \"user\" " +
                            "SET currency = ?, " +
                            "firstname   = ?, " +
                            "surname     = ?, " +
                            "photo       = ?, " +
                            "photo_small = ? " +
                            "WHERE id = ?"
            );
            usersPs.setString(1, user.getCurrency().name());
            usersPs.setString(2, user.getFirstname());
            usersPs.setString(3, user.getSurname());
            usersPs.setBytes(4, user.getPhoto());
            usersPs.setBytes(5, user.getPhotoSmall());
            usersPs.setObject(6, user.getId());
            return usersPs;
        });

        if (user.getFriendshipRequests() != null && !user.getFriendshipRequests().isEmpty()) {
            template.batchUpdate(
                    "INSERT INTO friendship (requester_id, addressee_id, status) " +
                            "VALUES (?, ?, ?) " +
                            "ON CONFLICT (requester_id, addressee_id) " +
                            "DO UPDATE SET status = ?",
                    user.getFriendshipRequests(),
                    user.getFriendshipRequests().size(),
                    (ps, fe) -> {
                        ps.setObject(1, user.getId());
                        ps.setObject(2, fe.getAddressee().getId());
                        ps.setString(3, fe.getStatus().name());
                        ps.setString(4, fe.getStatus().name());
                    }
            );
        }

        return user;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT u.*, " +
                                "fr.requester_id AS fr_requester_id, fr.addressee_id AS fr_addressee_id, fr.status AS fr_status, " +
                                "fa.requester_id AS fa_requester_id, fa.addressee_id AS fa_addressee_id, fa.status AS fa_status " +
                                "FROM \"user\" u " +
                                "LEFT JOIN friendship fr ON u.id = fr.requester_id " +
                                "LEFT JOIN friendship fa ON u.id = fa.addressee_id " +
                                "WHERE u.username = ?",
                        UserdataUserEntityRowMapper.instance,
                        id
                )
        );
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return template.query(
                "SELECT u.*, " +
                        "fr.requester_id AS fr_requester_id, fr.addressee_id AS fr_addressee_id, fr.status AS fr_status, " +
                        "fa.requester_id AS fa_requester_id, fa.addressee_id AS fa_addressee_id, fa.status AS fa_status " +
                        "FROM \"user\" u " +
                        "LEFT JOIN friendship fr ON u.id = fr.requester_id " +
                        "LEFT JOIN friendship fa ON u.id = fa.addressee_id " +
                        "WHERE u.username = ?",
                UserdataUserEntityRowMapper.instance,
                username
        ).stream().findFirst();
    }

    @Override
    public void delete(UserEntity user) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        template.update(
                "DELETE FROM \"user\" WHERE id = ?",
                user.getId()
        );
    }
}
