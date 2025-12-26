package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class FriendshipEntityRowMapper implements RowMapper<FriendshipEntity> {

    public static final FriendshipEntityRowMapper instance = new FriendshipEntityRowMapper();

    private FriendshipEntityRowMapper() {
    }

    @Nonnull
    @Override
    public FriendshipEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        FriendshipEntity friendship = new FriendshipEntity();
        UserEntity requester = new UserEntity();
        UserEntity addressee = new UserEntity();
        UUID requesterId = rs.getObject("requester_id", UUID.class);
        UUID addresseeId = rs.getObject("addressee_id", UUID.class);
        FriendshipStatus status = Optional.ofNullable(rs.getString("status"))
                .map(FriendshipStatus::valueOf)
                .orElse(null);
        if (requesterId != null) {
            requester.setId(requesterId);
            friendship.setRequester(requester);
        }
        if (addresseeId != null) {
            addressee.setId(addresseeId);
            friendship.setAddressee(addressee);
        }
        if (status != null) friendship.setStatus(status);
        return friendship;
    }
}
