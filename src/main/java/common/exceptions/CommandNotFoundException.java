package common.exceptions;

import common.enums.ExceptionsCodes;

public class CommandNotFoundException extends BaseException {

    // Исключение: Пользователь не найден
    public CommandNotFoundException() {
        super("Unknown command", ExceptionsCodes.COMMAND_NOT_FOUND);
    }
}