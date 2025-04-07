package common.commands.custom;

import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.User;
import common.utils.JSONHandler;
import common.utils.LoggerHandler;

import java.util.List;
import java.util.Map;

public class HelpCommand implements BaseCommand {
    public Map<String, BaseCommand> methods;
    LoggerHandler logger = new LoggerHandler();
    OutputHandler output = new OutputHandler();
    JSONHandler jsonHandler = new JSONHandler();

    // Получить короткое название команды
    public String getCommandName() {
        return "help";
    }

    // Получить описание команды
    @Override
    public String getCommandDescription(Interaction interaction) {
        return interaction.getLanguageValue("commands." + getCommandName() + ".description");
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {
        List<String> arguments = interaction.getArguments();

        if (arguments.isEmpty()) {
            return;
        }

        String commandName = arguments.getFirst().toLowerCase();

        if (jsonHandler.check("manual_ru.json", String.format("manual.%s", commandName))) {
            user.setExcepted(getCommandName(), "commandName").setValue(commandName);
            return;
        }
        user.setExcepted(getCommandName(), "commandName")
                .setValue(interaction.getLanguageValue(".manualNotFound"));
    }

    // Вызвать основной методы команды
    public void run(Interaction interaction) {
        this.methods = interaction.getCommandRepository().getCommands();
        logger.debug("List of methods (classes, commandRepository) was received");

        User user = interaction.getUser(interaction.getUserId());
        parseArgs(interaction, user);

        boolean isCommand = false;
        if (!interaction.getArguments().isEmpty()) {
            isCommand = !((String) user.getValue(getCommandName(), "commandName")).isEmpty();
        }

        if (isCommand) {
            manual(interaction, user);
            return;
        }

        help(interaction, user);
    }

    public void help(Interaction interaction, User user) {
        try {
            StringBuilder helpOutput = new StringBuilder("--------- HELP ---------\n");

            // Вывести короткое название и описание команды
            for (String commandName : methods.keySet()) {
                helpOutput.append(String.format("/%s - %s\n",
                        commandName,
                        methods.get(commandName).getCommandDescription(interaction)));
            }

            helpOutput.append("--------- HELP ---------\n\n");
            helpOutput.append(interaction.getLanguageValue(".commandHelp"));
            logger.debug("The reference material has been generated");

            output.output(interaction.setMessage(String.valueOf(helpOutput)).setInline(false));
        } catch (Exception err) {
            logger.error(String.format("Error (helpCommand, help): %s", err));

        } finally {
            user.clearExpected(getCommandName());
        }
    }

    public void manual(Interaction interaction, User user) {
        try {
            String commandName = (String) user.getValue(getCommandName(), "commandName");
            StringBuilder helpOutput;

            String manual = (String) jsonHandler.read("manual_ru.json",
                    String.format("manual.%s.%s", commandName, interaction.getLanguageCode().getLang()));

            if (manual.isEmpty()) {
                manual = interaction.getLanguageValue(".notFoundManual");
            }

            helpOutput = new StringBuilder("--------- HELP \"").append(commandName).append("\" ---------\n");
            helpOutput.append(manual);
            helpOutput.append("\n--------- HELP \"").append(commandName).append("\" ---------\n");

            output.output(interaction.setMessage(String.valueOf(helpOutput)).setInline(false));
        } catch (Exception err) {
            logger.error(String.format("Error (helpCommand, manual): %s", err));

        } finally {
            user.clearExpected(getCommandName());
        }
    }
}
