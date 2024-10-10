package common;

public class Main {
    // ntcn
    public static void main(String[] args) {

        CommandHandler commandHandler = new CommandHandler();
        commandHandler.commandLoader(); // Загружаем все команды
        commandHandler.getCommand(); // Вызываем команды
    }
}