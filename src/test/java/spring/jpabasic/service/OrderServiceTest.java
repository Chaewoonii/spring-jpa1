package spring.jpabasic.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import spring.jpabasic.domain.Address;
import spring.jpabasic.domain.Member;
import spring.jpabasic.domain.Order;
import spring.jpabasic.domain.OrderStatus;
import spring.jpabasic.domain.item.Book;
import spring.jpabasic.domain.item.Item;
import spring.jpabasic.exception.NotEnoughStockException;
import spring.jpabasic.repository.OrderRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired EntityManager entityManager;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception{
        // given
        Member member = createMember();
        Book book = createBook("스프링 JPA", 10000, 10);
        int orderCount = 2;

        // when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // then
        Order createdOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, createdOrder.getStatus(), "상품 주문 시 상태는 ORDER");
        assertEquals(1, createdOrder.getOrderItems().size(), "주문한 상품 종류 수가 동일해야 한다");
        assertEquals(10000 * orderCount, createdOrder.getTotalPrice(), "주문 가격은 주문상품 가격 * 수량이다");
        assertEquals(8, book.getStockQuantity(), "주문 수량만큼 재고 감소");
    }

    @Test
    public void 상품주문_재고수량초과() throws Exception{
        // given
        Member member = createMember();
        Item item = createBook("스프링 JPA", 10000, 10);

        int orderCount = 11; // 재고보다 많은 수량 주문

        // when, then
        assertThrows(NotEnoughStockException.class,
                () ->orderService.order(member.getId(), item.getId(), orderCount));
    }

    @Test
    public void 주문취소() throws Exception{
        // given
        Member member = createMember();
        Book item = createBook("스프링 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // when
        orderService.cancelOrder(orderId);

        // then
        Order foundOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCEL, foundOrder.getStatus(), "주문 취소 시 상태는 CANCEL 이다.");
        assertEquals(10, item.getStockQuantity(), "주문이 취소된 상품은 재고가 원상복구 되어야 한다.");
    }


    public Member createMember(){
        Member member = new Member();
        member.setName("회원 1");
        member.setAddress(new Address("서울", "어딘가", "123-456"));
        entityManager.persist(member);
        return member;
    }

    public Book createBook(String name, int price, int stockQuantity){
        Book book = new Book();
        book.setName(name);
        book.setStockQuantity(stockQuantity);
        book.setPrice(price);
        entityManager.persist(book);
        return book;
    }

}