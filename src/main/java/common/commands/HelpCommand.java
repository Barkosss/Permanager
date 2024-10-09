package common.commands;

import common.iostream.Output;
import common.iostream.OutputTerminal;
import org.reflections.Reflections;

import java.util.List;
import java.util.Set;

public class HelpCommand implements BaseCommand {
    public Output output = new OutputTerminal();

    // Получить короткое название команды
    public String getCommandName() {
        return "help";
    }

    // Получить описание команды
    public String getCommandDescription() {
        return "Справка по всем командам";
    }

    // Вызвать основной методы команды
    public void run(List<String> args) {
        try {

            // Получаем в Set все классы, которые имеют интерфейс BaseCommand и находятся в common.commands
            Reflections reflections = new Reflections("common.commands");
            Set<Class<? extends BaseCommand>> subclasses = reflections.getSubTypesOf(BaseCommand.class);

            output.output("--------- HELP ---------", false);

            // Вывести короткое название и описание команды
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
