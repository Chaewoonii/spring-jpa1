package spring.jpabasic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.jpabasic.domain.item.Item;
import spring.jpabasic.repository.ItemRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
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
}
