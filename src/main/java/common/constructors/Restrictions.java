package common.constructors;

public class Restrictions {

    // Имеет ли возможность кикать
    public Limit canKick;

    // Имеет ли возможность выдавать бан
    public Limit canBan;

    // Имеет ли возможность снимать бан
    public Limit canUnban;

    // Имеет ли возможность выдавать мут
    public Limit canMute;

    // Имеет ли возможность снимать мут
    public Limit canUnMute;

    // Имеет ли возможность выдавать предупреждение
    public Limit canWarn;

    // Имеет ли возможность снимать предупреждение
    public Limit canRemWarn;

    // Имеет ли возможность обнулять предупреждения
    public Limit canResetWarn;

    // Имеет ли возможность очищать сообщение(-я) в чате
    public Limit canClear;

    // Имеет ли возможность выдавать временную роль
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