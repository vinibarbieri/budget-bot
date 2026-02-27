package infra;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    private static DatabaseManager instance;
    private final HikariDataSource dataSource;

    private DatabaseManager() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:postgresql://100.92.138.68:5432/bot_finance");
        config.setUsername("vinicius");
        config.setPassword("Br0c0l!s");

        // Connection Pool optimization for Raspberry Pi 5
        config.setMaximumPoolSize(10); // Save Postgres RAM
        config.setMinimumIdle(2);      // Always have 2 open connections - hot cache
        config.setIdleTimeout(30000);  // Close connections after 30s
        config.setConnectionTimeout(10000); // Connection timeout = 10s

        this.dataSource = new HikariDataSource(config);
    }

    // If the instance doesn't exist, it's going to be created
    // synchronized is kind of mutex -> Every thread must go to a queue, so the first get in and create de instance, when the second enter instance != null
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // Checked exceptions -> Must have error handling, or it isn't compiling
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}