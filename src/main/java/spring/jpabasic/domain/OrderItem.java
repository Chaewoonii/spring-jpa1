package spring.jpabasic.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring.jpabasic.domain.item.Item;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")

    private Order order;
    private int orderPrice;
    private int count;

    // 주문 상품 정보(OrderItem) 생성
    // orderItem 생성하며 재고도 차감
    public static OrderItem createOrderItem(Item item,  int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count); // 재고 차감
        return orderItem;
    }

    // 주문 취소
    public void cancel() {
        getItem().addStock(count); // 재고 수량 원복
    }

    // 주문 상품 전체 가격 조회
    public int getTotalPrice(){
        return getOrderPrice() * getCount(); // 상품 가격 * 수량
    }
}
