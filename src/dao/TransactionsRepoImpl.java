package dao;

import domain.Transaction;
import infra.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class TransactionsRepoImpl implements TransactionsRepo {

    @Override
    public void save(Transaction transaction) {
        // 1. The SQL query uses '?' as placeholders for parameters.
        // This is the foundation of preventing SQL Injection.
        String sql = "INSERT INTO transactions (user_id, category_id, amount, description) VALUES (?, ?, ?, ?)";

        // 2. Try-with-resources to ensure both Connection and PreparedStatement are closed properly
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 3. Binding the parameters safely. The JDBC driver handles escaping special characters.
            pstmt.setLong(1, transaction.getUserId());
            pstmt.setLong(2, transaction.getCategoryId());
            pstmt.setBigDecimal(3, transaction.getAmount());
            pstmt.setString(4, transaction.getDescription());

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

    @Override
    public List<Transaction> findAllByUserId(Long userId) {
        // We will implement the SELECT query later. For now, let's focus on the INSERT.
        throw new UnsupportedOperationException("Not implemented yet");
    }
}