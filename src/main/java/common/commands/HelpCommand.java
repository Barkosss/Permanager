package common.commands;

import common.iostream.Output;
import common.iostream.OutputHandler;
import common.models.InteractionTelegram;
import org.reflections.Reflections;

import java.util.Set;

public class HelpCommand implements BaseCommand {
    public Output output = new OutputHandler();

    // Получить короткое название команды
    public String getCommandName() {
        return "help";
    }

    // Получить описание команды
    public String getCommandDescription() {
        return "Справка по всем командам";
    }

    // Вызвать основной методы команды
    public void run(InteractionTelegram interaction) {
        try {

            // Получаем в Set все классы, которые имеют интерфейс BaseCommand и находятся в common.commands
            Reflections reflections = new Reflections("common.commands");
            Set<Class<? extends BaseCommand>> subclasses = reflections.getSubTypesOf(BaseCommand.class);
            StringBuilder helpOutput = new StringBuilder("--------- HELP ---------\n");

            // Вывести короткое название и описание команды
            for (Class<? extends BaseCommand> subclass : subclasses) {
                BaseCommand command = subclass.getConstructor().newInstance();
                helpOutput.append(command.getCommandName()).append(":\n|---\t").append(command.getCommandDescription()).append("\n");
            }

            helpOutput.append("--------- HELP ---------\n");

            output.output(interaction.setMessage(helpOutput.toString()).setInline(false));

        } catch (Exception err) {
            System.out.println("[ERROR] Error: " + err);
        }
    }
}
