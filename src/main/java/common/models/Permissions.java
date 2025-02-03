package common.models;

public class Permissions {

    public enum Permission {
        KICK("KICK"),
        BAN("BAN"),
        UNBAN("UNBAN"),
        MUTE("MUTE"),
        UNMUTE("UNMUTE"),
        WARN("WARN"),
        REMWARN("REMWARN"),
        RESETWARNS("RESETWARNS"),
        CLEAR("CLEAR"),
        CONFIG("CONFIG");

        final String permission;

        Permission(String permission) {
            this.permission = permission;
        }

        public String getPermission() {
            return permission;
        }
    }

    // Имеет доступ к настройкам
    boolean canConfig;

    // Имеет ли возможность кикать
    boolean canKick;

    // Имеет ли возможность выдавать бан
    boolean canBan;

    // Имеет ли возможность снимать бан
    boolean canUnban;

    // Имеет ли возможность выдавать мут
    boolean canMute;

    // Имеет ли возможность снимать мут
    boolean canUnMute;

    // Имеет ли возможность выдавать предупреждение
    boolean canWarn;

    // Имеет ли возможность снимать предупреждение
    boolean canRemWarn;

    // Имеет ли возможность обнулять предупреждения
    boolean canResetWarn;

    // Имеет ли возможность очищать сообщение(-я) в чате
    boolean canClear;

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
    }

    public boolean canPermission(Permission permission) {

        if (this.canConfig) {
            return true;
        }

        return switch (permission) {
            case KICK -> this.canKick;
            case BAN -> this.canBan;
            case UNBAN -> this.canUnban;
            case MUTE -> this.canMute;
            case UNMUTE -> this.canUnMute;
            case WARN -> this.canWarn;
            case REMWARN -> this.canRemWarn;
            case RESETWARNS -> this.canResetWarn;
            case CLEAR -> this.canClear;
            case CONFIG -> this.canConfig;
        };
    }

    public void setPermission(Permission permission, boolean permissionStatus) {
        switch (permission) {
            case KICK -> this.canKick = permissionStatus;
            case BAN -> this.canBan = permissionStatus;
            case UNBAN -> this.canUnban = permissionStatus;
            case MUTE -> this.canMute = permissionStatus;
            case UNMUTE -> this.canUnMute = permissionStatus;
            case WARN -> this.canWarn = permissionStatus;
            case REMWARN -> this.canRemWarn = permissionStatus;
            case RESETWARNS -> this.canResetWarn = permissionStatus;
            case CLEAR -> this.canClear = permissionStatus;
            case CONFIG -> this.canConfig = permissionStatus;
        }
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
    public void setCanMute(boolean canMute) {
        this.canMute = canMute;
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
}
