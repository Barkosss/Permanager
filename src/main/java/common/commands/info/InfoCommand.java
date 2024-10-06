package common.commands.info;

import common.commands.BaseCommand;

import java.util.List;

public class InfoCommand implements BaseCommand {

    public String getCommandName() {
        return "info";
    }

    public String getCommandDescription() {
        return "Посмотреть информацию о боте";
    }

    public void run(List<String> args) {

    }
}
