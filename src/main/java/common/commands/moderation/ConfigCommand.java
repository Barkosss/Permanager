package common.commands.moderation;

import com.pengrad.telegrambot.model.ChatFullInfo;
import com.pengrad.telegrambot.request.GetChat;
import common.commands.AbstractCommand;
import common.enums.ModerationCommand;
import common.iostream.OutputHandler;
import common.models.Group;
import common.models.InputExpectation;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.Limit;
import common.models.Member;
import common.models.Permissions;
import common.models.Restrictions;
import common.models.Server;
import common.models.User;
import common.utils.LoggerHandler;
import common.utils.ValidateService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class ConfigCommand extends AbstractCommand {
    private final OutputHandler output = new OutputHandler();
    private final LoggerHandler logger = new LoggerHandler();
    private final ValidateService validate = new ValidateService();

    @Override
    public String getCommandName() {
        return "config";
    }

    @Override
    public String getCommandDescription(Interaction interaction) {
        return interaction.getLanguageValue("commands." + getCommandName() + ".description");
    }

    public void parseArgs(Interaction interaction, User user) {
        List<String> arguments = interaction.getArguments();

        if (arguments.isEmpty()) {
            return;
        }

        String strChatId = arguments.getFirst().toLowerCase();
        try {
            Long chatId = Long.parseLong(strChatId);
            user.setExcepted(getCommandName(), "chatId").setValue(chatId);
        } catch (NumberFormatException ignore) {
        }

        String argument = arguments.getFirst().toLowerCase();
        List<String> validSections = List.of("dashboard", "user", "group");

        if (validSections.contains(argument)) {
            user.setExcepted(getCommandName(), "section").setValue(argument);
            logger.debug(String.format("Parse arguments from chatId(%s, userId=%s), with argument=%s",
                    interaction.getChatId(), interaction.getUserId(), argument));
            arguments = arguments.subList(1, arguments.size());
        }

        if (arguments.isEmpty()) {
            return;
        }

        Object section = user.getValue(getCommandName(), "section");
        if (section instanceof String && validSections.contains((String) section)) {
            user.setExcepted(getCommandName(), "dashboardAction").setValue(String.join(" ", arguments));
            logger.debug(String.format("Parsed dashboardAction for section=%s from chatId=%s, userId=%s, args=%s",
                    section, interaction.getChatId(), interaction.getUserId(), arguments));
        }
    }

    @Override
    public void run(Interaction interaction) {
        if (interaction.getPlatform() == Interaction.Platform.CONSOLE) {
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandConsole"));
            return;
        }

        InteractionTelegram interactionTelegram = (InteractionTelegram) interaction;
        long chatId = interaction.getChatId();
        long userId = interaction.getUserId();

        User user = interaction.getUser(userId);

        // Проверяем на приватность чата
        Long targetChatId = (Long) user.getValue(getCommandName(), "chatId");
        if (targetChatId == null && interactionTelegram.execute(new GetChat(chatId)).chat().type() == ChatFullInfo.Type.Private) {
            logger.info(String.format("User by id(%d) use command \"%s\" in Chat by id(%d)",
                    userId, getCommandName(), chatId));
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandPrivateChat"));
            return;
        }

        // TODO: Check, user is admin or owner in target chat (targetChatId)
        Server server = interactionTelegram.findServerById(targetChatId);
        Long ownerId = server.getOwnerId();

        if (userId != ownerId) {
            logger.info(String.format("User by id(%s) user command \"%s\" for chat by id(%s), but not owner by id(%s)",
                    userId, getCommandName(), targetChatId, ownerId));
            output.output(interaction.setLanguageValue("system.error.notAvailableChatOwner"));
            return;
        }

        String commandName = getCommandName();
        String permissionName = ModerationCommand.CONFIG.getCommandName();

        logger.debug(String.format("Checking the user's access rights (%s) by id(%s) in the chat by id(%s)",
                permissionName, user.getUserId(), chatId));
        if (!user.hasPermission(chatId, ModerationCommand.CONFIG)) {
            try {
                logger.info(String.format("Access denied: userId=%d lacks permission \"%s\" in chatId=%d",
                        user.getUserId(), permissionName, chatId));
                output.output(interaction.setLanguageValue("system.error.accessDenied",
                        List.of(((InteractionTelegram) interaction).getUsername())));
            } catch (Exception err) {
                logger.error(String.format("Error while retrieving user from reply message (Config): %s", err));
            }
            return;
        }

        if (!user.isExceptedKey(commandName, "section")) {
            user.setExcepted(commandName, "section");
            output.output(interaction.setLanguageValue("config.start.section").setInline(true));
            logger.debug("Config command requested a section argument");
            return;
        }

        String section = ((String) user.getValue(getCommandName(), "section")).toLowerCase();
        logger.debug(String.format("Running section \"%s\" in config command", section));

        switch (section) {
            case "dashboard" -> dashboard(interactionTelegram, user);
            case "user" -> user(interactionTelegram, user);
            case "group" -> group(interactionTelegram, user);
            default -> {
                user.setExcepted(getCommandName(), "section");
                output.output(interaction.setLanguageValue("config.start.againSection").setInline(true));
                logger.debug("Config command requested a valid section argument again");
            }
        }
    }

    private void dashboard(InteractionTelegram interaction, User user) {
        String commandName = getCommandName();

        if (!user.isExceptedKey(commandName, "dashboardAction")) {
            user.setExcepted(commandName, "dashboardAction");
            output.output(interaction.setLanguageValue("config.dashboard.start"));
            logger.info("Dashboard command requested an action input");
            return;
        }

        String action = ((String) user.getValue(commandName, "dashboardAction")).toLowerCase().trim();

        switch (action) {
            // Настройка стандартных прав доступа
            case "default right access" -> {
                configDashboardDefaultRightAccess(interaction);
                logger.info("Dashboard command: configured default right access");
            }

            // Настройка стандартных ограничений
            case "default limits" -> {
                configDashboardDefaultLimits(interaction);
                logger.info("Dashboard command: configured default limits");
            }

            // Настройка команд
            case "moderation commands" -> {
                configDashboardModerationCommands(interaction);
                logger.info("Dashboard command: configured moderation commands");
            }

            // Если пользователь указал неправильный аргумент
            default -> {
                user.setExcepted(commandName, "dashboardAction");
                output.output(interaction.setLanguageValue("config.dashboard.start"));
                logger.warning(String.format("Dashboard command received an unknown action: %s", action));
                return;
            }
        }
        user.clearExpected(commandName);
    }

    // Настройка стандартных прав доступа
    private void configDashboardDefaultRightAccess(InteractionTelegram interaction) {
        Server server = interaction.findServerById(interaction.getChatId());
        String defaultRightAccess = ".dashboard.defaultRightAccess";
        Permissions serverDefaultPermissions = server.getDefaultPermissions();

        try {
            List<String> localizedPermissions = Stream.of(
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
                    .toList();

            String message = String.format(
                    "%s\n\n%sn%s",
                    interaction.getLanguageValue(defaultRightAccess + ".title"),
                    interaction.getLanguageValue(defaultRightAccess + ".description"),
                    interaction.getLanguageValue(defaultRightAccess + ".permissions", localizedPermissions)
            );

            output.output(interaction.setMessage(message));
            logger.info(String.format("Displayed default permissions dashboard for chat by id(%s)",
                    interaction.getChatId()));


        } catch (Exception err) {
            logger.error(String.format("Failed to display default permissions dashboard for chat by id(%s): %s",
                    interaction.getChatId(), err.getMessage()));
            output.output(interaction.setLanguageValue("system.error.something"));
        }
    }

    private void configDashboardDefaultLimits(InteractionTelegram interaction) {
        Server server = interaction.findServerById(interaction.getChatId());
        String defaultLimits = ".dashboard.defaultLimits";
        Restrictions serverDefaultLimits = server.getDefaultRestrictions();

        try {
            String undefined = interaction.getLanguageValue("system.undefined");

            List<String> commandRestrictions = Stream.of(
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
            ).flatMap(limit -> {
                long amountUses = limit.amountUses;
                long timestampPeriod = limit.timestampPeriod;
                return Stream.of(
                        (amountUses != 0 ? (String.valueOf(amountUses)) : (undefined)),
                        (timestampPeriod != 0 ? (String.valueOf(timestampPeriod)) : (undefined))
                );
            }).toList();

            String message = String.format(
                    "%s\n\n%s\n%s",
                    interaction.getLanguageValue(defaultLimits + ".title"),
                    interaction.getLanguageValue(defaultLimits + ".description"),
                    interaction.getLanguageValue(defaultLimits + ".restrictions", commandRestrictions)
            );

            output.output(interaction.setMessage(message));
            logger.info(String.format("Displayed default limits dashboard for chat by id(%s)", interaction.getChatId()));

        } catch (Exception err) {
            logger.error(String.format("Failed to display default limits dashboard for chat by id(%s): %s",
                    interaction.getChatId(), err.getMessage()));
            output.output(interaction.setLanguageValue("system.error.something"));
        }
    }

    private void configDashboardModerationCommands(InteractionTelegram interaction) {
        Server server = interaction.findServerById(interaction.getChatId());
        String moderationCommand = ".dashboard.moderationCommands";


        Map<String, Boolean> serverModerationCommand = server.getModerationCommands();
        String enable = interaction.getLanguageValue("system.enable");
        String disable = interaction.getLanguageValue("system.disable");

        try {
            List<String> commandStatus = serverModerationCommand.keySet().stream()
                    .map(commandName -> (serverModerationCommand.get(commandName)) ? (enable) : (disable))
                    .toList();

            String message = String.format(
                    "%s\n\n%s\n%s",
                    interaction.getLanguageValue(moderationCommand + ".title"),
                    interaction.getLanguageValue(moderationCommand + ".description"),
                    interaction.getLanguageValue(moderationCommand + ".commands", commandStatus)
            );

            output.output(interaction.setMessage(message));
            logger.info(String.format("Displayed dashboard moderation commands for chat by id(%s)",
                    interaction.getChatId()));

        } catch (Exception err) {
            logger.error(String.format("Failed to generate moderation commands dashboard message for chat by id(%s): %s",
                    interaction.getChatId(), err.getMessage()));
            output.output(interaction.setLanguageValue("system.error.something"));
        }
    }

    private void user(InteractionTelegram interaction, User user) {
        String commandName = getCommandName();

        if (!user.isExceptedKey(commandName, "userAction")) {
            user.setExcepted(commandName, "userAction");
            output.output(interaction.setLanguageValue("config.user.start"));
            logger.info("User action expected but not provided");
            return;
        }

        String action = ((String) user.getValue(commandName, "userAction")).toLowerCase().trim();

        switch (action) {
            // Настройка стандартных прав доступа
            case "edit limits" -> configUserEditLimits(interaction, user);

            // Настройка стандартных ограничений
            case "edit priority" -> configUserEditPriority(interaction, user);

            // Убрать пользователя из модераторов
            case "remove" -> configUserRemove(interaction, user);

            // Если пользователь указал неправильный аргумент
            default -> {
                user.setExcepted(commandName, "userAction");
                output.output(interaction.setLanguageValue("config.user.start"));
                logger.info(String.format("Invalid user action provided %s", action));
                return;
            }
        }
        user.clearExpected(commandName);
    }

    private void configUserEditLimits(InteractionTelegram interaction, User user) {
        String undefined = interaction.getLanguageValue("system.undefined");
        String commandName = getCommandName();
        String userEditLimits = ".user.editLimits";

        Optional<Long> validUserId = validate.isValidLong(interaction.getArguments().getLast());
        long userId = validUserId.orElse(interaction.getUserId());

        Server server = interaction.findServerById(interaction.getChatId());
        Member targetMember = server.getMembers().get(userId);

        if (targetMember == null) {
            logger.warning(String.format("Target member by id(%s) not found in chat by id(%s)", userId, interaction.getChatId()));
            output.output(interaction.setLanguageValue("system.error.memberNotFound"));
            return;
        }

        Restrictions restrictionsTargetMember = targetMember.getRestrictions();

        try {
            List<String> limits = Stream.of(
                    restrictionsTargetMember.getLimitKick(),
                    restrictionsTargetMember.getLimitBan(),
                    restrictionsTargetMember.getLimitUnban(),
                    restrictionsTargetMember.getLimitMute(),
                    restrictionsTargetMember.getLimitUnMute(),
                    restrictionsTargetMember.getLimitWarn(),
                    restrictionsTargetMember.getLimitRemWarn(),
                    restrictionsTargetMember.getLimitResetWarn(),
                    restrictionsTargetMember.getLimitClear(),
                    restrictionsTargetMember.getLimitGiveTempRole()
            ).flatMap(limit -> Stream.of(
                    ((limit.amountUses != 0) ? (String.valueOf(limit.amountUses)) : (undefined)),
                    (limit.timestampPeriod != 0 ? (String.valueOf(limit.timestampPeriod)) : (undefined))
            )).toList();

            String message = String.format(
                    "%s\n\n%s\n%s\n\n%s",
                    interaction.getLanguageValue(userEditLimits + ".title"),
                    interaction.getLanguageValue(userEditLimits + ".description"),
                    interaction.getLanguageValue(userEditLimits + ".commands", limits),
                    interaction.getLanguageValue(userEditLimits + ".request")
            );

            user.setExcepted(commandName, "userEditLimits");
            output.output(interaction.setMessage(message));
            logger.info(String.format("User by id(%s) is editing limits for member by id(%s)", interaction.getUserId(), userId));

        } catch (Exception err) {
            logger.error(String.format("Failed to build edit limits message for userId by id(%s): %s", userId, err.getMessage()));
            output.output(interaction.setLanguageValue("system.error.something"));
        }
    }

    private void configUserRemove(InteractionTelegram interaction, User user) {
        String commandName = getCommandName();
        long chatId = interaction.getChatId();
        Server server = interaction.findServerById(chatId);

        if (!user.isExceptedKey(commandName, "userId")) {
            user.setExcepted(commandName, "userId", InputExpectation.UserInputType.UNSIGNED_LONG);
            output.output(interaction.setLanguageValue(".user.removeUser.requestUser"));
            logger.info("Awaiting userId input to remove moderation");
            return;
        }

        Long userId = (Long) user.getValue(commandName, "userId");
        Member member = server.getMember(userId);

        if (!server.hasMember(userId) && member.getPriority() == 0) {
            user.setExcepted(commandName, "userId", InputExpectation.UserInputType.LONG);
            output.output(interaction.setLanguageValue(".user.removeUser.requestUser"));
            logger.warning(String.format("User with ID %s is not a moderator or does not exist", userId));
            return;
        }

        if (server.removeModerator(userId)) {
            output.output(interaction.setLanguageValue(".user.removeUser.requestUser"));
            logger.warning(String.format("Failed to remove user by id(%s) from moderators", userId));
            return;
        }

        output.output(interaction.setLanguageValue(".user.removeUser.accepted"));
        logger.info(String.format("User by id(%s) was successfully removed from moderators", userId));
    }

    private void configUserEditPriority(InteractionTelegram interaction, User user) {
        String commandName = getCommandName();
        long chatId = interaction.getChatId();
        Server server = interaction.findServerById(chatId);

        if (!user.isExceptedKey(commandName, "userId")) {
            user.setExcepted(commandName, "userId", InputExpectation.UserInputType.UNSIGNED_LONG);
            output.output(interaction.setLanguageValue(".user.editPriority.requestUser"));
            logger.info(String.format("Expecting userId(%s) for command %s in chat by id(%s)",
                    interaction.getUserId(), commandName, chatId));
            return;
        }

        Long userId = (Long) user.getValue(commandName, "userId");
        Member member = server.getMember(userId);

        if (!server.hasMember(userId) && member.getPriority() == 0) {
            user.setExcepted(commandName, "userId", InputExpectation.UserInputType.UNSIGNED_LONG);
            output.output(interaction.setLanguageValue(".user.editPriority.requestUser"));
            logger.warning(String.format("Invalid or missing member (UserID: %s, ChatID: %s)", userId, chatId));
            return;
        }

        if (!user.isExceptedKey(commandName, "userNewPriority")) {
            user.setExcepted(commandName, "userNewPriority", InputExpectation.UserInputType.UNSIGNED_INTEGER);
            output.output(interaction.setLanguageValue(".user.editPriority.requestPriority"));
            logger.info(String.format("Expecting new priority for user by id(%s) in chat by id(%s)", userId, chatId));
            return;
        }

        int newPriority = (int) user.getValue(commandName, "userNewPriority");
        member.setPriority(newPriority);
        output.output(interaction.setLanguageValue(".user.removeUser.accepted"));
        logger.info(String.format("Priority of user by id(%s) set to %s in chat by id(%s)", userId, newPriority, chatId));
    }

    private void group(InteractionTelegram interaction, User user) {
        final String commandName = getCommandName();

        if (!user.isExceptedKey(commandName, "groupAction")) {
            user.setExcepted(commandName, "groupAction");
            output.output(interaction.setLanguageValue("config.group.start"));
            logger.info(String.format("[%s] Awaiting group action input from user by id(%s) in chat by id(%s)",
                    commandName, user.getUserId(), interaction.getChatId()));
            return;
        }

        Object actionObj = user.getValue(commandName, "userAction");
        if (!(actionObj instanceof String)) {
            user.setExcepted(commandName, "groupAction");
            output.output(interaction.setLanguageValue("config.group.start"));
            logger.warning(String.format("[%s] Invalid or missing group action from user by id(%s) in chat by id(%s)",
                    commandName, user.getUserId(), interaction.getChatId()));
            return;
        }

        String action = ((String) actionObj).toLowerCase().trim();
        logger.info(String.format("[%s] Received group action \"%s\" from user by id(%s) in chat by id(%s)",
                commandName, action, user.getUserId(), interaction.getChatId()));

        switch (action) {

            case "edit name": {
                // Изменить название группы
                logger.debug(String.format("[%s] Executing group name edit", commandName));
                configGroupEditName(interaction, user);
                break;
            }

            case "remove": {
                // Удалить группу
                logger.debug(String.format("[%s] Executing group removal", commandName));
                configGroupRemove(interaction, user);
                break;
            }

            case "edit limits": {
                // Настройка стандартных прав доступа
                logger.debug(String.format("[%s] Executing group limits editing", commandName));
                configGroupEditLimits(interaction, user);
                break;
            }

            case "edit priority": {
                // Настройка стандартных ограничений
                logger.debug(String.format("[%s] Executing group priority editing", commandName));
                configGroupEditPriority(interaction, user);
                break;
            }

            default: { // Если пользователь указал неправильный аргумент
                user.setExcepted(commandName, "groupAction");
                output.output(interaction.setLanguageValue("config.group.start"));
                logger.warning(String.format("[%s] Unknown group action \"%s\" from user by id(%s) in chat by id(%s)",
                        commandName, action, user.getUserId(), interaction.getChatId()));
                return;
            }
        }
        user.clearExpected(commandName);
        logger.info(String.format("[%s] Group command completed for user by id(%s) in chat by id(%s)",
                commandName, user.getUserId(), interaction.getChatId()));
    }

    private void configGroupEditName(InteractionTelegram interaction, User user) {
        final String commandName = getCommandName();
        Server server = interaction.findServerById(interaction.getChatId());

        // Запрашиваем название группы
        if (!user.isExceptedKey(commandName, "oldGroupName")) {
            user.setExcepted(commandName, "oldGroupName");
            output.output(interaction.setLanguageValue(".group.editName.oldGroupName"));
            logger.info(String.format("[%s] Awaiting old group name from user by id(%s) in chat by id(%s)",
                    commandName, user.getUserId(), server.getId()));
            return;
        }

        String oldGroupName = (String) user.getValue(commandName, "oldGroupName");

        // Проверка на наличие группы на сервере
        if (server.hasGroup(oldGroupName)) {
            user.setExcepted(commandName, "groupName");
            output.output(interaction.setLanguageValue(".group.editName.groupNameForRemove"));
            logger.warning(String.format("[%s] Group \"%s\" not found on server by id(%s) (user by id(%s))",
                    commandName, oldGroupName, server.getId(), user.getUserId()));
            return;
        }

        // Запрашиваем новое название группы
        if (!user.isExceptedKey(commandName, "newGroupName")) {
            user.setExcepted(commandName, "newGroupName");
            output.output(interaction.setLanguageValue("config.group.editName.newGroupName"));
            logger.info(String.format("[%s] Awaiting new group name for \"%s\" from user by id(%s) in chat by id(%s)",
                    commandName, oldGroupName, user.getUserId(), server.getId()));
            return;
        }

        String newGroupName = (String) user.getValue(commandName, "newGroupName");
        server.getGroup(oldGroupName).setName(newGroupName);
        output.output(interaction.setLanguageValue(".group.editName.accepted"));

        logger.info(String.format("[%s] Group \"%s\" renamed to \"%s\" on server by id(%s) by user by id(%s)",
                commandName, oldGroupName, newGroupName, server.getId(), user.getUserId()));
    }

    private void configGroupRemove(InteractionTelegram interaction, User user) {
        final String commandName = getCommandName();
        Server server = interaction.findServerById(interaction.getChatId());

        if (!user.isExceptedKey(commandName, "groupName")) {
            user.setExcepted(commandName, "groupName");
            output.output(interaction.setLanguageValue(".group.removeGroup.groupName"));
            logger.info(String.format("[%s] Awaiting group name input from user by id(%s) in chat by id(%s)",
                    commandName, user.getUserId(), server.getId()));
            return;
        }

        String groupName = (String) user.getValue(commandName, "groupName");

        // Проверка на наличие группы на сервере
        if (!server.hasGroup(groupName)) {
            user.setExcepted(commandName, "groupName");
            output.output(interaction.setLanguageValue(".group.removeGroup.groupName"));
            logger.info(String.format("[%s] Group \"%s\" not found on server by id(%s) for user by id(%s)",
                    commandName, groupName, server.getId(), user.getUserId()));
            return;
        }

        // Удаляем и проверяем, получилось ли успешно удалить группу
        if (server.removeGroup(groupName)) {
            output.output(interaction.setLanguageValue(".group.removeGroup.accepted"));
            logger.info(String.format("[%s] Group \"%s\" successfully removed from server by id(%s) by user by id(%s)",
                    commandName, groupName, server.getId(), user.getUserId()));
            return;
        }

        output.output(interaction.setLanguageValue("system.error.something"));
        logger.error(String.format("[%s] Failed to remove group \"%s\" from server by id(%s) (user by id(%s))",
                commandName, groupName, server.getId(), user.getUserId()));
    }

    private void configGroupEditLimits(InteractionTelegram interaction, User user) {
        final String commandName = getCommandName();
        Server server = interaction.findServerById(interaction.getChatId());

        if (!user.isExceptedKey(commandName, "groupName")) {
            user.setExcepted(commandName, "groupName");
            output.output(interaction.setLanguageValue("group.editLimits.groupName"));
            logger.info(String.format("[%s] Awaiting group name input from user %s", commandName, user.getUserId()));
            return;
        }

        String groupName = (String) user.getValue(commandName, "groupName");

        if (server.hasGroup(groupName)) {
            user.setExcepted(commandName, "groupName");
            output.output(interaction.setLanguageValue("group.editLimits.groupName"));
            logger.info(String.format("[%s] Group \"%s\" not found on the server by id(%s)",
                    commandName, groupName, server.getId()));
            return;
        }

        Group group = server.getGroup(groupName);
        Restrictions restrictions = group.getRestrictions();

        if (!user.isExceptedKey(getCommandName(), "groupEditLimits")) {
            String groupLimits = ".group.editLimits";
            try {
                String undefined = interaction.getLanguageValue("system.undefined");

                List<String> limits = Stream.of(
                        restrictions.getLimitKick(),
                        restrictions.getLimitBan(),
                        restrictions.getLimitUnban(),
                        restrictions.getLimitMute(),
                        restrictions.getLimitUnMute(),
                        restrictions.getLimitWarn(),
                        restrictions.getLimitRemWarn(),
                        restrictions.getLimitResetWarn(),
                        restrictions.getLimitClear(),
                        restrictions.getLimitGiveTempRole()
                ).flatMap(limit -> {
                    long amountUses = limit.amountUses;
                    long timestampPeriod = limit.timestampPeriod;
                    return Stream.of(
                            amountUses != 0 ? String.valueOf(amountUses) : undefined,
                            timestampPeriod != 0 ? String.valueOf(timestampPeriod) : undefined
                    );
                }).toList();

                String message = String.format("%s\n\n%s\n%s\n\n%s",
                        interaction.getLanguageValue(groupLimits + ".title"),
                        interaction.getLanguageValue(groupLimits + ".description"),
                        interaction.getLanguageValue(groupLimits + ".restrictions", limits),
                        interaction.getLanguageValue(groupLimits + ".request"));

                user.setExcepted(getCommandName(), "groupEditLimits");
                output.output(interaction.setMessage(message));
                logger.info(String.format("[%s] Send current restrictions info for group %s", commandName, groupName));

            } catch (Exception err) {
                logger.error(String.format("[%s] Error displaying group restrictions for \"%s\": %s",
                        commandName, groupName, err.getMessage()));
                output.output(interaction.setLanguageValue("system.error.something"));
            }
            return;
        }

        List<String> arguments = interaction.getArguments();
        if (arguments.size() % 3 != 0) {
            output.output(interaction.setLanguageValue(".group.editLimits.error.incorrectSize"));
            logger.info(String.format("[%s] Invalid arguments count: %s", commandName, arguments.size()));
            return;
        }

        for (int index = 0; index < arguments.size(); index += 3) {

            Optional<ModerationCommand> moderationCommand = interaction.getCommand(arguments.get(index));
            if (moderationCommand.isEmpty()) {
                output.output(interaction.setLanguageValue(".group.editLimits.error.moderationCommandNotFound"));
                logger.info(String.format("[%s] Moderation command not found: \"%s\"", commandName, arguments.get(index)));
                return;
            }

            // Проверка на валидность количества использований
            Optional<Integer> countUses = validate.isValidInteger(arguments.get(index + 1));
            if (countUses.isEmpty() || countUses.get() < 0) {
                output.output(interaction.setLanguageValue(".group.editLimits.error.incorrectCountUses"));
                logger.info(String.format("[%s] Invalid countUses: %s", commandName, countUses));
                return;
            }

            // Проверка на валидность длительности ограничения
            Optional<LocalDateTime> validDuration = validate.isValidDuration(arguments.get(index + 2));
            if (validDuration.isEmpty() || validDuration.get().isBefore(LocalDateTime.now())) {
                output.output(interaction.setLanguageValue(".group.editLimits.error.incorrectDuration"));
                logger.info(String.format("[%s] Invalid duration: %s", commandName, arguments.get(index + 2)));
                return;
            }

            long timestampPeriod = validDuration.get().atZone(ZoneId.systemDefault()).toEpochSecond();
            Limit limit = new Limit(countUses.get(), timestampPeriod);
            group.setRestrictions(new Restrictions().setLimit(moderationCommand.get(), limit));
            logger.info(String.format("[%s] Set limit for \"%s\": %s uses, %s timestamp",
                    commandName, moderationCommand.get(), countUses.get(), timestampPeriod));
        }

        logger.info(String.format("[%s] Finished setting limits for group \"%s\"", commandName, groupName));
    }

    private void configGroupEditPriority(InteractionTelegram interaction, User user) {
        Server server = interaction.findServerById(interaction.getChatId());
        String command = getCommandName();
        long userId = interaction.getUserId();
        long chatId = interaction.getChatId();

        // Ожидаем имя группы
        if (!user.isExceptedKey(command, "groupName")) {
            user.setExcepted(command, "groupName");
            output.output(interaction.setLanguageValue(".group.editPriority.requestUser"));
            logger.info(String.format("User %s initiated group priority editing. Awaiting group name.", userId));
            return;
        }

        String groupName = (String) user.getValue(command, "groupName");

        // Проверяем наличие группы
        if (!server.hasGroup(groupName)) {
            user.setExcepted(command, "groupName");
            output.output(interaction.setLanguageValue(".group.editPriority.requestUser"));
            logger.warning(String.format("User %s entered non-existing group name '%s' in chat %s.",
                    userId, groupName, chatId));
            return;
        }

        // Ожидаем новый приоритет
        if (!user.isExceptedKey(command, "groupNewPriority")) {
            user.setExcepted(command, "groupNewPriority", InputExpectation.UserInputType.UNSIGNED_INTEGER);
            output.output(interaction.setLanguageValue(".group.editPriority.requestPriority"));
            logger.info(String.format("User %s selected group \"%s\". Awaiting new priority input.", userId, groupName));
            return;
        }

        // Применяем изменения
        Group group = server.getGroup(groupName);
        int newPriority = (int) user.getValue(command, "groupNewPriority");
        group.setPriority(newPriority);

        output.output(interaction.setLanguageValue(".group.editPriority.accepted"));
        logger.info(String.format("User %s updated priority of group '%s' to %d in chat %s.",
                userId, groupName, newPriority, chatId));
    }
}
