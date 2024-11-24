package common.utils;

import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.Reminder;
import common.repositories.ReminderRepository;

import java.util.List;

public class ReminderHandler {
    OutputHandler output = new OutputHandler();
    LoggerHandler logger = new LoggerHandler();
    ReminderRepository reminderRepository = new ReminderRepository();

    public void run(Interaction interaction) {
        long timestamp = System.currentTimeMillis() / 1000;
        List<Reminder> reminders;

        while (true) {
            // Проверка на наличие напоминаний на текущее время
            if (reminderRepository.existsByTimestamp(timestamp)) {
                reminders = reminderRepository.findByTimestamp(timestamp);

                // Проходимся по всем напоминаниям на текущее время
                for (Reminder reminder : reminders) {
                    // Проверка на платформу
                    if (reminder.getPlatform() == Interaction.Platform.TELEGRAM) {
                        interaction = ((InteractionTelegram) interaction).setUserId(reminder.getUserId());
                    }

                    logger.info("The reminder was sent to the user id(" + reminder.getUserId() + ")");
                    output.output(interaction.setMessage(reminder.getContent()));
                }
                logger.info("Reminder(s) for timestamp(" + timestamp + ") has been deleted(s)");
                reminderRepository.remove(timestamp);
            }

            timestamp = System.currentTimeMillis() / 1000;
        }
    }
}
