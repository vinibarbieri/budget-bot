package services;

import bot.MessageSender;
import dao.BudgetRepo;
import dao.TransactionsRepo;
import domain.Budget;
import domain.Category;
import domain.CategoryType;
import domain.Transaction;

import java.math.BigDecimal;
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

        messageSender.sendMessage(userId, "Transaction recorded successfully.");

        budgetRepo.save(currentBudget);
    }

    public void getBalance(Long userId) {
        List<Transaction> allTransactions = transactionsRepo.findAllByUserId(userId);
        BigDecimal balance = BigDecimal.ZERO;

        for (Transaction currentTransaction : allTransactions) {
            balance = balance.add(currentTransaction.getAmount());
        }

        messageSender.sendMessage(userId, "Your current balance is: " + balance);
    }
}
