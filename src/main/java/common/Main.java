package common;

public class Main {
    public static void main(String[] args) {
        CommandHandler commandHandler = new CommandHandler();
        commandHandler.commandLoader();
        commandHandler.getCommand();
    }
}