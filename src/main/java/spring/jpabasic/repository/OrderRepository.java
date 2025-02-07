package spring.jpabasic.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.validation.constraints.AssertFalse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import spring.jpabasic.domain.Order;
import spring.jpabasic.domain.OrderSearch;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    // 주문 검색
    // JPQL -> 번거롭고 실수로 인한 버그가 발생할 가능성이 높음
    /*
    public List<Order> findAllByStringJPQL(OrderSearch orderSearch){
        String jpql = "select o from Order o join o.member m"; // 값이 nullable 이므로 동적 쿼리로 해결
        boolean isFirstCondition = true;

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null){
            if (isFirstCondition){ // 처음 조회하는 경우, 조인 해줌
                jpql += " where"; // select o from Order o join o.member m where o.status = :status
                isFirstCondition = false;
            } else {
                jpql += " and"; // select o from Order o join o.member m and o.status = :status
            }
            jpql += " o.status = :status";
        }

        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())){
            if (isFirstCondition){
                jpql += " where";
                isFirstCondition = false
            }else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class).setMaxResults(1000);

        if (orderSearch.getOrderStatus() != null){
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())){
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }
     */

    // JPA Criteria로 처리: JPA Criteria: JPQL을 java 코드로 작성할 수 있도록 도와줌(특히 동적 쿼리)
    // 그래도 복잡합 ---> QueryDSL
    public List<Order> findAllByString(OrderSearch orderSearch){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER); // 회원과 조인
        List<Predicate> criteria = new ArrayList<>();

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null){
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())){
            Predicate name = cb.like(m.<String>get("name"),
                    "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); // 최대 1000ro
        return query.getResultList();
    }

}
