package spring.jpabasic.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import spring.jpabasic.domain.item.Item;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    // 상품 저장
    public void save(Item item){
        // item 은 jpa 가 저장하기 전까지 id 값이 없음
        if (item.getId() == null){
            em.persist(item); // id가 null이면(저장하지 않았다면) 신규 등록
        } else {
            em.merge(item); // 이미 저장된 아이템이면 기존 정보와 merge
        }
    }

    // 상품 단건 조회
    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    // 상품 여러개 조회
    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class) // JPQL
                .getResultList();
    }
}
