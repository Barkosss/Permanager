package common.exceptions;

import common.enums.Exceptions;

public class PermissionsNotFoundException extends BaseException {

    public PermissionsNotFoundException() {
        super("Unknown member", Exceptions.Codes.PERMISSIONS_NOT_FOUND.getValue());
    }
}
