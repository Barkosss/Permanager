package common.utils;

import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.Reminder;
import common.repositories.ReminderRepository;

import java.util.List;

public class SystemService {
    OutputHandler output = new OutputHandler();
    LoggerHandler logger = new LoggerHandler();
    ReminderRepository reminderRepository = new ReminderRepository();

    public SystemService(Interaction interaction) {
        // Поток для системы напоминаний
        Thread threadReminder = new Thread(() ->
                reminderHandler(interaction)
        );
        threadReminder.setName("Thread-Reminder");
        threadReminder.start();
        logger.info("SYSTEM: ReminderHandler is launch", true);

        // Поток для системы банов
        Thread threadBan = new Thread(() ->
                banHandler(interaction)
        );
        threadBan.setName("Thread-Ban");
        threadBan.start();
        logger.info("SYSTEM: BanHandler is launch", true);

        // Поток для системы мьютов
        Thread threadMute = new Thread(() ->
                muteHandler(interaction)
        );
        threadMute.setName("Thread-Mute");
        threadMute.start();
        logger.info("SYSTEM: MuteHandler is launch", true);
    }

    private void checkOldReminder(Interaction interaction) {

    }

    public void reminderHandler(Interaction interaction) {
        long timestamp = System.currentTimeMillis() / 1000;
        List<Reminder> reminders;

        checkOldReminder(interaction);

        while (true) {
            try {
                // Проверка на наличие напоминаний на текущее время
                if (reminderRepository.existsByTimestamp(timestamp)) {
                    reminders =  reminderRepository.findByTimestamp(timestamp);

                    // Берём на секунду будущие напоминания
                    if (reminderRepository.existsByTimestamp(timestamp + 1)) {
                        reminders.addAll(reminderRepository.findByTimestamp(timestamp + 1));
                    }

                    // Берём на две секунды будущие напоминания
                    if (reminderRepository.existsByTimestamp(timestamp + 2)) {
                        reminders.addAll(reminderRepository.findByTimestamp(timestamp + 2));
                    }

                    // Проходимся по всем напоминаниям на текущее время
                    for (Reminder reminder : reminders) {
                        // Проверка на платформу
                        if (reminder.getPlatform() == Interaction.Platform.TELEGRAM) {
                            interaction = ((InteractionTelegram) interaction).setChatId(reminder.getChatId());
                        }

                        logger.info(String.format("The reminder was sent to the user id(%s, chatId=%s",
                                reminder.getUserId(), reminder.getChatId()));
                        output.output(interaction.setMessage(reminder.getContent()));
                    }
                    logger.info(String.format("Reminder(s) for timestamp(%s) has been deleted(s)", timestamp));
                    reminderRepository.remove(timestamp);
                }

                timestamp = System.currentTimeMillis() / 1000;
            } catch (Exception err) {
                logger.error(String.format("ReminderHandler: %s", err));
            }
        }
    }

    public void banHandler(Interaction interaction) {

    }

    public void muteHandler(Interaction interaction) {

    }
}
