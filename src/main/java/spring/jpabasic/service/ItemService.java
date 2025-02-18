package spring.jpabasic.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.jpabasic.domain.item.Book;
import spring.jpabasic.domain.item.Item;
import spring.jpabasic.repository.ItemRepository;

import java.util.List;

@Service
@Transactional//(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    // 상품 저장
    @Transactional // readOnly = true 라서 저장되지 않음. 상품 저장을 위해 어노테이션 추가
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    // 상품 목록 조회
    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    // 단건 상품 조회
    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }

    // EntityManager에서 id로 엔티티를 찾고(1차 캐시 또는 DB) 엔티티 정보를 수정하면
    // 변경감지에 의해 트랜잭션 종료 시점(commit)에 Update 쿼리 실행
    @Transactional
    public void updateItem(Long id, String name, int price, int stockQuantity){
        Item item = itemRepository.findOne(id);
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
    }

/*     상품 정보 업데이트: 변경감지 기능 활용(dirty checking)
     - 영속성 컨텍스트(EntityManager)에서 엔티티 조회(영속 상태), 데이터 수정
     - 준영속(detached): 영속성 컨텍스트에 저장되었다가 분리된 상태 (em이 관리하지 않음).*/
    @Transactional
    public void updateItem(Long itemId, Book param){
        Item findItem = itemRepository.findOne(itemId); // 영속성 컨텍스트에서 엔티티 조회(managed): 스냅샷

        findItem.setPrice(param.getPrice()); // 데이터 수정
        findItem.setName(param.getName());
        findItem.setStockQuantity(param.getStockQuantity());

        // 트랜젝션 커밋 시점에 스냅샷과 비교하여 변경감지(dirty checking) -> 데이터 수정(UPDATE 실행
    }

/*    상품 정보 업데이트: 병합(merge)
     - 준영속 상태의 엔티티를 영속 상태로 변경할 때 사용되는 병합(merge) 기능을 사용하여 데이터를 수정할 수도 있음
     - null 값이 있을 경우 해당 속성에 값이 있더라도 null로 바뀌므로 주의 필요
     - 동작과정
        1) merge(item) 실행
        2) item 의 id 값으로(준영속 엔티티의 식별자) 1차 캐시에서 엔티티 조회: find()
            2-1) 1차 캐시에 엔티티가 없으면 DB에서 엔티티 조회, 1차캐시에 저장
        3) 조회한 영속 엔티티에 파라미터로 넘어온 엔티티의 값을 채워넣음 (merge(item)): 병합
        4) 영속 상태의 엔티티 반환
    @Transactional
    public void update(Book param){
        Book managed = em.merge(param);
    }*/
}
