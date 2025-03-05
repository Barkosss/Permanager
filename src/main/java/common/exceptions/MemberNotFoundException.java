package common.exceptions;

import common.enums.ExceptionsCodes;

public class MemberNotFoundException extends BaseException {

    // Исключение: Пользователь не найден
    public MemberNotFoundException() {
        super("Unknown member", ExceptionsCodes.MEMBER_NOT_FOUND);
    }
}
