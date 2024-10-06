package common.commands.moderation;

import common.commands.BaseCommand;

import java.util.List;

public class ResetWarnsCommand implements BaseCommand {

    public String getCommandName() {
        return "resetwarns";
    }

    public String getCommandDescription() {
        return "Очистить предупреждения";
    }

    public void run(List<String> args) {

    }
}
