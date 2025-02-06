package spring.jpabasic.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
//    @Column(name = "member_id")
    private Long id;

    private String name;

    /* Embedded type
    * - JPA 에서는 새로운 값 타입을 직접 정의해서 사용할 수 있는데, 이 것을 Embedded type 라고 한다
    * - 사용자 지정 클래스를 새로운 값 타입으로 설정한다.
    * - Embedded 값 타입으로 사용되는 클래스에 @Embeddable 어노테이션을 붙여줘야 한다.
    * - 사용자 지정 클래스: Address에 @Embeddable 어노테이션을 붙여야 함
    * */
    @Embedded
    private Address address;

     /* Hibernate Collection
     * JPA 는 자바에서 기본으로 제공하는 Collection, List, Set, Map 컬렉션을 지원
     * 일대다, 다다대 엔터티 관계 매핑할 경우 등 값 다입을 하나 이상 보관할 경우 사용
     * 하이버네이트는 엔티티를 영속 상태로 만들 때 컬렉션 필드로 감싸서 사용한다.
     *  -> 엔티티를 영속 상태로 만들 때, 원본 컬렉션을 감싸고 있는 내장 컬렉션 - 래퍼 컬랙션을 생성한다.
     *  -> 내장 컬렉션을 사용하도록 참조를 변경함
     *  -> 따라서, 컬렉션 사용 시 **즉시 초기화**를 권장
     * */
    @OneToMany(mappedBy = "member") // order 테이블의 member 필드에 의해 매핑됨
    private List<Order> orders = new ArrayList<>(); // 즉시 초기화

}
