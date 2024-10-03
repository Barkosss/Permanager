package common.enums;

public class Exceptions {
    public enum Codes {
        WRONG_ANSWER(301),
        GROUP_NOT_FOUND(302),
        MEMBER_NOT_FOUND(303),
        PERMISSIONS_NOT_FOUND(304),
        TASK_NOT_FOUND(305);

        private final int code;

        Codes(int code) {
            this.code = code;
        }

        public int getValue() {
            return code;
        }
    }
}