// File: bot/command/CommandRouter.java
package bot.command;

import bot.MessageSender;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

public class CommandRouter {
    // The Registry: Maps a string command (like "/budget") to its memory instance.
    private final Map<String, BotCommand> commandRegistry = new HashMap<>();
    private final MessageSender messageSender;

    public CommandRouter(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    /**
     * Registers a command into the router. This is usually called during the application startup.
     */
    public void registerCommand(String commandString, BotCommand commandHandler) {
        commandRegistry.put(commandString, commandHandler);
    }

    /**
     * Parses the raw text from Telegram and routes it to the correct command.
     * Example input: "/spend 50.00 FOOD"
     */
    public void route(Long userId, String rawMessage) {
        if (rawMessage == null || rawMessage.trim().isEmpty()) {
            return;
        }

        // Split the message by spaces
        String[] parts = rawMessage.trim().split("\\s+");
        String commandKey = parts[0].toLowerCase(); // e.g., "/spend"

        BotCommand command = commandRegistry.get(commandKey);

        if (command != null) {
            // Extract arguments (everything after the command itself)
            String[] args = Arrays.copyOfRange(parts, 1, parts.length);

            try {
                // Execute the concrete command
                command.execute(userId, args);
            } catch (Exception e) {
                // Global error handling for domain exceptions
                messageSender.sendMessage(userId, "Error processing your request: " + e.getMessage());
            }
        } else {
            // O(1) fallback for unknown commands
            messageSender.sendMessage(userId, "Unknown command. Type /help to see available options.");
        }
    }
}