package dao;

import domain.Budget;
import infra.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BudgetRepoImpl implements BudgetRepo {

    @Override
    public void save(Budget budget) {
        String sql = "INSERT INTO budget (category_id, month, year, limit_amount) VALUES (?, ?, ?, ?)";

        // 2. Try-with-resources to ensure both Connection and PreparedStatement are closed properly
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 3. Binding the parameters safely. The JDBC driver handles escaping special characters.
            pstmt.setLong(1, budget.getCategoryId());
            pstmt.setInt(2, budget.getMonth());
            pstmt.setInt(3, budget.getYear());
            pstmt.setBigDecimal(4, budget.getLimitAmount());

            // 4. Executing the query on the database
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Saving transaction failed, no rows affected.");
            }

        } catch (SQLException e) {
            // In a real robust system, we would log this error using SLF4J and rethrow a custom DomainException
            throw new RuntimeException("Error communicating with the database: " + e.getMessage(), e);
        }
    }
}
