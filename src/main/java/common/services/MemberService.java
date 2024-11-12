package common.services;

import common.repositories.UserRepository;

public class MemberService {
    private final UserRepository userRepository;

    public MemberService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
