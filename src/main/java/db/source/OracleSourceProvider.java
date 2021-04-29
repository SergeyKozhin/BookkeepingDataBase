package db.source;

import oracle.jdbc.pool.OracleDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class OracleSourceProvider {
    private static final String URL = "jdbc:oracle:thin:@localhost:51521:XE";
    private static final String USER = "C##_sergey";
    private static final String PASSWORD = "password";

    public static OracleDataSource getDataSource() {
        try {
            OracleDataSource ds = new OracleDataSource();
            ds.setURL(URL);
            ds.setUser(USER);
            ds.setPassword(PASSWORD);

            try (Connection connection = ds.getConnection()) {

            } catch (SQLException e) {
                throw new RuntimeException("Error while connecting to database", e);
            }

            return ds;

        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to database", e);
        }
    }
}
