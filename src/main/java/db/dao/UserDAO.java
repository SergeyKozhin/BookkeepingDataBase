package db.dao;

import at.favre.lib.crypto.bcrypt.BCrypt;
import oracle.jdbc.OracleTypes;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private final DataSource ds;

    public UserDAO(DataSource ds) {
        this.ds = ds;
    }

    public int add(String username, String password) {
        try (Connection connection = ds.getConnection();
             CallableStatement cs = connection.prepareCall("BEGIN INSERT INTO USERS (USERNAME, PASSWORD_HASH) VALUES (?, ?) RETURNING ID INTO ?; END;")) {

            cs.setString(1, username);
            cs.setString(2, BCrypt.withDefaults().hashToString(12, password.toCharArray()));
            cs.registerOutParameter(3, OracleTypes.NUMBER);

            cs.execute();

            return cs.getInt(3);
        } catch (SQLException e) {
            throw new RuntimeException("Error adding user", e);
        }
    }

    public boolean authenticate(String username, String password) {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT PASSWORD_HASH FROM USERS WHERE USERNAME = ?")) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return BCrypt.verifyer().verify(password.toCharArray(), rs.getString("PASSWORD_HASH")).verified;
                }
                return false;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error authorizing user", e);
        }
    }
}
