package dao;

import domain.Category;
import domain.Transaction;
import infra.DatabaseManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;

public class TransactionsRepoImpl implements TransactionsRepo {

    @Override
    public void save(Transaction transaction) {
        String sql = "INSERT INTO transactions (user_id, category_id, amount, description) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, transaction.getUserId());
            pstmt.setLong(2, transaction.getCategoryId());
            pstmt.setBigDecimal(3, transaction.getAmount());
            pstmt.setString(4, transaction.getDescription());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Saving transaction failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error communicating with the database: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Transaction> findAllByUserId(Long userId) {
        // We will implement the SELECT query later. For now, let's focus on the INSERT.
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public BigDecimal calculateSpentAmount(Long categoryId, YearMonth period){
        // 1. The query with '?' placeholders.
        // We send this structure FIRST so the PostgreSQL engine can parse it,
        // validate the syntax, and compile an Abstract Syntax Tree (AST) and Execution Plan.
        String sql  = "SELECT SUM(amount) FROM transactions " +
                "WHERE category_id = ? " +
                "AND EXTRACT (MONTH FROM transaction_date) = ? " +
                "AND EXTRACT (YEAR FROM transaction_date) = ?";

        // 2. Try-With-Resources block (Automatic Memory/Resource Management).
        // - conn: Borrows an already open TCP socket from the HikariCP pool.
        // - pstmt: Acts as a remote pointer to the pre-compiled Execution Plan in the DB's RAM.
        // This block guarantees that .close() is called for both, preventing resource
        // and socket leaks in the constrained Raspberry Pi environment.
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 3. Parameter Binding (SQL Injection prevention).
            // Instead of concatenating strings, we send the actual parameters as binary packets
            // over the network. The DB engine inserts these directly into the pre-compiled AST.
            // It's structurally impossible for malicious text to be executed as logic here.
            pstmt.setLong(1, categoryId);
            pstmt.setInt(2, period.getMonthValue());
            pstmt.setInt(3, period.getYear());

            // 4. ResultSet Execution.
            // executeQuery() sends the bound binary data and triggers the DB to run the plan.
            // The ResultSet 'rs' is NOT a full dump of the table in the Java RAM.
            // It's a database cursor (a pointer) that streams the results over the network.
            try (ResultSet rs = pstmt.executeQuery()) {

                // Moves the cursor to the first row of the result.
                // rs.next() = false when there is no more rows
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal(1);

                    return total != null ? total : BigDecimal.ZERO;
                }
            }

        } catch (SQLException e) {
            // 5. Exception Translation.
            // We catch the infrastructure-level exception (SQLException) and throw a
            // domain-level RuntimeException so the Service layer doesn't depend on JDBC APIs.
            throw new RuntimeException("Fail on calculating the expenses in DB", e);
        }

        return BigDecimal.ZERO;
    }
}