package dao;

import domain.Category;
import domain.Transaction;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public interface TransactionsRepo {

    void save(Transaction transaction);

    List<Transaction> findAllByUserId(Long userId);

    BigDecimal calculateSpentAmount(Long categoryId, YearMonth period);

}