package common.exceptions;

import common.enums.ExceptionsEnum;

public class WrongArgumentsException extends BaseException {

    public WrongArgumentsException() {
        super("Wrong arguments", ExceptionsEnum.Codes.WRONG_ANSWER.getValue());
    }
}
