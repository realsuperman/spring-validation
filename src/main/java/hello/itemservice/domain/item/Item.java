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

    private Long id;
    @NotBlank
    private String itemName;
    @NotNull
    @Range(min=1000,max=1000000)
    private Integer price;
    @NotNull
    @Max(9999)
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