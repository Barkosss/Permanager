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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

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

        // Поток для системы напоминаний
        Thread threadReminder = new Thread(() -> {
            logger.info("SYSTEM: ReminderHandler is launch", true);
            reminderHandler(interaction);
        });
        threadReminder.setName("Thread-Reminder");
        threadReminder.start();

        // Поток для системы банов
        Thread threadBan = new Thread(() -> {
            logger.info("SYSTEM: BanHandler is launch", true);
            banHandler((InteractionTelegram) interaction);
        });
        threadBan.setName("Thread-Ban");
        threadBan.start();

        // Поток для системы мьютов
        Thread threadMute = new Thread(() -> {
            logger.info("SYSTEM: MuteHandler is launch", true);
            muteHandler((InteractionTelegram) interaction);
        });
        threadMute.setName("Thread-Mute");
        threadMute.start();
    }

    private void reminderHandler(Interaction interaction) {
        AtomicReference<StringBuilder> message = new AtomicReference<>();
        long timestamp = System.currentTimeMillis() / 1000;
        AtomicReference<List<Reminder>> reminders = new AtomicReference<>();
        try (ScheduledExecutorService schedulerReminder = Executors.newSingleThreadScheduledExecutor()) {
            logger.info("Reminder handler started.", true);

            try {

                schedulerReminder.scheduleAtFixedRate(() -> {
                    try {
                        if (reminderRepository.existsByTimestamp(timestamp)) {
                            reminders.set(reminderRepository.findByTimestamp(timestamp));

                            // Проходимся по всем напоминаниям
                            for (Reminder reminder : reminders.get()) {
                                message.set(new StringBuilder());
                                message.get().append(interaction.getLanguageValue("reminder.send.reminder")).append("\n");
                                message.get().append(interaction.getLanguageValue("reminder.send.content", List.of(
                                        reminder.getContent()
                                ))).append("\n");
                                message.get().append(interaction.getLanguageValue("reminder.send.createdAt", List.of(
                                        reminder.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                                ))).append("\n");

                                // Если имеется дата изменения сообщения
                                if (reminder.getEditAt() != null) {
                                    message.get().append(interaction.getLanguageValue("reminder.send.editAt", List.of(
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

                    } catch (Exception err) {
                        logger.fatal("Reminder handler (Send reminder): " + err, true);
                    }
                }, 0, 1, TimeUnit.MINUTES);
            } catch (Exception err) {
                logger.fatal("Reminder handler: " + err, true);
            } finally {
                schedulerReminder.shutdown();
            }
        }
    }

    public void banHandler(InteractionTelegram interaction) {
        AtomicLong timestamp = new AtomicLong();
        List<Server> servers = serverRepository.getAll();
        try (ScheduledExecutorService schedulerBan = Executors.newSingleThreadScheduledExecutor()) {
            logger.info("Ban handler started.", true);

            try {
                schedulerBan.scheduleAtFixedRate(() -> {
                    try {
                        timestamp.set(System.currentTimeMillis() / 1000);
                        for (Server server : servers) {
                            if (server.getBans().isEmpty()) {
                                continue;
                            }

                            Map<Long, List<User>> bans = server.getBans();
                            for (long unbanTimestamp : bans.keySet()) {
                                if (unbanTimestamp > timestamp.get()) {
                                    continue;
                                }

                                for (User user : bans.get(unbanTimestamp)) {
                                    interaction.execute(new UnbanChatMember(server.getId(), user.getUserId()));
                                    logger.info(String.format("User by id(%s) unbanned from chat by id(%s)", user.getUserId(), server.getId()), true);
                                }
                            }
                        }

                    } catch (Exception err) {
                        logger.fatal("Ban handler (Unban user): " + err, true);
                    }
                }, 0, 1, TimeUnit.MINUTES);
            } catch (Exception err) {
                logger.fatal("Ban handler: " + err, true);
            } finally {
                schedulerBan.shutdown();
            }
        }
    }

    public void muteHandler(InteractionTelegram interaction) {
        AtomicLong timestamp = new AtomicLong();
        List<Server> servers = serverRepository.getAll();
        try (ScheduledExecutorService schedulerMute = Executors.newSingleThreadScheduledExecutor()) {
            logger.info("Mute handler started.", true);

            try {
                schedulerMute.scheduleAtFixedRate(() -> {
                    try {
                        timestamp.set(System.currentTimeMillis() / 1000);
                        for (Server server : servers) {
                            if (server.getBans().isEmpty()) {
                                continue;
                            }

                            Map<Long, List<User>> bans = server.getBans();
                            for (long unbanTimestamp : bans.keySet()) {
                                if (unbanTimestamp > timestamp.get() || unbanTimestamp == 0) {
                                    continue;
                                }

                                for (User user : bans.get(unbanTimestamp)) {
                                    interaction.execute(new RestrictChatMember(server.getId(), user.getUserId(),
                                            new ChatPermissions().canSendMessages(true)));
                                    logger.info(String.format("User by id(%s) unmuted in chat by id(%s)", user.getUserId(), server.getId()), true);
                                }
                            }
                        }

                    } catch (Exception err) {
                        logger.fatal("Ban handler (Unban user): " + err, true);
                    }
                }, 0, 1, TimeUnit.MINUTES);
            } catch (Exception err) {
                logger.fatal("Ban handler: " + err, true);
            } finally {
                schedulerMute.shutdown();
            }
        }
    }
}
