package common.repositories;

import common.models.Reminder;
import common.utils.LoggerHandler;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReminderRepository {
    LoggerHandler logger = new LoggerHandler();
    Map<Long, List<Reminder>> reminders;

    public ReminderRepository() {
        this.reminders = new TreeMap<>();
    }

    // Создать напоминание в памяти
    public void create(Reminder reminder) {
        long reminderId = reminder.getId();
        if (reminders.containsKey(reminderId)) {
            reminders.get(reminderId).add(reminder);
            return;
        }
        reminders.put(reminderId, List.of(reminder));
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