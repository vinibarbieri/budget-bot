package services;

import bot.MessageSender;
import dao.BudgetRepo;
import dao.TransactionsRepo;
import domain.Budget;
import domain.Category;
import domain.CategoryType;
import domain.Transaction;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public class BudgetService {

    private final TransactionsRepo transactionsRepo;
    private final BudgetRepo budgetRepo;
    private final MessageSender messageSender;


    public BudgetService (TransactionsRepo transactionsRepo, BudgetRepo budgetRepo, MessageSender messageSender) {
        this.transactionsRepo = transactionsRepo;
        this.budgetRepo = budgetRepo;
        this.messageSender = messageSender;
    }

    public void addTransaction(Long userId, BigDecimal amount, String description, Category category, Budget currentBudget) {

        boolean isExpense = category.getType() == CategoryType.EXPENSE;

        BigDecimal transactionAmount;
        if (isExpense) {
            transactionAmount = amount.abs().negate();
        } else {
            transactionAmount = amount.abs();
        }

        Transaction transaction = new Transaction(userId, category.getId(), transactionAmount, description);
        transactionsRepo.save(transaction);

        if (isExpense && currentBudget != null){
            currentBudget.recordExpense(amount.abs());

            if (currentBudget.isOverBudget()) {
                messageSender.sendMessage(userId, "Warning: You have exceeded your budget for " + category.getName() + "!");
            }
        }
        // Isolate bot logic look @calculateRemainingBudget
        messageSender.sendMessage(userId, "Transaction recorded successfully.");

    }

    public void createMonthlyBudget(Long userId, Long categoryId, Integer month, Integer year, BigDecimal limitAmount) {

        try {
            if (limitAmount.floatValue() <= 0 || month <= 0 || year <= 2025) {
                throw new IllegalArgumentException("Invalid arguments: Verify amount limit, month and year");
            }

            Budget newBudget = new Budget(categoryId, month, year, limitAmount);
            budgetRepo.save(newBudget);
            // Isolate bot logic look @calculateRemainingBudget
            messageSender.sendMessage(userId, limitAmount + "budget created for " +month + "/" +year);

        } catch (IllegalArgumentException e) {
            messageSender.sendMessage(userId, e.getMessage());
        }

    }

    public void getBalance(Long userId) {
        List<Transaction> allTransactions = transactionsRepo.findAllByUserId(userId);
        BigDecimal balance = BigDecimal.ZERO;

        for (Transaction currentTransaction : allTransactions) {
            balance = balance.add(currentTransaction.getAmount());
        }
        // Isolate bot logic look @calculateRemainingBudget
        messageSender.sendMessage(userId, "Your current balance is: " + balance);
    }

    public BigDecimal calculateRemainingBudget(Long userId, Category category, YearMonth period) {
        BigDecimal monthlySpentAmount = transactionsRepo.calculateSpentAmount(category.getId(), period);
        BigDecimal monthlyBudget = budgetRepo.monthlyBudget(category.getId(), period);

        return monthlyBudget.subtract(monthlySpentAmount);
    }
}
