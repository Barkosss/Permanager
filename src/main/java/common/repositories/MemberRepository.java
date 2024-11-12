package common.repositories;

import common.models.Member;
import common.exceptions.MemberNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class MemberRepository {
    public Map<Long, Member> members = new HashMap<>();

    void create(Member user) {
        // ...
    }

    Member findById(long findMemberId) throws MemberNotFoundException {
        for (Long memberId : members.keySet()) {
            if (memberId == findMemberId) {
                return members.get(memberId);
            }
        }
        throw new MemberNotFoundException();
    }

    void delete(long userId) {

    }
}
