package services;

import bot.MessageSender;
import dao.TransactionsRepo;
import domain.Budget;
import domain.Category;
import domain.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class BudgetService {

    private final TransactionsRepo transactionsRepo;
    private final MessageSender messageSender;


    public BudgetService (TransactionsRepo transactionsRepo, MessageSender messageSender) {
        this.transactionsRepo = transactionsRepo;
        this.messageSender = messageSender;
    }

    public void addExpense(Long userId, BigDecimal amount, String description, Category category, Budget currentBudget) {
        Transaction transaction = new Transaction(userId, category.getId(), amount, description);

        transactionsRepo.save(transaction);
        currentBudget.recordExpense(amount);

        if (currentBudget.isOverBudget()) {
            messageSender.sendMessage(userId, "Warning: You have exceeded your budget for " + category.getName() + "!");
        } else {
            messageSender.sendMessage(userId, "Expense recorded successfully.");
        }

        // TODO: Implement BudgetRepo to update budget in the DB
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
