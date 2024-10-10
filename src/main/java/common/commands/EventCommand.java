package common.commands;

import java.util.List;

public class EventCommand implements BaseCommand {

    // Получить короткое название команды
    public String getCommandName() {
        return "event";
    }

    // Получить описание команды
    public String getCommandDescription() {
        return "Управление мероприятиями";
    }

    // Вызвать основной метод команды
    public void run(List<String> args) {

    }
}