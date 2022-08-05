package hello.itemservice.validation;

import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageCodesResolverTest {
    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    @Test
    void messageCodesResolverObject(){
        String[] messageCode = codesResolver.resolveMessageCodes("required","item");
        for(String code : messageCode){
            System.out.println(code);
        }
        assertThat(messageCode).containsExactly("required.item","required");
    }

    @Test
    void messageCodesResolverField(){
        String[] messageCodes = codesResolver.resolveMessageCodes("required","item","itemName",String.class);
        //{required.item.itemName > required.itemName > required.java.lang.String > required} 이렇게 생성됨
        for(String message : messageCodes){
            System.out.println(message);
        }
        /*
            1. bindingResult.rejectValue("itemName","required");
            위의 rejectValue는 내부적으로 codesResolver를 사용하여 String[]을 조회한다
            그리고 new FieldError(객체(이미 알고있음),itemName(인자로준거),null,false,1의결과,null) 이렇게 만들어줌
         */
        assertThat(messageCodes).containsExactly("required.item.itemName","required.itemName","required.java.lang.String","required");
    }
}
