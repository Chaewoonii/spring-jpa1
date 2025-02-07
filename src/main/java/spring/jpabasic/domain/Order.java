package spring.jpabasic.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    // EAGER: 즉시 로딩. 조회 시 연관된 모든 데이터를 조회 -> 예측과 추적이 어려움. 특히 JPQL을 실행할 때 N+1 문제가 자주 발셍
    // 실무에서 모든 연관관계는 지연 로딩(LAZY) 으로 설정.
    // @OneToOne, @ManyToOne 관계는 기본이 즉시 로딩이므로 직접 지연 로딩으로 설정해야 함.
    // OneToMany -> 기본이 LAZY
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 주문회원

    // member 테이블에 order 필드와 매핑됨.
    // CascadeType.ALL: 지울 때 자식 속성 모두 삭제(Order 삭제 -> OrderItem 삭제)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;

    private OrderStatus status;

    // 양방향 연관관계 셋팅 (편의 메서드): 핵심적으로 컨트롤 하는 쪽에 코드가 위치하는 것이 좋다.
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
        // 주문이 생성되면 member를 지정해줘야 함 -> order.setMember()
        // 멤버는 배정받은(?) order를 가져야 함 -> member.getOrders().add(order)
        // setMember 에 위의 과정을 하나로 묶기
    }

    // order <-> orderItem 도 양방향
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem); // 주문한 목록에 아이템을 넣고
        orderItem.setOrder(this); // 아이템에 주문을 셋팅
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    // 주문 생성 메서드: 주문 생성에 관련된 로직들을 Order 에서 처리: 응집력 향상
    // 주문 생성 관련하여 수정 시 Order 만 고치면 된다.
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem: orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    // 주문 취소
    public void cancel(){
        if (delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송이 완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem: orderItems){
            orderItem.cancel();
        }
    }

    // 전체 주문 가격 조회
    public int getTotalPrice(){
        /*
        * int totalPrice = 0
        * for (OrderItem orderItem: orderItems){
        *   totalPrice += orderItem.getTotalPrice();
        * }와 같은 코드
        * */
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }


}
