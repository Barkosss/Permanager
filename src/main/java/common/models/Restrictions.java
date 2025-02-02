package common.models;

public class Restrictions {

    // Ограничение на использование команды кик
    private Limit limitKick;

    // Ограничение на использование команды бан
    private Limit limitBan;

    // Ограничение на использование команды для разбана
    private Limit limitUnban;

    // Ограничение на использование команды мут
    private Limit limitMute;

    // Ограничение на использование команды снятия мута
    private Limit limitUnMute;

    // Ограничение на использование команды выдачу предупреждения
    private Limit limitWarn;

    // Ограничение на использование команды снятия предупреждения
    private Limit limitRemWarn;

    // Ограничение на использование команды сброса предупреждений
    private Limit limitResetWarn;

    // Ограничение на использование команды очищать сообщение(-я) в чате
    private Limit limitClear;

    // Ограничение на использование команды выдавать временную роль
    private Limit limitGiveTempRole;

    // Пустой конструктор
    public Restrictions() {
        this.limitKick = new Limit();
        this.limitBan = new Limit();
        this.limitUnban = new Limit();
        this.limitMute = new Limit();
        this.limitUnMute = new Limit();
        this.limitWarn = new Limit();
        this.limitRemWarn = new Limit();
        this.limitResetWarn = new Limit();
        this.limitClear = new Limit();
        this.limitGiveTempRole = new Limit();
    }

    // Получить ограничения на kick
    public Limit getLimitKick() {
        return limitKick;
    }

    // Назначить ограничения на kick
    public void setLimitKick(Limit limitKick) {
        this.limitKick = limitKick;
    }

    // Получить ограничения на ban
    public Limit getLimitBan() {
        return limitBan;
    }

    // Назначить ограничения на ban
    public void setLimitBan(Limit limitBan) {
        this.limitBan = limitBan;
    }

    // Получить ограничения на unban
    public Limit getLimitUnban() {
        return limitUnban;
    }

    // Назначить ограничения на unban
    public void setLimitUnban(Limit limitUnban) {
        this.limitUnban = limitUnban;
    }

    // Получить ограничения на mute
    public Limit getLimitMute() {
        return limitMute;
    }

    // Назначить ограничения на mute
    public void setLimitMute(Limit limitMute) {
        this.limitMute = limitMute;
    }

    // Получить ограничения на unmute
    public Limit getLimitUnMute() {
        return limitUnMute;
    }

    // Назначить ограничения на unmute
    public void setLimitUnMute(Limit limitUnMute) {
        this.limitUnMute = limitUnMute;
    }

    // Получить ограничения на warn
    public Limit getLimitWarn() {
        return limitWarn;
    }

    // Назначить ограничения на warn
    public void setLimitWarn(Limit limitWarn) {
        this.limitWarn = limitWarn;
    }

    // Получить ограничения на remwarn
    public Limit getLimitRemWarn() {
        return limitRemWarn;
    }

    // Назначить ограничения на remwarn
    public void setLimitRemWarn(Limit limitRemWarn) {
        this.limitRemWarn = limitRemWarn;
    }

    // Получить ограничения на resetwarn
    public Limit getLimitResetWarn() {
        return limitResetWarn;
    }

    // Назначить ограничения на resetwarn
    public void setLimitResetWarn(Limit limitResetWarn) {
        this.limitResetWarn = limitResetWarn;
    }

    // Получить ограничения на clear
    public Limit getLimitClear() {
        return limitClear;
    }

    // Назначить ограничения на clear
    public void setLimitClear(Limit limitClear) {
        this.limitClear = limitClear;
    }

    // Получить ограничения на temprole
    public Limit getLimitGiveTempRole() {
        return limitGiveTempRole;
    }

    // Назначить ограничения на temprole
    public void setLimitGiveTempRole(Limit limitGiveTempRole) {
        this.limitGiveTempRole = limitGiveTempRole;
    }
}