package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserdataUserSetExtractor;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserRepositorySpringJdbc implements UserdataUserRepository {

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
    public Optional<UserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.query(
                        "SELECT u.*, " +
                                "fr.requester_id AS fr_requester_id, fr.addressee_id AS fr_addressee_id, fr.status AS fr_status, " +
                                "fa.requester_id AS fa_requester_id, fa.addressee_id AS fa_addressee_id, fa.status AS fa_status " +
                                "FROM \"user\" u " +
                                "LEFT JOIN friendship fr ON u.id = fr.requester_id " +
                                "LEFT JOIN friendship fa ON u.id = fa.addressee_id " +
                                "WHERE u.username = ?",
                        UserdataUserSetExtractor.instance,
                        id));
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.query(
                        "SELECT u.*, " +
                                "fr.requester_id AS fr_requester_id, fr.addressee_id AS fr_addressee_id, fr.status AS fr_status, " +
                                "fa.requester_id AS fa_requester_id, fa.addressee_id AS fa_addressee_id, fa.status AS fa_status " +
                                "FROM \"user\" u " +
                                "LEFT JOIN friendship fr ON u.id = fr.requester_id " +
                                "LEFT JOIN friendship fa ON u.id = fa.addressee_id " +
                                "WHERE u.id = ?",
                        UserdataUserSetExtractor.instance,
                        username));
    }

    @Override
    public void delete(UserEntity user) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        template.update(
                "DELETE FROM \"user\" WHERE id = ?",
                user.getId()
        );
    }

    private void createFriendshipWithStatus(UserEntity requester, UserEntity addressee, FriendshipStatus status) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    """
                            INSERT INTO friendship (requester_id, addressee_id, status)
                            VALUES (?, ?, ?)
                            """
            );
            statement.setObject(1, requester.getId());
            statement.setObject(2, addressee.getId());
            statement.setString(3, status.name());
            return statement;
        });
    }

    @Override
    public void addIncomeInvitation(UserEntity requester, UserEntity addressee) {
        createFriendshipWithStatus(requester, addressee, FriendshipStatus.PENDING);
    }

    @Override
    public void addOutcomeInvitation(UserEntity requester, UserEntity addressee) {
        createFriendshipWithStatus(requester, addressee, FriendshipStatus.PENDING);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        createFriendshipWithStatus(requester, addressee, FriendshipStatus.ACCEPTED);
        createFriendshipWithStatus(addressee, requester, FriendshipStatus.ACCEPTED);
    }
}
