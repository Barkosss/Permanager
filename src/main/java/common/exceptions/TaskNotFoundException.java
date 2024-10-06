package common.exceptions;

import common.enums.Exceptions;

public class TaskNotFoundException extends BaseException {

    public TaskNotFoundException() {
        super("Unknown member", Exceptions.Codes.TASK_NOT_FOUND.getValue());
    }
}