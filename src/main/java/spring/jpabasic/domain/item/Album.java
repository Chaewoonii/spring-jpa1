package spring.jpabasic.domain.item;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("A") // 구분할 수 있는 값
@Getter @Setter
public class Album extends Item{
    private String author;
    private String isbn;
}
