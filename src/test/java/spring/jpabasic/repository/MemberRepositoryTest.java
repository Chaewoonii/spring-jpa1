package spring.jpabasic.repository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import spring.jpabasic.domain.Member;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void testMember() throws Exception{
        //given
        Member member = new Member();
        member.setName("memberA");

        //when
        Long savedId = memberRepository.save(member);
        Member findMember = memberRepository.find(savedId);

        //then
        assertThat(findMember.getId()).isEqualTo(savedId);
        assertThat(findMember.getName()).isEqualTo(member.getName());

        // 영속성 컨텍스트에서 식별자가 같으면 같은 엔터티로 인식한다.
        // 1차 캐시에서 관리하던 엔터티를 불러오는 것이며, SELECT 쿼리도 날리지 않음.
        System.out.println("findMember == member: " + (findMember == member));

    }

}