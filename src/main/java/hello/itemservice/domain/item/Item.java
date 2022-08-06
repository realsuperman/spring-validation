package hello.itemservice.domain.item;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
//@ScriptAssert(lang="javascript",script = "_this.price * _this.quantity>=10000",message = "10000원 넘게 입력해라") // 오브젝트 오류 처리
public class Item {

    //@NotNull(groups = UpdateCheck.class)
    private Long id;
    //@NotBlank(groups = {SaveCheck.class,UpdateCheck.class})
    private String itemName;
    //@NotNull(groups = {SaveCheck.class,UpdateCheck.class})
    //@Range(min=1000,max=1000000,groups = {SaveCheck.class,UpdateCheck.class})
    private Integer price;
    //@NotNull(groups = {SaveCheck.class,UpdateCheck.class})
    //@Max(value=9999,groups = {SaveCheck.class})
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}

// 객체 생성시 성공하면 다음으로 생성 실패시 typeMismatch를 FieldError추가