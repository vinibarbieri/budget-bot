package dao;

import domain.Transaction;
import java.util.List;

public interface TransactionsRepo {

    void save(Transaction transaction);

    List<Transaction> findAllByUserId(Long userId);

}