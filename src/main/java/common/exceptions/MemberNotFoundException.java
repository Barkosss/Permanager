package common.exceptions;

public class MemberNotFoundException extends BaseException {

    public MemberNotFoundException() {
        super("Unknown member", 504);
    }
}
