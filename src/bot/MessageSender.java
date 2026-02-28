package bot;

public interface MessageSender {
    void sendMessage(Long chatId, String text);
}
