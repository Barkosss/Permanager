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
    }

    // Создать напоминание в памяти
    public Reminder create(Reminder reminder) {
        long timestamp = Timestamp.valueOf(reminder.getCreatedAt()).getTime() / 1000;
        if (reminders == null) {
            reminders = new TreeMap<>();
        }

        if (!reminders.containsKey(timestamp)) {
            reminders.put(timestamp, new ArrayList<>());
        }

        reminders.get(timestamp).add(reminder);
        return reminder;
    }

    // Удалить напоминания
    public void remove(Long timestamp) {
        reminders.remove(timestamp);
    }

    // Найти напоминание по ID у пользователя
    public List<Reminder> findByTimestamp(long timestamp) {
        List<Reminder> reminder;
        if ((reminder = reminders.get(timestamp)) != null) {
            return reminder;
        }
        logger.error("Reminder by timestamp(" + timestamp + ") is not found");
        return null;
    }

    // Существует ли напоминание у пользователя
    public boolean existsByTimestamp(long reminderId) {
        return reminders.get(reminderId) != null;
    }
}