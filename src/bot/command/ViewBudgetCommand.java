package bot.command;

import bot.MessageSender;
import dao.CategoryRepo;
import domain.Category;
import services.BudgetService;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

public class ViewBudgetCommand implements BotCommand {

    private final BudgetService budgetService;
    private final MessageSender messageSender;
    private final CategoryRepo categoryRepo;

    public ViewBudgetCommand(BudgetService budgetService, MessageSender messageSender, CategoryRepo categoryRepo) {
        this.budgetService = budgetService;
        this.messageSender = messageSender;
        this.categoryRepo = categoryRepo;
    }

    @Override
    public void execute(Long userId, String[] args) {
        if (args.length < 1) {
            messageSender.sendMessage(userId, "Please provide a category. Example: /budget FOOD");
            return;
        }

        try {
            String categoryName = args[0].toUpperCase();
            Optional<Category> catOpt = categoryRepo.findByNameAndUser(categoryName, userId);

            if (catOpt.isEmpty()) {
                messageSender.sendMessage(userId, "Category not found.");
                return;
            }

            BigDecimal remaining = budgetService.calculateRemainingBudget(catOpt.get(), YearMonth.now());

            messageSender.sendMessage(userId, "Your remaining budget for " + categoryName + " is: $" + remaining);
        } catch (Exception e) {
            messageSender.sendMessage(userId, "An error occurred while viewing your budget.");
        }
    }
}