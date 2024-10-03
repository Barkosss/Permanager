package common.exceptions;

import common.enums.Exceptions;

public class MemberNotFoundException extends BaseException {

    public MemberNotFoundException() {
        super("Unknown member", Exceptions.Codes.MEMBER_NOT_FOUND.getValue());
    }
}
