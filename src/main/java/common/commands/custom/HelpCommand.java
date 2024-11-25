package common.commands.custom;

import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.User;
import common.utils.JSONHandler;
import org.reflections.Reflections;

import java.util.Set;

public class HelpCommand implements BaseCommand {
    OutputHandler output = new OutputHandler();
    JSONHandler jsonHandler = new JSONHandler();

    // Получить короткое название команды
    public String getCommandName() {
        return "help";
    }

    // Получить описание команды
    public String getCommandDescription() {
        return "Справка по всем командам";
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {

    }

    // Вызвать основной методы команды
    public void run(Interaction interaction) {
        try {
            // Получаем в Set все классы, которые имеют интерфейс BaseCommand и находятся в common.commands
            Reflections reflections = new Reflections("common.commands");
            Set<Class<? extends BaseCommand>> subclasses = reflections.getSubTypesOf(BaseCommand.class);
            String commandName = "";
            if (!interaction.getArguments().isEmpty()) {
                commandName = interaction.getArguments().getFirst().toLowerCase();
            }

            StringBuilder helpOutput;
            if (!commandName.isEmpty() && jsonHandler.check("manual.json", "help.manual." + commandName)) {
                helpOutput = new StringBuilder("--------- HELP \"" + commandName + "\" ---------\n");
                helpOutput.append(jsonHandler.read("manual.json", "help.manual." + commandName));
                helpOutput.append("\n--------- HELP \"").append(commandName).append("\" ---------\n");

            } else {
                helpOutput = new StringBuilder("--------- HELP ---------\n");

                // Вывести короткое название и описание команды
                for (Class<? extends BaseCommand> subclass : subclasses) {
                    BaseCommand command = subclass.getConstructor().newInstance();
                    helpOutput.append(command.getCommandName()).append(":\n|---\t")
                            .append(command.getCommandDescription()).append("\n");
                }

                helpOutput.append("--------- HELP ---------\n");
            }

            output.output(interaction.setMessage(String.valueOf(helpOutput)).setInline(false));

        } catch (Exception err) {
            System.out.println("[ERROR] Error (helpCommand): " + err);
        }
    }
}
