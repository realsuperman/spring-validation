package hello.itemservice.web.validation;

import hello.itemservice.web.validation.form.ItemSaveForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/validation/api/items")
public class ValidationItemApiController {
    @PostMapping("/add")
    // 파라미터 매핑이 안되면 컨트롤러 로직이 실행안하고 팅겨짐(ModelAttribute는 파라미터 매핑 안되도 컨트롤러 로직은 수행됨)
    public Object addItem(@RequestBody @Validated ItemSaveForm form, BindingResult bindingResult){
        log.info("API 컨트롤러 호출");
        if(bindingResult.hasErrors()){
            log.info("검증 오류 발생 errors{}",bindingResult);
            return bindingResult.getAllErrors(); // 바인딩리절트가 가지고 있는 모든 필드오류,오브젝트 오류를 반환함
        }
        log.info("성공 로직 실행");
        return form;
    }
}
