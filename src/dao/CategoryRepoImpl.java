package dao;

import domain.Category;
import domain.CategoryType;
import infra.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryRepoImpl implements CategoryRepo {

    @Override
    public void save(Category category) {
        String sql = "INSERT INTO categories (user_id, name, type) VALUES (?, ?, ?)";

        // 2. Try-with-resources to ensure both Connection and PreparedStatement are closed properly
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 3. Binding the parameters safely. The JDBC driver handles escaping special characters.
            pstmt.setLong(1, category.getUserId());
            pstmt.setString(2, category.getName());
            pstmt.setString(3, category.getType().name());

            // 4. Executing the query on the database
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Saving Category failed, no rows affected.");
            }

        } catch (SQLException e) {
            // In a real robust system, we would log this error using SLF4J and rethrow a custom DomainException
            throw new RuntimeException("Error communicating with the database: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Category> listActiveCategories(Long userId) {
        String sql  = "SELECT id, user_id, name, type, is_active, created_at " +
                "FROM Categories " +
                "WHERE user_id = ? AND is_active = TRUEAND is_active = TRUE";

        List<Category> activeCategories = new ArrayList<>();

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category(
                            rs.getLong("id"),
                            rs.getLong("user_id"),
                            rs.getString("name"),
                            CategoryType.valueOf(rs.getString("type")),
                            rs.getBoolean("is_active"),
                            rs.getTimestamp("created_at")
                    );
                    activeCategories.add(category);
                }
            }

        } catch (SQLException e) {
           throw new RuntimeException("Fail on getting categories in DB", e);
        }

        return activeCategories;
    }

    @Override
    public Optional<Category> findByNameAndUser(String name, Long userId) {
        String sql = "SELECT id, user_id, name, type, is_active, created_at " +
                "FROM categories " +
                "WHERE user_id = ? AND name = ? AND is_active = TRUE";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);
            pstmt.setString(2, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Category category = new Category(
                            rs.getLong("id"),
                            rs.getLong("user_id"),
                            rs.getString("name"),
                            CategoryType.valueOf(rs.getString("type")),
                            rs.getBoolean("is_active"),
                            rs.getTimestamp("created_at")
                    );

                    return Optional.of(category);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching category by name", e);
        }

        return Optional.empty();
    }
}
