package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ItemValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
        // item == clazz
        // item = subItem(item의 자식)
        // 즉, isAssignableFrom을 쓰면 item뿐 아니라 자식도 체크해줌
    }

    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item)target;
        if(!StringUtils.hasText(item.getItemName())){
            errors.rejectValue("itemName","required"); // rejectValue는 사실상 new FieldError를 만들어줌
            // 필드값, 에러메시지값을 넣는다 물론 뒤에 파라미터 추가 가능(object[]로)
        }
        if(item.getPrice()==null || item.getPrice()<1000 || item.getPrice()>1000000){
            errors.rejectValue("price","range",new Object[]{1000,1000000},null);
        }
        if(item.getQuantity()==null || item.getQuantity()>=9999){
            errors.rejectValue("quantity","max",new Object[]{9999},null);
        }
        // 비지니스 검증 로직(복합 룰 검증)
        if(item.getPrice()!=null && item.getQuantity()!=null){
            int resultPrice = item.getPrice()*item.getQuantity();
            if(resultPrice<10000) {
                errors.reject("totalPriceMin",new Object[]{10000,resultPrice},null);
            }
        }
    }
}
