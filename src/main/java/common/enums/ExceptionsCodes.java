package common.enums;


/**
 * Перечисление кодов ошибок
 */
public enum ExceptionsCodes {
    /**
     * Неправильный аргумент
     */
    WRONG_ANSWER(401),
    /**
     * Группа по его названию не найдена
     */
    GROUP_NOT_FOUND(402),
    /**
     * Пользователь по его ID не найден
     */
    MEMBER_NOT_FOUND(403),
    /**
     * Задача по его ID не найдена
     */
    TASK_NOT_FOUND(404),
    /**
     * Команда не найдена
     */
    COMMAND_NOT_FOUND(405),
    /**
     * Разрешение не найдено
     */
    PERMISSIONS_NOT_FOUND(501);


    private final int code;

    ExceptionsCodes(int code) {
        this.code = code;
    }

    public int getValue() {
        return code;
    }
}
