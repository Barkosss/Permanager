package Permanager;

public class Member {

    // Id пользователя (Telegram ID или Disocrd ID)
    public long id;

    // Имеет ли возможность кикать
    public boolean canKick;

    // Имеет ли возможность выдавать бан
    public boolean canBan;

    // Имеет ли возможность снимать бан
    public boolean canUnban;

    // Имеет ли возможность выдавать мут
    public boolean canMute;

    // Имеет ли возможность снимать мут
    public boolean canUnmute;

    // Имеет ли возможность выдавать предупреждение
    public boolean canWarn;

    // Имеет ли возможность снимать предупреждение
    public boolean canRemwarn;

    // Имеет ли возможность обнулять предупреждения
    public boolean canResetwarn;

    // Имеет ли возможность очищать сообщение(-я) в чате
    public boolean canClear;

    // Имеет ли возможность выдавать временную роль
    public boolean canTempRole;
};