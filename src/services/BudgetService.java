package services;

import dao.BudgetRepo;
import dao.TransactionsRepo;
import domain.Budget;
import domain.Category;
import domain.CategoryType;
import domain.Transaction;

import java.math.BigDecimal;
import java.time.YearMonth;

public class BudgetService {

    private final TransactionsRepo transactionsRepo;
    private final BudgetRepo budgetRepo;

    public BudgetService(TransactionsRepo transactionsRepo, BudgetRepo budgetRepo) {
        this.transactionsRepo = transactionsRepo;
        this.budgetRepo = budgetRepo;
    }

    public Transaction addTransaction(Long userId, BigDecimal amount, String description, Category category) {
        boolean isExpense = category.getType() == CategoryType.EXPENSE;
        BigDecimal finalAmount = isExpense ? amount.abs().negate() : amount.abs();

        Transaction transaction = new Transaction(userId, category.getId(), finalAmount, description);
        transactionsRepo.save(transaction);

        return transaction;
    }

    public Budget createMonthlyBudget(Long categoryId, Integer month, Integer year, BigDecimal limitAmount) {
        if (limitAmount.compareTo(BigDecimal.ZERO) <= 0 || month <= 0 || month > 12 || year < 2025) {
            throw new IllegalArgumentException("Invalid arguments: Verify amount limit, month and year");
        }

        Budget newBudget = new Budget(categoryId, month, year, limitAmount);
        budgetRepo.save(newBudget);

        return newBudget;
    }

    public BigDecimal getBalance(Long userId) {
        return transactionsRepo.calculateTotalBalance(userId);
    }

    public BigDecimal calculateRemainingBudget(Category category, YearMonth period) {
        BigDecimal monthlySpentAmount = transactionsRepo.calculateSpentAmount(category.getId(), period);
        BigDecimal monthlyBudgetLimit = budgetRepo.monthlyBudget(category.getId(), period);

        return monthlyBudgetLimit.add(monthlySpentAmount);
    }
}