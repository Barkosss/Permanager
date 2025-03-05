package common.models;

public class Limit {

    // Количество использований
    public long amountUses;

    // Кулдан (В секундах)
    public long timestampPeriod;

    // Пустой конструктор
    public Limit() {
        this.amountUses = 0;
        this.timestampPeriod = 0;
    }

    // Конструктор лимита
    public Limit(long amountUses, long timestampPeriod) {
        this.amountUses = amountUses;
        this.timestampPeriod = timestampPeriod;
    }

    // Получить количество использований
    public long getAmountUses() {
        return amountUses;
    }

    // Назначить количество использований
    public void setAmountUses(long amountUses) {
        this.amountUses = amountUses;
    }

    // Получить кулдаун
    public long getTimestampPeriod() {
        return timestampPeriod;
    }

    // Назначить кулдаун
    public void setTimestampPeriod(long timestampPeriod) {
        this.timestampPeriod = timestampPeriod;
    }

    // For Debug
    public String getString() {
        return String.format("Limit={amountUses=%d, timestampPeriod=%d}", amountUses, timestampPeriod);
    }
}
