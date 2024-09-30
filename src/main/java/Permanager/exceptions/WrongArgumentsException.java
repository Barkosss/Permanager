package exceptions;

import enums.ExceptionsEnum;

public class WrongArgumentsException extends BaseException {

    public WrongArgumentsException() {
        super("Wrong arguments", ExceptionsEnum.Codes.WRONG_ANSWER.getValue());
    }
}
