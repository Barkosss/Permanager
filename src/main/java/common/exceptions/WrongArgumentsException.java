package common.exceptions;

import common.enums.Exceptions;

public class WrongArgumentsException extends BaseException {

    // Исключение: Некорректные аргументы
    public WrongArgumentsException() {
        super("Wrong arguments", Exceptions.Codes.WRONG_ANSWER.getValue());
    }
}
