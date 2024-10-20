package common.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import common.models.Member;

@Repository
public interface MemberRepository extends CrudRepository<Member, String> {
    // ...
}