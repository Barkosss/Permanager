package common.exceptions;

import common.enums.ExceptionsCodes;

public class TaskNotFoundException extends BaseException {

    // Исключение: Задача не найдена
    public TaskNotFoundException() {
        super("Unknown member", ExceptionsCodes.TASK_NOT_FOUND);
    }
}