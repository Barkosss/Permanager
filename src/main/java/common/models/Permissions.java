package common.models;

public class Permissions {

    // Имеет ли возможность кикать
    public boolean canKick;

    // Имеет ли возможность выдавать бан
    public boolean canBan;

    // Имеет ли возможность снимать бан
    public boolean canUnban;

    // Имеет ли возможность выдавать мут
    public boolean canMute;

    // Имеет ли возможность снимать мут
    public boolean canUnMute;

    // Имеет ли возможность выдавать предупреждение
    public boolean canWarn;

    // Имеет ли возможность снимать предупреждение
    public boolean canRemWarn;

    // Имеет ли возможность обнулять предупреждения
    public boolean canResetWarn;

    // Имеет ли возможность очищать сообщение(-я) в чате
    public boolean canClear;

    // Имеет ли возможность выдавать временную роль
    public boolean canGiveTempRole;

    public Permissions() {
        this.canKick = false;
        this.canBan = false;
        this.canUnban = false;
        this.canMute = false;
        this.canUnMute = false;
        this.canWarn = false;
        this.canRemWarn = false;
        this.canResetWarn = false;
        this.canClear = false;
        this.canGiveTempRole = false;
    }

    public Permissions(boolean canKick, boolean canBan, boolean canUnban, boolean canMute, boolean canUnMute, boolean canWarn, boolean canRemWarn, boolean canResetWarn, boolean canClear, boolean canGiveTempRole) {
        this.canKick = canKick;
        this.canBan = canBan;
        this.canUnban = canUnban;
        this.canMute = canMute;
        this.canUnMute = canUnMute;
        this.canWarn = canWarn;
        this.canRemWarn = canRemWarn;
        this.canResetWarn = canResetWarn;
        this.canClear = canClear;
        this.canGiveTempRole = canGiveTempRole;
    }

    // Получить, имеет ли пользователь доступ к kick
    public boolean getCanKick() {
        return canKick;
    }

    // Установить значение, имеет ли пользователь доступ к kick
    public void setCanKick(boolean canKick) {
        this.canKick = canKick;
    }

    // Получить, имеет ли пользователь доступ к ban
    public boolean getCanBan() {
        return canBan;
    }

    // Установить значение, имеет ли пользователь доступ к ban
    public void setCanBan(boolean canBan) {
        this.canBan = canBan;
    }

    // Получить, имеет ли пользователь доступ к unban
    public boolean getCanUnban() {
        return canUnban;
    }

    // Установить значение, имеет ли пользователь доступ к unban
    public void setCanUnban(boolean canUnban) {
        this.canUnban = canUnban;
    }

    // Получить, имеет ли пользователь доступ к mute
    public boolean getCanMute() {
        return canKick;
    }

    // Установить значение, имеет ли пользователь доступ к mute
    public void setCanMute(boolean CanMute) {
        this.canMute = CanMute;
    }

    // Получить, имеет ли пользователь доступ к unmute
    public boolean getCanUnMute() {
        return canUnMute;
    }

    // Установить значение, имеет ли пользователь доступ к unmute
    public void setCanUnMute(boolean canUnMute) {
        this.canUnMute = canUnMute;
    }

    // Получить, имеет ли пользователь доступ к warn
    public boolean getCanWarn() {
        return canWarn;
    }

    // Установить значение, имеет ли пользователь доступ к warn
    public void setCanWarn(boolean canWarn) {
        this.canWarn = canWarn;
    }

    // Получить, имеет ли пользователь доступ к remwarn
    public boolean getCanRemWarn() {
        return canRemWarn;
    }

    // Установить значение, имеет ли пользователь доступ к remwarn
    public void setCanRemWarn(boolean canRemWarn) {
        this.canRemWarn = canRemWarn;
    }

    // Получить, имеет ли пользователь доступ к resetwarn
    public boolean getCanResetWarn() {
        return canResetWarn;
    }

    // Установить значение, имеет ли пользователь доступ к resetwarn
    public void setCanResetWarn(boolean canResetWarn) {
        this.canResetWarn = canResetWarn;
    }

    // Получить, имеет ли пользователь доступ к clear
    public boolean getCanClear() {
        return canClear;
    }

    // Установить значение, имеет ли пользователь доступ к clear
    public void setCanClear(boolean canClear) {
        this.canClear = canClear;
    }

    // Получить, имеет ли пользователь доступ к temprole
    public boolean getCanGiveTempRole() {
        return canGiveTempRole;
    }

    // Установить значение, имеет ли пользователь доступ к temprole
    public void setCanGiveTempRole(boolean canGiveTempRole) {
        this.canGiveTempRole = canGiveTempRole;
    }
}
