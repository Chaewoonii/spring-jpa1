package spring.jpabasic.domain.item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.weaver.NewConstructorTypeMunger;
import spring.jpabasic.domain.Category;
import spring.jpabasic.exception.NotEnoughStockException;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    // item 들의 공통 속성
    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    // 비즈니스 로직
    // 해당 데이터를 가지고 있는 쪽에 비스니스 메서드가 있는 것이 좋음(객체지향적, 응집력 ↑)
    // 상품 수량 증가
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    // 상품 수량 감소
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0){
            throw new NotEnoughStockException("상품 수량이 부족합니다. 현재 재고는 " + this.stockQuantity + "개 입니다.");
        }
        this.stockQuantity = restStock;
    }

}
