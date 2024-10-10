package common.enums;


/**
 * Перечисление кодов ошибок
 */
public enum ExceptionsCodes {
    /**
     * Неправильный аргумент
     */
    WRONG_ANSWER(301),
    /**
     * Группа не найдена
     */
    GROUP_NOT_FOUND(302),
    /**
     * Пользователь не найден
     */
    MEMBER_NOT_FOUND(303),
    /**
     * Разрешение не найдено
     */
    PERMISSIONS_NOT_FOUND(304),
    /**
     * Задача не найдена
     */
    TASK_NOT_FOUND(305);


    private final int code;

    ExceptionsCodes(int code) {
        this.code = code;
    }

    public int getValue() {
        return code;
    }
}