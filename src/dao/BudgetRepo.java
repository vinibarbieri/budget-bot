package dao;

import domain.Budget;

import java.math.BigDecimal;
import java.time.YearMonth;

public interface BudgetRepo {
    void save(Budget budget);

    BigDecimal montlhyBudget(Long categoryId, YearMonth period);
}
