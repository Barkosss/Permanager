package common.commands;


import java.util.List;

public class TaskCommand implements BaseCommand {

    // Получить короткое название команды
    public String getCommandName() {
        return "task";
    }

    // Описание команды
    public String getCommandDescription() {
        return "Управление задачами";
    }

    // Вызвать основной метод команды
    public void run(List<String> args) {

    }
}
