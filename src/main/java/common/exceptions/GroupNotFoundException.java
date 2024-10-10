package common.exceptions;

import common.enums.ExceptionsCodes;

public class GroupNotFoundException extends BaseException {

    // Исключение: Группа не найдена
    public GroupNotFoundException() {
        super("Unknown member", ExceptionsCodes.GROUP_NOT_FOUND);
    }
}