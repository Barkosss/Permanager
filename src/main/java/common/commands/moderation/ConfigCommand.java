package common.commands.moderation;

import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.Permissions;
import common.models.Server;
import common.models.User;
import common.utils.LoggerHandler;

import java.util.List;

public class ConfigCommand implements BaseCommand {
    OutputHandler output = new OutputHandler();
    LoggerHandler logger = new LoggerHandler();

    @Override
    public String getCommandName() {
        return "config";
    }

    @Override
    public String getCommandDescription() {
        return "";
    }

    public void parseArgs(Interaction interaction, User user) {
        List<String> arguments = interaction.getArguments();

        if (arguments.isEmpty()) {
            return;
        }

        String argument = arguments.getFirst().toLowerCase();
        if (List.of("dashboard", "user", "group").contains(argument)) {
            user.setExcepted(getCommandName(), "section").setValue(argument);
            logger.debug(String.format("Parse arguments from chatId(%s, userId=%s), with argument=%s",
                    interaction.getChatId(), interaction.getUserId(), argument));
        }
    }

    @Override
    public void run(Interaction interaction) {
        if (interaction.getPlatform() == Interaction.Platform.CONSOLE) {
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandConsole"));
            return;
        }

        User user = interaction.getUser(interaction.getUserId());
        parseArgs(interaction, user);

        if (!user.hasPermission(interaction.getChatId(), Permissions.Permission.CONFIG)) {
            output.output(interaction.setLanguageValue("system.error.accessDenied"));
            return;
        }

        if (!user.isExceptedKey(getCommandName(), "section")) {
            user.setExcepted(getCommandName(), "section");
            output.output(interaction.setMessage(interaction.getLanguageValue("config.start.section")).setInline(true));
            logger.debug("Config command requested a section argument");
            return;
        }

        switch (((String) user.getValue(getCommandName(), "section")).toLowerCase()) {
            case "dashboard": {
                logger.debug("Run method \"dashboard\" in config command");
                dashboard(interaction, user);
                break;
            }
            case "user": {
                logger.debug("Run method \"user\" in config command");
                user(interaction, user);
                break;
            }
            case "group": {
                logger.debug("Run method \"group\" in config command");
                group(interaction, user);
                break;
            }

            default: {
                user.setExcepted(getCommandName(), "section");
                output.output(interaction.setMessage(interaction.getLanguageValue("config.start.againSection"))
                        .setInline(true));
                logger.debug("Config command requested a section argument");
                break;
            }
        }
    }

    private void dashboard(Interaction interaction, User user) {

        if (!user.isExceptedKey(getCommandName(), "dashboardAction")) {
            user.setExcepted(getCommandName(), "dashboardAction");
            output.output(interaction.setMessage(interaction.getLanguageValue("config.dashboard.start")));
            logger.info("Config command requested a dashboard action");
            return;
        }

        String action = ((String) user.getValue(getCommandName(), "dashboardAction")).toLowerCase();
        switch (action) {
            case "default right access": {
                configDefaultRightAccess(interaction, user);
                break;
            }

            case "default limits": {
                // Настройка стандартных ограничений
                break;
            }

            case "moderation commands": {
                // Настройка команд
                break;
            }

            default: { // Если пользователь указал неправильный аргумент
                user.setExcepted(getCommandName(), "dashboardAction");
                output.output(interaction.setMessage(interaction.getLanguageValue("config.dashboard.start")));
                logger.info("Config command requested a dashboard action");
                break;
            }
        }
        output.output(interaction.setMessage(interaction.getLanguageValue("config.dashboard")));
        user.clearExpected(getCommandName());
    }

    // Настройка стандартных прав доступа
    private void configDefaultRightAccess(Interaction interaction, User user) {
        Server server = interaction.findServerById(interaction.getChatId());
        String defaultRightAccess = "config.dashboard.defaultRightAccess";
        StringBuilder message = new StringBuilder(interaction.getLanguageValue(defaultRightAccess + ".title"));

        message.append(interaction.getLanguageValue(defaultRightAccess + "description"));
        // ...

        output.output(interaction.setMessage(String.valueOf(message)));
    }

    private void user(Interaction interaction, User user) {
        output.output(interaction.setMessage("Settings of user"));
        user.clearExpected(getCommandName());
    }

    private void group(Interaction interaction, User user) {
        output.output(interaction.setMessage("Settings of group"));
        user.clearExpected(getCommandName());
    }
}
