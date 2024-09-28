package constructors;

import java.util.ArrayList;

public class Server {

    // ID Discord сервера или Telegram беседы (Общее название - Сервер)
    public long id;

    // Список модераторов/администраторов сервера (Общее название - администраторы)
    public ArrayList<Member> members = new ArrayList<>();

    // Стандартные права доступа
    public Permissions defaultPermissions;

    public Server(long id, ArrayList<Member> members, Permissions defaultPermissions) {
        this.id = id;
        this.members = members;
        this.defaultPermissions = defaultPermissions;
    }

    // Получение список администраторов
    public ArrayList<Member> getMembers() {
        return members;
    }

    // Установить список администраторов
    public void setMembers(ArrayList<Member> members) {
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