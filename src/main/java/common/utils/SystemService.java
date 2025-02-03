package common.utils;

import com.pengrad.telegrambot.model.ChatPermissions;
import com.pengrad.telegrambot.request.RestrictChatMember;
import com.pengrad.telegrambot.request.UnbanChatMember;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.Reminder;
import common.models.Server;
import common.models.User;
import common.repositories.ReminderRepository;
import common.repositories.ServerRepository;
import common.repositories.UserRepository;
import common.repositories.WarningRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class SystemService {
    OutputHandler output = new OutputHandler();
    LoggerHandler logger = new LoggerHandler();
    UserRepository userRepository;
    ServerRepository serverRepository;
    ReminderRepository reminderRepository;
    WarningRepository warningRepository;

    public SystemService(Interaction interaction) {
        userRepository = interaction.getUserRepository();
        serverRepository = interaction.getServerRepository();
        reminderRepository = interaction.getReminderRepository();
        warningRepository = interaction.getWarningRepository();

        long timestamp = System.currentTimeMillis() / 1000;
        // Поток для системы напоминаний
        Thread threadReminder = new Thread(() ->
                reminderHandler(interaction)
        );
        threadReminder.setName("Thread-Reminder");
        threadReminder.start();
        logger.info("SYSTEM: ReminderHandler is launch", true);

        // Поток для системы банов
        Thread threadBan = new Thread(() ->
                banHandler((InteractionTelegram) interaction)
        );
        threadBan.setName("Thread-Ban");
        threadBan.start();
        logger.info("SYSTEM: BanHandler is launch", true);

        // Поток для системы мьютов
        Thread threadMute = new Thread(() ->
                muteHandler((InteractionTelegram) interaction)
        );
        threadMute.setName("Thread-Mute");
        threadMute.start();
        logger.info("SYSTEM: MuteHandler is launch", true);
    }

    private void reminderHandler(Interaction interaction) {
        StringBuilder message;
        long timestamp = System.currentTimeMillis() / 1000;
        List<Reminder> reminders;

        while (true) {
            try {
                if (reminderRepository.existsByTimestamp(timestamp)) {
                    reminders = reminderRepository.findByTimestamp(timestamp);

                    // Проходимся по всем напоминаниям
                    for (Reminder reminder : reminders) {
                        message = new StringBuilder();
                        message.append(interaction.getLanguageValue("reminder.send.reminder")).append("\n");
                        message.append(interaction.getLanguageValue("reminder.send.content", List.of(
                                reminder.getContent()
                        ))).append("\n");
                        message.append(interaction.getLanguageValue("reminder.send.createdAt", List.of(
                                reminder.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                        ))).append("\n");

                        // Если имеется дата изменения сообщения
                        if (reminder.getEditAt() != null) {
                            message.append(interaction.getLanguageValue("reminder.send.editAt", List.of(
                                    reminder.getEditAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                            ))).append("\n");
                        }

                        // Проверка на платформу
                        if (reminder.getPlatform() == Interaction.Platform.TELEGRAM) {
                            output.output(((InteractionTelegram) interaction).setChatId(reminder.getChatId())
                                    .setMessage(message.toString()));
                        } else {
                            output.output(interaction.setMessage(message.toString()));
                        }

                        userRepository.findById(interaction.getChatId(), interaction.getUserId())
                                .removeReminder(reminder);
                        reminderRepository.remove(timestamp/*, указать reminderID*/);
                    }
                }
                Thread.sleep(60000);
                timestamp = System.currentTimeMillis() / 1000;

            } catch (Exception err) {
                logger.fatal("Reminder handler (Send reminder): " + err);
            }
        }
    }

    public void banHandler(InteractionTelegram interaction) {
        long timestamp;
        List<Server> servers = serverRepository.getAll();

        while (true) {
            try {
                timestamp = System.currentTimeMillis() / 1000;
                for (Server server : servers) {
                    if (server.getBans().isEmpty()) {
                        continue;
                    }

                    Map<Long, List<User>> bans = server.getBans();
                    for (long unbanTimestamp : bans.keySet()) {
                        if (unbanTimestamp > timestamp) {
                            continue;
                        }

                        for (User user : bans.get(unbanTimestamp)) {
                            interaction.telegramBot
                                    .execute(new UnbanChatMember(server.getId(), user.getUserId()));
                        }
                    }
                }

                Thread.sleep(60000);

            } catch (Exception err) {
                logger.fatal("Ban handler (Unban user): " + err);
            }
        }
    }

    public void muteHandler(InteractionTelegram interaction) {
        long timestamp;
        List<Server> servers = serverRepository.getAll();

        while (true) {
            try {
                timestamp = System.currentTimeMillis() / 1000;
                for (Server server : servers) {
                    if (server.getBans().isEmpty()) {
                        continue;
                    }

                    Map<Long, List<User>> bans = server.getBans();
                    for (long unbanTimestamp : bans.keySet()) {
                        if (unbanTimestamp > timestamp || unbanTimestamp == 0) {
                            continue;
                        }

                        for (User user : bans.get(unbanTimestamp)) {
                            interaction.telegramBot
                                    .execute(new RestrictChatMember(server.getId(), user.getUserId(),
                                            new ChatPermissions().canSendMessages(true)));
                        }
                    }
                }

                Thread.sleep(60000);

            } catch (Exception err) {
                logger.fatal("Ban handler (Unban user): " + err);
            }
        }
    }
}
