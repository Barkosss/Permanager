package common.commands;


import java.util.List;

public class TaskCommand implements BaseCommand {

    public String getCommandName() {
        return "task";
    }

    public String getCommandDescription() {
        return "Управление задачами";
    }

    public void run(List<String> args) {

    }
}
