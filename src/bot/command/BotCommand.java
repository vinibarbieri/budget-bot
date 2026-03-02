package bot.command;

public interface BotCommand {
    /**
     * Executes the specific bot command.
     * * @param userId The Telegram User ID (used to send the reply).
     * @param args   The arguments passed after the command (e.g., ["50.00", "FOOD"]).
     */
    void execute(Long userId, String[] args);
}
