package bot.command;

import bot.MessageSender;
import dao.CategoryRepo;
import domain.Category;
import services.BudgetService;

import java.math.BigDecimal;
import java.util.Optional;

public class AddTransactionCommand implements BotCommand {

    private final BudgetService budgetService;
    private final MessageSender messageSender;
    private final CategoryRepo categoryRepo;

    public AddTransactionCommand(BudgetService budgetService, MessageSender messageSender, CategoryRepo categoryRepo) {
        this.budgetService = budgetService;
        this.messageSender = messageSender;
        this.categoryRepo = categoryRepo;
    }

    @Override
    public void execute(Long userId, String[] args) {
        if (args.length < 2) {
            messageSender.sendMessage(userId, "Usage: /spend <CATEGORY> <AMOUNT>. Example: /spend FOOD 50.00");
            return;
        }

        try {
            String categoryName = args[0].toUpperCase();
            BigDecimal amount = new BigDecimal(args[1]);

            Optional<Category> catOpt = categoryRepo.findByNameAndUser(categoryName, userId);

            if (catOpt.isEmpty()) {
                messageSender.sendMessage(userId, "Category not found. Create it first!");
                return;
            }

            String description = args.length > 2 ? String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length)) : "No description";

            budgetService.addTransaction(userId, amount, description, catOpt.get());

            messageSender.sendMessage(userId, "✅ Spent $" + amount + " on " + categoryName);

        } catch (NumberFormatException e) {
            messageSender.sendMessage(userId, "Invalid amount format. Please use numbers like 50.00");
        } catch (Exception e) {
            messageSender.sendMessage(userId, "An error occurred while saving your transaction.");
        }
    }
}