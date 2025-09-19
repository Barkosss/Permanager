package common.models;

public class InteractionConsole extends AbstractInteraction {

    public Interaction setChatId(long chatId) {
        this.chatId = chatId;
        return this;
    }

    public InteractionConsole() {
        this.userId = 0L;
        this.languageCode = Language.ENGLISH;
        this.platform = Platform.CONSOLE;
    }

    public InteractionConsole setMessage(String message) {
        this.message = message;
        return this;
    }

    // For Debug
    @Override
    public String toString() {
        return String.format("InteractionConsole={Platform=%s; Message=%s; Inline=%s; Arguments=%s}",
                platform, message, inline, message);
    }
}
