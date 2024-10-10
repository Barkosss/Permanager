package common.models;

public class Restrictions {

    // Ограничение на использование команды кик
    public Limit canKick;

    // Ограничение на использование команды бан
    public Limit canBan;

    // Ограничение на использование команды для разбана
    public Limit canUnban;

    // Ограничение на использование команды мут
    public Limit canMute;

    // Ограничение на использование команды снятия мута
    public Limit canUnMute;

    // Ограничение на использование команды выдачу предупреждения
    public Limit canWarn;

    // Ограничение на использование команды снятия предупреждения
    public Limit canRemWarn;

    // Ограничение на использование команды сброса предупреждений
    public Limit canResetWarn;

    // Ограничение на использование команды очищать сообщение(-я) в чате
    public Limit canClear;

    // Ограничение на использование команды выдавать временную роль
    public Limit canGiveTempRole;


    // Получить ограничения на kick
    public Limit getCanKick() {
        return canKick;
    }

    // Назначить ограничения на kick
    public void setCanKick(Limit canKick) {
        this.canKick = canKick;
    }

    // Получить ограничения на ban
    public Limit getCanBan() {
        return canBan;
    }

    // Назначить ограничения на ban
    public void setCanBan(Limit canBan) {
        this.canBan = canBan;
    }

    // Получить ограничения на unban
    public Limit getCanUnban() {
        return canUnban;
    }

    // Назначить ограничения на unban
    public void setCanUnban(Limit canUnban) {
        this.canUnban = canUnban;
    }

    // Получить ограничения на mute
    public Limit getCanMute() {
        return canMute;
    }

    // Назначить ограничения на mute
    public void setCanMute(Limit canMute) {
        this.canMute = canMute;
    }

    // Получить ограничения на unmute
    public Limit getCanUnMute() {
        return canUnMute;
    }

    // Назначить ограничения на unmute
    public void setCanUnMute(Limit canUnMute) {
        this.canUnMute = canUnMute;
    }

    // Получить ограничения на warn
    public Limit getCanWarn() {
        return canWarn;
    }

    // Назначить ограничения на warn
    public void setCanWarn(Limit canWarn) {
        this.canWarn = canWarn;
    }

    // Получить ограничения на remwarn
    public Limit getCanRemWarn() {
        return canRemWarn;
    }

    // Назначить ограничения на remwarn
    public void setCanRemWarn(Limit canRemWarn) {
        this.canRemWarn = canRemWarn;
    }

    // Получить ограничения на resetwarn
    public Limit getCanResetWarn() {
        return canResetWarn;
    }

    // Назначить ограничения на resetwarn
    public void setCanResetWarn(Limit canResetWarn) {
        this.canResetWarn = canResetWarn;
    }

    // Получить ограничения на clear
    public Limit getCanClear() {
        return canClear;
    }

    // Назначить ограничения на clear
    public void setCanClear(Limit canClear) {
        this.canClear = canClear;
    }

    // Получить ограничения на temprole
    public Limit getCanGiveTempRole() {
        return canGiveTempRole;
    }

    // Назначить ограничения на temprole
    public void setCanGiveTempRole(Limit canGiveTempRole) {
        this.canGiveTempRole = canGiveTempRole;
    }
}