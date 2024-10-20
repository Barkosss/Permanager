package gateway.services;

import common.models.Member;
import common.repositories.MemberRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member sateMember(Member member) {
        return memberRepository.save(member);
    }

    public Optional<Member> getMemberById(String id) {
        return memberRepository.findById(id);
    }

    public Iterable<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public void deleteMember(String id) {
        memberRepository.deleteById(id);
    }
}