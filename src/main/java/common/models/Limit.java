package common.models;

public class Limit {

    // Количество использований
    public int amountUses;

    // Кулдан (В секундах)
    public int timestampPeriod;

    // Пустой конструктор
    public Limit() {
        this.amountUses = 0;
        this.timestampPeriod = 0;
    }

    // Конструктор лимита
    public Limit(int amountUses, int timestampPeriod) {
        this.amountUses = amountUses;
        this.timestampPeriod = timestampPeriod;
    }

    // Получить количество использований
    public int getAmountUses() {
        return amountUses;
    }

    // Назначить количество использований
    public void setAmountUses(int amountUses) {
        this.amountUses = amountUses;
    }

    // Получить кулдаун
    public int getTimestampPeriod() {
        return timestampPeriod;
    }

    // Назначить кулдаун
    public void setTimestampPeriod(int timestampPeriod) {
        this.timestampPeriod = timestampPeriod;
    }
}
