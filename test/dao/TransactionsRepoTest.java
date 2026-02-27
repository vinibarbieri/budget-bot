package dao;

import domain.Transaction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

public class TransactionsRepoTest {

    @Test
    public void testSaveNewTransactionSuccessfully() {
        // 1. Setup: Instantiating the implementation of our DAO
        TransactionsRepo repo = new TransactionsRepoImpl();

        // 2. Arrange: Creating the domain object
        // IMPORTANT: hardcoding userId = 1L and categoryId = 1L.
        // These IDs MUST exist in users and categories tables in Postgres!
        Transaction expense = new Transaction(
                1L,                                  // userId
                1L,                                  // categoryId
                new BigDecimal("150.00"),            // amount
                "Nespresso capsules and Absolut"     // description
        );

        // 3. Act & Assert: The test passes if the save method executes cleanly without SQLExceptions
        assertDoesNotThrow(() -> {

            repo.save(expense);

        }, "The repository should save the transaction without throwing exceptions. " +
                "If it fails, check if userId 1 and categoryId 1 exist in the database.");
    }
}