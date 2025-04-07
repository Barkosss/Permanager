package common.repositories;

import common.models.Reminder;
import common.utils.LoggerHandler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReminderRepository {
    private final LoggerHandler logger = new LoggerHandler();
    private Map<Long, List<Reminder>> reminders;

    public ReminderRepository() {
        this.reminders = new TreeMap<>();
        logger.info("ReminderRepository initialized.", true);
    }

    // Создать напоминание в памяти
    public Reminder create(Reminder reminder) {
        long timestamp = Timestamp.valueOf(reminder.getCreatedAt()).getTime() / 1000;
        if (reminders == null) {
            reminders = new TreeMap<>();
            logger.info("Reminders map was null, reinitialized as TreeMap.", true);
        }

        if (!reminders.containsKey(timestamp)) {
            reminders.put(timestamp, new ArrayList<>());
            logger.debug("Created new reminder list for timestamp: " + timestamp);
        }

        reminders.get(timestamp).add(reminder);
        logger.debug("Added reminder to the list for timestamp: " + timestamp);
        return reminder;
    }

    // Удалить напоминания
    public void remove(Long timestamp) {
        if (reminders.containsKey(timestamp)) {
            reminders.remove(timestamp);
            logger.info("Removed reminders for timestamp: " + timestamp);
        } else {
            logger.warning("No reminders found for timestamp: " + timestamp);
        }
    }

    // Найти напоминание по ID у пользователя
    public List<Reminder> findByTimestamp(long timestamp) {
        List<Reminder> reminder;
        if ((reminder = reminders.get(timestamp)) != null) {
            logger.debug("Found reminders for timestamp: " + timestamp);
            return reminder;
        }
        logger.error("Reminder by timestamp(" + timestamp + ") is not found");
        return null;
    }

    // Существует ли напоминание у пользователя
    public boolean existsByTimestamp(long reminderId) {
        boolean exists = reminders.containsKey(reminderId);
        logger.debug("Reminder with timestamp(" + reminderId + ") exists: " + exists);
        return exists;
    }
}