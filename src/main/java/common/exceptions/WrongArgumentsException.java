package common.exceptions;

import common.enums.ExceptionsCodes;

public class WrongArgumentsException extends BaseException {

    // Исключение: Некорректные аргументы
    public WrongArgumentsException() {
        super("Wrong arguments", ExceptionsCodes.WRONG_ANSWER);
    }
}
