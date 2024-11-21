package common.commands;

import common.iostream.Output;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.utils.JSONHandler;

import org.reflections.Reflections;

import java.util.Set;

public class HelpCommand implements BaseCommand {
    Output output = new OutputHandler();
    JSONHandler jsonHandler = new JSONHandler();

    // Получить короткое название команды
    public String getCommandName() {
        return "help";
    }

    // Получить описание команды
    public String getCommandDescription() {
        return "Справка по всем командам";
    }

    public boolean isHas(Set<Class<? extends BaseCommand>> subclasses, String commandName) {

        for (Class<? extends BaseCommand> subclass : subclasses) {
            if (subclass.getName().toLowerCase().equals(commandName)) {
                return true;
            }
        }
        return false;
    }

    // Вызвать основной методы команды
    public void run(Interaction interaction) {
        try {
            // Получаем в Set все классы, которые имеют интерфейс BaseCommand и находятся в common.commands
            Reflections reflections = new Reflections("common.commands");
            Set<Class<? extends BaseCommand>> subclasses = reflections.getSubTypesOf(BaseCommand.class);
            String commandName = interaction.getArguments().get(1);

            StringBuilder helpOutput;
            if (isHas(subclasses, commandName.toLowerCase())) {
                helpOutput = new StringBuilder("--------- HELP " + commandName + " ---------\n");
                helpOutput.append(jsonHandler.read("", "help.manual" + commandName.toLowerCase()));
                helpOutput.append("--------- HELP ").append(commandName).append(" ---------\n");

            } else {
                helpOutput = new StringBuilder("--------- HELP ---------\n");

                // Вывести короткое название и описание команды
                for (Class<? extends BaseCommand> subclass : subclasses) {
                    BaseCommand command = subclass.getConstructor().newInstance();
                    helpOutput.append(command.getCommandName()).append(":\n|---\t").append(command.getCommandDescription()).append("\n");
                }

                helpOutput.append("--------- HELP ---------\n");
            }

            output.output(interaction.setMessage(helpOutput.toString()).setInline(false));

        } catch (Exception err) {
            System.out.println("[ERROR] Error: " + err);
        }
    }
}
