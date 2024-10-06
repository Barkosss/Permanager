package common.commands;

import common.iostream.Output;
import common.iostream.OutputTerminal;
import org.reflections.Reflections;

import java.util.Set;

public class HelpCommand implements BaseCommand {
    public Output output = new OutputTerminal();

    public String getCommandName() {
        return "help";
    }

    public String getCommandDescription() {
        return "Справка по всем командам";
    }

    public void run() {
        try {
            Reflections reflections = new Reflections("common.commands");
            Set<Class<? extends BaseCommand>> subclasses = reflections.getSubTypesOf(BaseCommand.class);

            output.output("--------- HELP ---------", false);

            for (Class<? extends BaseCommand> subclass : subclasses) {
                BaseCommand command = subclass.getConstructor().newInstance();
                output.output(command.getCommandName() + ":\n|---\t" + command.getCommandDescription(), false);
            }

            output.output("--------- HELP ---------", false);
        } catch (Exception err) {
            System.out.println("[ERROR] Error: " + err);
        }
    }
}
