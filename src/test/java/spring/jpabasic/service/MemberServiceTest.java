package spring.jpabasic.service;

import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spring.jpabasic.domain.Member;
import spring.jpabasic.repository.MemberRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Test
    @Transactional
    public void testJoin(){
        Member member = new Member();
        member.setName("member1");

        Long savedId = memberService.join(member);

        assertThat(savedId).isEqualTo(member.getId());
    }

    @Test
    @Transactional
    public void joinExceptionTest() throws Exception{
        Member member1 = new Member();
        member1.setName("member1");

        Member member2 = new Member();
        member2.setName("member1");

        memberService.join(member1);
        assertThrows(IllegalStateException.class,
                () -> memberService.join(member2));
    }

    @Test
    @Transactional
    public void testFindOne(){
        Member member = new Member();
        member.setName("findMember");

        Long savedId = memberService.join(member);

        Member foundMember = memberService.findOne(savedId);

        assertThat(member).isSameAs(foundMember);
    }

    @Test
    @Transactional
    public void testFindMembers(){
        Member member1 = new Member();
        member1.setName("member1");
        Member member2 = new Member();
        member2.setName("member2");
        Member member3 = new Member();
        member3.setName("member3");

        memberService.join(member1);
        memberService.join(member2);
        memberService.join(member3);

        List<Member> members = memberService.findMembers();
        assertThat(members.size()).isEqualTo(3);

    }

}