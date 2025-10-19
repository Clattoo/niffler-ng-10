package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class SpendDaoJdbc implements SpendDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public SpendEntity create(SpendEntity spend) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                            "VALUES ( ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setString(1, spend.getUsername());
                ps.setDate(2, spend.getSpendDate());
                ps.setString(3, spend.getCurrency().name());
                ps.setDouble(4, spend.getAmount());
                ps.setString(5, spend.getDescription());
                ps.setObject(6, spend.getCategory().getId());

                ps.executeUpdate();

                final UUID generatedKey;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedKey = rs.getObject("id", UUID.class);
                    } else {
                        throw new SQLException("Can't find ID in ResultSet");
                    }
                }
                spend.setId(generatedKey);
                return spend;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl());
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT s.*, " +
                             "c.id AS cat_id, c.name AS cat_name, c.username AS cat_username, c.archived AS cat_archived " +
                             "FROM spend s " +
                             "LEFT JOIN category c ON s.category_id = c.id " +
                             "WHERE s.id = ?"
             )) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SpendEntity se = new SpendEntity();
                    se.setId(rs.getObject("id", UUID.class));
                    se.setUsername(rs.getString("username"));
                    se.setSpendDate(rs.getDate("spend_date"));
                    se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    se.setAmount(rs.getDouble("amount"));
                    se.setDescription(rs.getString("description"));

                    CategoryEntity category = new CategoryEntity();
                    category.setId(rs.getObject("cat_id", UUID.class));
                    category.setName(rs.getString("cat_name"));
                    category.setUsername(rs.getString("cat_username"));
                    category.setArchived(rs.getBoolean("cat_archived"));
                    se.setCategory(category);

                    return Optional.of(se);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpendEntity> findAllByUsername(String username) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl());
             PreparedStatement ps = connection.prepareStatement("""
                         SELECT
                             s.id             AS spend_id,
                             s.username       AS spend_username,
                             s.spend_date     AS spend_date,
                             s.currency       AS currency,
                             s.amount         AS amount,
                             s.description    AS description,
                             c.id             AS cat_id,
                             c.name           AS cat_name,
                             c.username       AS cat_username,
                             c.archived       AS cat_archived
                         FROM spend s
                         LEFT JOIN category c ON s.category_id = c.id
                         WHERE s.username = ?
                     """)) {

            ps.setString(1, username);
            ps.execute();

            List<SpendEntity> spends = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    SpendEntity se = new SpendEntity();
                    se.setId(rs.getObject("spend_id", UUID.class));
                    se.setUsername(rs.getString("spend_username"));
                    se.setSpendDate(rs.getDate("spend_date"));
                    se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    se.setAmount(rs.getDouble("amount"));
                    se.setDescription(rs.getString("description"));

                    CategoryEntity category = new CategoryEntity();
                    category.setId(rs.getObject("cat_id", UUID.class));
                    category.setName(rs.getString("cat_name"));
                    category.setUsername(rs.getString("cat_username"));
                    category.setArchived(rs.getBoolean("cat_archived"));

                    se.setCategory(category);
                    spends.add(se);
                }
            }
            return spends;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteSpend(SpendEntity spend) {
        if (spend.getId() == null) {
            throw new IllegalArgumentException("Spend ID must not be null");
        }
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl());
             PreparedStatement ps = connection.prepareStatement(
                     "DELETE FROM spend WHERE id = ?"
             )) {
            ps.setObject(1, spend.getId());
            int deletedRows = ps.executeUpdate();
            if (deletedRows != 1) {
                throw new SQLException(
                        "Failed to delete spend with id = %s from table spend".formatted(spend.getId())
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}