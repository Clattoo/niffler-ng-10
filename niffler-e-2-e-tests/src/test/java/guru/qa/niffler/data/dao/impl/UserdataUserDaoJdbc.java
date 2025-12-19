package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserdataUserSetExtractor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class UserdataUserDaoJdbc implements UserdataUserDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity create(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"user\" (username, currency, firstname, surname, full_name, photo, photo_small)"
                        +
                        "VALUES(?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setString(5, user.getFullname());
            ps.setBytes(6, user.getPhoto());
            ps.setBytes(7, user.getPhotoSmall());
            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can't find id in ResultSet");
                }
            }
            user.setId(generatedKey);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserEntity update(UserEntity user) {
        try (PreparedStatement usersPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "UPDATE \"user\" " +
                        "SET currency = ?, " +
                        "firstname   = ?, " +
                        "surname     = ?, " +
                        "photo       = ?, " +
                        "photo_small = ? " +
                        "WHERE id = ? ");
             PreparedStatement friendsPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                     "INSERT INTO friendship (requester_id, addressee_id, status) " +
                             "VALUES (?, ?, ?) " +
                             "ON CONFLICT (requester_id, addressee_id) " +
                             "DO UPDATE SET status = ?")
        ) {
            usersPs.setString(1, user.getCurrency().name());
            usersPs.setString(2, user.getFirstname());
            usersPs.setString(3, user.getSurname());
            usersPs.setBytes(4, user.getPhoto());
            usersPs.setBytes(5, user.getPhotoSmall());
            usersPs.setObject(6, user.getId());
            usersPs.executeUpdate();

            if (user.getFriendshipRequests() != null && !user.getFriendshipRequests().isEmpty()) {
                for (FriendshipEntity fe : user.getFriendshipRequests()) {
                    friendsPs.setObject(1, user.getId());
                    friendsPs.setObject(2, fe.getAddressee().getId());
                    friendsPs.setString(3, fe.getStatus().name());
                    friendsPs.setString(4, fe.getStatus().name());
                    friendsPs.addBatch();
                    friendsPs.clearParameters();
                }
                friendsPs.executeBatch();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT u.*, " +
                        "fr.requester_id AS fr_requester_id, fr.addressee_id AS fr_addressee_id, fr.status AS fr_status, " +
                        "fa.requester_id AS fa_requester_id, fa.addressee_id AS fa_addressee_id, fa.status AS fa_status " +
                        "FROM \"user\" u " +
                        "LEFT JOIN friendship fr ON u.id = fr.requester_id " +
                        "LEFT JOIN friendship fa ON u.id = fa.addressee_id " +
                        "WHERE u.id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                UserdataUserSetExtractor extractor = new UserdataUserSetExtractor();
                UserEntity user = extractor.extractData(rs);
                if (rs.next()) {
                    return Optional.of(user);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT u.*, " +
                        "fr.requester_id AS fr_requester_id, fr.addressee_id AS fr_addressee_id, fr.status AS fr_status, " +
                        "fa.requester_id AS fa_requester_id, fa.addressee_id AS fa_addressee_id, fa.status AS fa_status " +
                        "FROM \"user\" u " +
                        "LEFT JOIN friendship fr ON u.id = fr.requester_id " +
                        "LEFT JOIN friendship fa ON u.id = fa.addressee_id " +
                        "WHERE u.username = ?"
        )) {
            ps.setObject(1, username);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                UserdataUserSetExtractor extractor = new UserdataUserSetExtractor();
                UserEntity user = extractor.extractData(rs);
                if (rs.next()) {
                    return Optional.of(user);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"user\" WHERE id =?"
        )) {
            ps.setObject(1, user.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
