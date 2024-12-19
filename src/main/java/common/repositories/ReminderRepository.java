package common.repositories;

import common.models.Reminder;
import common.utils.LoggerHandler;

import java.time.LocalDate;
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
    public Reminder create(Reminder reminder) {
        if (reminders == null) {
            reminders = new TreeMap<>();
        }
        
        long reminderId = reminder.getId();
        LocalDate sendAt = reminder.getSendAt();

        if (reminders.containsKey(reminderId)) {
            reminders.get(reminderId).add(reminder);
            return reminder;
        }
        reminders.put(reminderId, List.of(reminder));
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