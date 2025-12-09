package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class UserdataUserRepositoryJdbc implements UserdataUserRepository {

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
                if (rs.next()) {
                    return Optional.of(getUser(rs));
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
                if (rs.next()) {
                    return Optional.of(getUser(rs));
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

    private void createFriendshipWithStatus(UserEntity requester, UserEntity addressee, FriendshipStatus status) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO friendship (requester_id, addressee_id, status) VALUES (?,?,?)"
        )) {
            ps.setObject(1, requester.getId());
            ps.setObject(2, addressee.getId());
            ps.setString(3, status.name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

    private UserEntity getUser(ResultSet rs) throws SQLException {
        Map<UUID, UserEntity> users = new ConcurrentHashMap<>();
        UserEntity user = null;

        while (rs.next()) {
            UUID userId = rs.getObject("id", UUID.class);

            user = users.computeIfAbsent(userId, id -> {
                UserEntity result = new UserEntity();
                try {
                    result.setId(rs.getObject("id", UUID.class));
                    result.setUsername(rs.getString("username"));
                    result.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    result.setFirstname(rs.getString("firstname"));
                    result.setSurname(rs.getString("surname"));
                    result.setFullname(rs.getString("full_name"));
                    result.setPhoto(rs.getBytes("photo"));
                    result.setPhotoSmall(rs.getBytes("photo_small"));
                    return result;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            List<FriendshipEntity> friendshipRequests = user.getFriendshipRequests();
            List<FriendshipEntity> friendshipAddressee = user.getFriendshipAddressees();
            FriendshipEntity friendship = new FriendshipEntity();
            UserEntity requester = new UserEntity();
            UserEntity addressee = new UserEntity();
            UUID requesterId = rs.getObject("requester_id", UUID.class);
            UUID addresseeId = rs.getObject("addressee_id", UUID.class);
            FriendshipStatus status = FriendshipStatus.valueOf(rs.getString("status"));

            if (requesterId != null) {
                requester.setId(requesterId);
                friendship.setRequester(requester);
            }
            if (addresseeId != null) {
                addressee.setId(addresseeId);
                friendship.setAddressee(addressee);
            }
            if (status != null) friendship.setStatus(status);

            if (requesterId.equals(userId)) friendshipRequests.add(friendship);
            if (addresseeId.equals(userId)) friendshipAddressee.add(friendship);

            user.setFriendshipRequests(friendshipRequests);
            user.setFriendshipAddressees(friendshipAddressee);
        }
        return user;
    }
}
