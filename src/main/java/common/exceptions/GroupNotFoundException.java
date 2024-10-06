package common.exceptions;

import common.enums.Exceptions;

public class GroupNotFoundException extends BaseException {

    public GroupNotFoundException() {
        super("Unknown member", Exceptions.Codes.GROUP_NOT_FOUND.getValue());
    }
}