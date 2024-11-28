package common.commands.custom;

import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.User;
import common.utils.JSONHandler;
import common.utils.LoggerHandler;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map;

public class HelpCommand implements BaseCommand {
    Map<String, String> methods;
    LoggerHandler logger = new LoggerHandler();
    OutputHandler output = new OutputHandler();
    JSONHandler jsonHandler = new JSONHandler();

    public HelpCommand() {
        try {
            this.methods = new HashMap<>();

            // Получаем в Set все классы, которые имеют интерфейс BaseCommand и находятся в common.commands
            Reflections reflections = new Reflections("common.commands");
            Set<Class<? extends BaseCommand>> subclasses = reflections.getSubTypesOf(BaseCommand.class);

            // Вывести короткое название и описание команды
            for (Class<? extends BaseCommand> subclass : subclasses) {
                BaseCommand command = subclass.getConstructor().newInstance();
                methods.put(command.getCommandName(), command.getCommandDescription());
            }

        } catch(Exception err) {
            logger.error("Help constructors: " + err);
        }
    }

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
        List<String> arguments = interaction.getArguments();

        if (arguments.isEmpty()) {
            return;
        }

        String commandName = arguments.getFirst().toLowerCase();

        if (jsonHandler.check("manual.json", "manual." + commandName)) {
            user.setExcepted(getCommandName(), "commandName").setValue(commandName);
            return;
        }
        user.setExcepted(getCommandName(), "commandName").setValue("");
    }

    // Вызвать основной методы команды
    public void run(Interaction interaction) {
        User user = interaction.getUser(interaction.getUserId());
        parseArgs(interaction, user);

        boolean isCommand = false;
        if (!interaction.getArguments().isEmpty()) {
            isCommand = !((String) user.getValue(getCommandName(), "commandName")).isEmpty();
        }

        if (isCommand) {
            manual(interaction, user);
        } else {
            help(interaction, user);
        }
    }

    public void help(Interaction interaction, User user) {
        try {
            StringBuilder helpOutput = new StringBuilder("--------- HELP ---------\n");

            // Вывести короткое название и описание команды
            for (String commandName : methods.keySet()) {
                helpOutput.append(commandName).append(":\n|---\t")
                        .append(methods.get(commandName)).append("\n");
            }

            helpOutput.append("--------- HELP ---------\n");
            output.output(interaction.setMessage(String.valueOf(helpOutput)).setInline(false));

        } catch (Exception err) {
            logger.error("Error (helpCommand, help): " + err);
        }

        user.clearExpected(getCommandName());
    }

    public void manual(Interaction interaction, User user) {
        try {
            String commandName = (String) user.getValue(getCommandName(), "commandName");
            StringBuilder helpOutput;

            String manual = (String) jsonHandler.read("manual.json", "manual." + commandName
                    + "." + interaction.getLanguageCode().getLang());

            if (manual.isEmpty()) {
                manual = interaction.getLanguageValue("help.notFoundManual");
            }

            helpOutput = new StringBuilder("--------- HELP \"" + commandName + "\" ---------\n");
            helpOutput.append(manual);
            helpOutput.append("\n--------- HELP \"").append(commandName).append("\" ---------\n");

            output.output(interaction.setMessage(String.valueOf(helpOutput)).setInline(false));
            user.clearExpected(getCommandName());
        } catch (Exception err) {
            logger.error("Error (helpCommand, manual): " + err);
        }
    }
}
