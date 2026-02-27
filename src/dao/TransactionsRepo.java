package dao;

import domain.Transaction;
import java.util.List;

public interface TransactionsRepo {

    // Saves a new financial transaction into the database
    void save(Transaction transaction);

    // Retrieves all transactions for a specific user
    List<Transaction> findAllByUserId(Long userId);

}