package common.constructors;

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
    public boolean canTempRole;

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
        this.canTempRole = false;
    }

    public Permissions(boolean canKick, boolean canBan, boolean canUnban, boolean canMute, boolean canUnMute, boolean canWarn, boolean canRemWarn, boolean canResetWarn, boolean canClear, boolean canTempRole) {
        this.canKick = canKick;
        this.canBan = canBan;
        this.canUnban = canUnban;
        this.canMute = canMute;
        this.canUnMute = canUnMute;
        this.canWarn = canWarn;
        this.canRemWarn = canRemWarn;
        this.canResetWarn = canResetWarn;
        this.canClear = canClear;
        this.canTempRole = canTempRole;
    }

    // ...
    public boolean getCanKick() {
        return canKick;
    }

    // ...
    public void setCanKick(boolean canKick) {
        this.canKick = canKick;
    }

    // ...
    public boolean getCanBan() {
        return canBan;
    }

    // ...
    public void setCanBan(boolean canBan) {
        this.canBan = canBan;
    }

    // ...
    public boolean getCanUnban() {
        return canUnban;
    }

    // ...
    public void setCanUnban(boolean canUnban) {
        this.canUnban = canUnban;
    }

    // ...
    public boolean getCanMute() {
        return canKick;
    }

    // ...
    public void setCanMute(boolean CanMute) {
        this.canMute = CanMute;
    }

    // ...
    public boolean getCanUnMute() {
        return canUnMute;
    }

    // ...
    public void setCanUnMute(boolean canUnMute) {
        this.canUnMute = canUnMute;
    }

    // ...
    public boolean getCanWarn() {
        return canWarn;
    }

    // ...
    public void setCanWarn(boolean canWarn) {
        this.canWarn = canWarn;
    }

    // ...
    public boolean getCanRemWarn() {
        return canRemWarn;
    }

    // ...
    public void setCanRemWarn(boolean canRemWarn) {
        this.canRemWarn = canRemWarn;
    }

    // ...
    public boolean getCanResetWarn() {
        return canResetWarn;
    }

    // ...
    public void setCanResetWarn(boolean canResetWarn) {
        this.canResetWarn = canResetWarn;
    }

    // ...
    public boolean getCanClear() {
        return canClear;
    }

    // ...
    public void setCanClear(boolean canClear) {
        this.canClear = canClear;
    }

    // ...
    public boolean getCanTempRole() {
        return canTempRole;
    }

    // ...
    public void setCanTempRole(boolean canTempRole) {
        this.canTempRole = canTempRole;
    }
}
