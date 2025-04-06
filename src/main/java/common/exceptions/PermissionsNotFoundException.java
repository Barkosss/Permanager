package common.exceptions;

import common.enums.ExceptionsCodes;

public class PermissionsNotFoundException extends BaseException {

    // Исключение: Разрешение не найдено
    public PermissionsNotFoundException() {
        super("Unknown member", ExceptionsCodes.PERMISSIONS_NOT_FOUND);
    }
}
