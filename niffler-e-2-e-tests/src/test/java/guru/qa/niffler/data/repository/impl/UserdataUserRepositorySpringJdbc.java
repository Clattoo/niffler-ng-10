package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserdataUserSetExtractor;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UserdataUserRepositorySpringJdbc implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();

    private final UserdataUserDao userdataUserDao = new UserdataUserDaoSpringJdbc();

    @Override
    @Nonnull
    public UserEntity create(UserEntity user) {
        return userdataUserDao.create(user);
    }

    @Override
    @Nonnull
    public UserEntity update(UserEntity user) {
        return userdataUserDao.update(user);
    }

    @Override
    @Nonnull
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
    @Nonnull
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
        userdataUserDao.delete(user);
    }

    @Override
    public void sendInvitation(UserEntity requester, UserEntity addressee) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO  friendship (requester_id, addressee_id, status) " +
                            "VALUES (?, ?, ?) " +
                            "ON CONFLICT (requester_id, addressee_id) " +
                            "DO UPDATE SET status = ?"
            );
            ps.setObject(1, requester.getId());
            ps.setObject(2, addressee.getId());
            ps.setString(3, FriendshipStatus.PENDING.name());
            ps.setDate(4, Date.valueOf(LocalDate.now()));
            return ps;
        });
    }


    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.batchUpdate(
                "INSERT INTO  friendship (requester_id, addressee_id, status) " +
                        "VALUES (?, ?, ?) " +
                        "ON CONFLICT (requester_id, addressee_id) " +
                        "DO UPDATE SET status = ?",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        if (i == 0) {
                            ps.setObject(1, requester.getId());
                            ps.setObject(2, addressee.getId());
                        } else {
                            ps.setObject(1, addressee.getId());
                            ps.setObject(2, requester.getId());
                        }
                        ps.setString(3, FriendshipStatus.ACCEPTED.name());
                        ps.setDate(4, Date.valueOf(LocalDate.now()));
                    }

                    @Override
                    public int getBatchSize() {
                        return 2;
                    }
                }
        );
    }
}
