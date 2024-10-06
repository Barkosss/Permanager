package common.commands;

public class EventCommand implements BaseCommand {

    public String getCommandName() {
        return "event";
    }

    public String getCommandDescription() {
        return "Управление мероприятиями";
    }

    public void run() {
        System.out.println("Class Event");
    }
}