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

    public void reminderHandler(Interaction interaction) {
        long timestamp = System.currentTimeMillis() / 1000;
        List<Reminder> reminders;

        while (true) {
            try {
                // Проверка на наличие напоминаний на текущее время
                if (reminderRepository.existsByTimestamp(timestamp)) {
                    reminders = reminderRepository.findByTimestamp(timestamp);

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