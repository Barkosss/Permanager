package common.exceptions;

import common.enums.Exceptions;

public class TaskNotFoundException extends BaseException {

    // Исключение: Задача не найдена
    public TaskNotFoundException() {
        super("Unknown member", Exceptions.Codes.TASK_NOT_FOUND.getValue());
    }
}