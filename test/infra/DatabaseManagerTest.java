package infra;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManagerTest {

    @Test
    public void testSingletonInstanceIsNotNull() {
        DatabaseManager instance = DatabaseManager.getInstance();
        assertNotNull(instance, "Singleton instance wasn't suppose to be null");
    }

    @Test
    public void testConnectionIsValid() {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {

            assertNotNull(conn, "Connection can't be null");
            assertFalse(conn.isClosed(), "Connection should be open and ready for access");

        } catch (SQLException e) {
            fail("Database connection failed. Verify Tailscale, credentials or if Postgres is running Error: " + e.getMessage());
        }
    }
}