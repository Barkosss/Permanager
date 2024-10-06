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
    public Limit canTempRole;


    // ...
    public Limit getCanKick() {
        return canKick;
    }

    // ...
    public void setCanKick(Limit canKick) {
        this.canKick = canKick;
    }

    // ...
    public Limit getCanBan() {
        return canBan;
    }

    // ...
    public void setCanBan(Limit canBan) {
        this.canBan = canBan;
    }

    // ...
    public Limit getCanUnban() {
        return canUnban;
    }

    // ...
    public void setCanUnban(Limit canUnban) {
        this.canUnban = canUnban;
    }

    // ...
    public Limit getCanMute() {
        return canMute;
    }

    // ...
    public void setCanMute(Limit canMute) {
        this.canMute = canMute;
    }

    // ...
    public Limit getCanUnMute() {
        return canUnMute;
    }

    // ...
    public void setCanUnMute(Limit canUnMute) {
        this.canUnMute = canUnMute;
    }

    // ...
    public Limit getCanWarn() {
        return canWarn;
    }

    // ...
    public void setCanWarn(Limit canWarn) {
        this.canWarn = canWarn;
    }

    // ...
    public Limit getCanRemWarn() {
        return canRemWarn;
    }

    // ...
    public void setCanRemWarn(Limit canRemWarn) {
        this.canRemWarn = canRemWarn;
    }

    // ...
    public Limit getCanResetWarn() {
        return canResetWarn;
    }

    // ...
    public void setCanResetWarn(Limit canResetWarn) {
        this.canResetWarn = canResetWarn;
    }

    // ...
    public Limit getCanClear() {
        return canClear;
    }

    // ...
    public void setCanClear(Limit canClear) {
        this.canClear = canClear;
    }

    // ...
    public Limit getCanTempRole() {
        return canTempRole;
    }

    // ...
    public void setCanTempRole(Limit canTempRole) {
        this.canTempRole = canTempRole;
    }
}