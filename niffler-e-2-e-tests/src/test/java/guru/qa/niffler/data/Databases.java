package guru.qa.niffler.data;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Databases {
    private Databases() {
    }

    private static final Map<String, DataSource> datasources = new ConcurrentHashMap<>();

    private static DataSource dataSource(String jbdcUrl) {
        return datasources.computeIfAbsent(
                jbdcUrl,
                key -> {
                    PGSimpleDataSource ds = new PGSimpleDataSource();
                    ds.setUser("postgres");
                    ds.setPassword("secret");
                    ds.setUrl(key);
                    return ds;
                }
        );
    }

    public static Connection connection(String jbdcUrl) throws SQLException {
        return dataSource(jbdcUrl).getConnection();
    }
}
