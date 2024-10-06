package common.commands;

public interface BaseCommand {

    void run();

    String getCommandName();

    String getCommandDescription();
}
