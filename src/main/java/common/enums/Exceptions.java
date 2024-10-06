package common.enums;


/**
 * Перечисление кодов ошибок
 */
public class Exceptions {
    public enum Codes {
        WRONG_ANSWER(301), // Неправильный аргумент
        GROUP_NOT_FOUND(302), // Группа не найдена
        MEMBER_NOT_FOUND(303), // Пользователь не найден
        PERMISSIONS_NOT_FOUND(304), // Разрешение не найдено
        TASK_NOT_FOUND(305); // Задача не найдена

        private final int code;

        Codes(int code) {
            this.code = code;
        }

        public int getValue() {
            return code;
        }
    }
}