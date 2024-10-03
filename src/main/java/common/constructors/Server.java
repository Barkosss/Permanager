package common.constructors;

import java.util.List;

public class Server {

    // ID Discord сервера или Telegram беседы (Общее название - Сервер)
    public long id;

    // Список модераторов/администраторов сервера (Общее название - администраторы)
    public List<Member> members;

    // Стандартные права доступа
    public Permissions defaultPermissions;

    public Server(long id, List<Member> members, Permissions defaultPermissions) {
        this.id = id;
        this.members = members;
        this.defaultPermissions = defaultPermissions;
    }


    // Получение ID сервера
    public long getId() {
        return id;
    }

    // Установить ID сервера
    public void setId(long id) {
        this.id = id;
    }

    // Получение список администраторов
    public List<Member> getMembers() {
        return members;
    }

    // Установить список администраторов
    public void setMembers(List<Member> members) {
        this.members = members;
    }

    // Получение список стандартных прав доступа
    public Permissions getDefaultPermissions() {
        return defaultPermissions;
    }

    // Установить список стандартных прав доступа
    public void setDefaultPermissions(Permissions defaultPermissions) {
        this.defaultPermissions = defaultPermissions;
    }
}