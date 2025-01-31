package common.commands.moderation;

import com.pengrad.telegrambot.model.ChatFullInfo;
import com.pengrad.telegrambot.request.GetChat;
import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.Permissions;
import common.models.Restrictions;
import common.models.Server;
import common.models.User;
import common.utils.LoggerHandler;

import java.util.List;
import java.util.stream.Stream;

public class ConfigCommand implements BaseCommand {
    private final OutputHandler output = new OutputHandler();
    private final LoggerHandler logger = new LoggerHandler();

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
            arguments = arguments.subList(1, arguments.size());
        }

        if (arguments.isEmpty()) {
            return;
        }

        switch ((String) user.getValue(getCommandName(), "section")) {
            case "dashboard": {
                // Dashboard...
                user.setExcepted(getCommandName(), "dashboardAction").setValue(String.join(" ", arguments));
                break;
            }

            case "group": {
                // Group...
                user.setExcepted(getCommandName(), "dashboardAction").setValue(String.join(" ", arguments));
                break;
            }

            case "user": {
                // User...
                user.setExcepted(getCommandName(), "dashboardAction").setValue(String.join(" ", arguments));
                break;
            }
        }
    }

    @Override
    public void run(Interaction interaction) {
        if (interaction.getPlatform() == Interaction.Platform.CONSOLE) {
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandConsole"));
            return;
        }

        InteractionTelegram interactionTelegram = (InteractionTelegram) interaction;

        // Проверяем на приватность чата
        if (interactionTelegram.telegramBot
                .execute(new GetChat(interaction.getChatId())).chat().type() == ChatFullInfo.Type.Private) {
            logger.info(String.format("User by id(%d) use command \"%s\" in Chat by id(%d)",
                    interaction.getUserId(), getCommandName(), interaction.getChatId()));
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandPrivateChat"));
            return;
        }

        User user = interaction.getUser(interaction.getUserId());
        parseArgs(interaction, user);


        logger.debug(String.format("Checking the user's access rights (%s) by id(%s) in the chat by id(%s)",
                Permissions.Permission.CONFIG.getPermission(), user.getUserId(), interaction.getChatId()));
        if (!user.hasPermission(interaction.getChatId(), Permissions.Permission.CONFIG)) {
            try {
                logger.info(String.format("The user by id(%s) doesn't have access rights (%s) in chat by id(%s)",
                        user.getUserId(), Permissions.Permission.CONFIG.getPermission(), interaction.getChatId()));
                output.output(interaction.setLanguageValue("system.error.accessDenied",
                        List.of(((InteractionTelegram) interaction).getUsername())));
            } catch (Exception err) {
                logger.error(String.format("Get User from reply message (Config): %s", err));
            }
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
                dashboard(interactionTelegram, user);
                break;
            }
            case "user": {
                logger.debug("Run method \"user\" in config command");
                user(interactionTelegram, user);
                break;
            }
            case "group": {
                logger.debug("Run method \"group\" in config command");
                group(interactionTelegram, user);
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

    private void dashboard(InteractionTelegram interaction, User user) {

        if (!user.isExceptedKey(getCommandName(), "dashboardAction")) {
            user.setExcepted(getCommandName(), "dashboardAction");
            output.output(interaction.setMessage(interaction.getLanguageValue("config.dashboard.start")));
            logger.info("Config command requested a dashboard action");
            return;
        }

        String action = ((String) user.getValue(getCommandName(), "dashboardAction")).toLowerCase().trim();
        switch (action) {
            case "default right access": {
                // Настройка стандартных прав доступа
                configDefaultRightAccess(interaction, user);
                break;
            }

            case "default limits": {
                // Настройка стандартных ограничений
                configDefaultLimits(interaction, user);
                break;
            }

            case "moderation commands": {
                // Настройка команд
                configModerationCommand(interaction, user);
                break;
            }

            default: { // Если пользователь указал неправильный аргумент
                user.setExcepted(getCommandName(), "dashboardAction");
                output.output(interaction.setMessage(interaction.getLanguageValue("config.dashboard.start")));
                logger.info("Config command requested a dashboard action");
                return;
            }
        }
        user.clearExpected(getCommandName());
    }

    // Настройка стандартных прав доступа
    private void configDefaultRightAccess(InteractionTelegram interaction, User user) {
        Server server = interaction.findServerById(interaction.getChatId());
        String defaultRightAccess = ".dashboard.defaultRightAccess";
        Permissions serverDefaultPermissions = server.getDefaultPermissions();
        try {
            String message = interaction.getLanguageValue(defaultRightAccess + ".title")
                    + "\n\n"
                    + interaction.getLanguageValue(defaultRightAccess + ".description")
                    + "\n"
                    + interaction.getLanguageValue(defaultRightAccess + ".permissions",
                    Stream.of(
                                    serverDefaultPermissions.getCanBan(),
                                    serverDefaultPermissions.getCanUnban(),
                                    serverDefaultPermissions.getCanKick(),
                                    serverDefaultPermissions.getCanMute(),
                                    serverDefaultPermissions.getCanUnMute(),
                                    serverDefaultPermissions.getCanWarn(),
                                    serverDefaultPermissions.getCanRemWarn(),
                                    serverDefaultPermissions.getCanResetWarn(),
                                    serverDefaultPermissions.getCanClear()
                            ).map(permission -> interaction.getLanguageValue("system." + permission))
                            .toList()
            );

            output.output(interaction.setMessage(String.valueOf(message)));

        } catch (Exception err) {
            logger.error("Default right access (Config) an error occurred: " + err);
            output.output(interaction.setLanguageValue("system.error.something"));
        }
    }

    public void configDefaultLimits(InteractionTelegram interaction, User user) {
        Server server = interaction.findServerById(interaction.getChatId());
        String defaultLimits = ".dashboard.defaultLimits";
        Restrictions serverDefaultLimits = server.getDefaultRestrictions();

        try {
            String message = interaction.getLanguageValue(defaultLimits + ".title")
                    + "\n\n"
                    + interaction.getLanguageValue(defaultLimits + ".description")
                    + "\n"
                    + interaction.getLanguageValue(defaultLimits + ".restrictions",
                    Stream.of(
                            serverDefaultLimits.getLimitKick(),
                            serverDefaultLimits.getLimitBan(),
                            serverDefaultLimits.getLimitUnban(),
                            serverDefaultLimits.getLimitMute(),
                            serverDefaultLimits.getLimitUnMute(),
                            serverDefaultLimits.getLimitWarn(),
                            serverDefaultLimits.getLimitRemWarn(),
                            serverDefaultLimits.getLimitResetWarn(),
                            serverDefaultLimits.getLimitClear(),
                            serverDefaultLimits.getLimitGiveTempRole()
                    ).flatMap(limit -> Stream.of(
                            String.valueOf(limit.amountUses),
                            String.valueOf(limit.timestampPeriod)
                    )).toList());


            output.output(interaction.setMessage(message));

        } catch (Exception err) {
            logger.error("...");
            output.output(interaction.setLanguageValue("system.error.something"));
        }
    }

    public void configModerationCommand(InteractionTelegram interaction, User user) {

    }

    private void user(InteractionTelegram interaction, User user) {
        output.output(interaction.setMessage("Settings of user"));
        user.clearExpected(getCommandName());
    }

    private void group(InteractionTelegram interaction, User user) {
        output.output(interaction.setMessage("Settings of group"));
        user.clearExpected(getCommandName());
    }
}
