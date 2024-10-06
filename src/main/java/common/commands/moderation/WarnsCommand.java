package common.commands.moderation;

import common.commands.BaseCommand;

import java.util.List;

public class WarnsCommand implements BaseCommand {

    public String getCommandName() {
        return "warns";
    }

    public String getCommandDescription() {
        return "Посмотреть список предупреждений";
    }

    public void run(List<String> args) {

    }
}
