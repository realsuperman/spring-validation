package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    //@PostMapping("/add")
    /* 바인딩리절트가 있으면 파라미터들의 문제가 있어도 컨트롤러가 호출은 된다 예를 들어서 숫자만 넣어야 하는
       필드에 문자를 넣으면 이건 파라미터 자체가 문제라고 볼 수 있다. 이랬을 때 바인딩 리절트가 없으면
       컨트롤러의 로직을 안타고 이상한 에러 페이지가 뜨게 되지만 바인딩 리절트가 있으면 문제가 된 예외 메시지를
       붙여서 컨트롤러 로직을 수행함
       정리하자면 ModelAttribute의 객체에 타입 오류 등으로 바인딩이 실패하는 경우 스프링이 FieldError를 생성해서
       BingdingResult에 넣어준다
     */
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        // @ModelAttribute Item item하면 model에 자동으로 item을 put해줌 BindingResult는 ModelAttribute의 뒤에서 선언되어야 함(순서 중요)

        // 검증 로직
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.addError(new FieldError("item","itemName","상품 이름은 필수"));
            // 대상 object와 object의 field 마지막으로 에러 메시지를 바인딩 result 객체에 넣어준다
            // FieldError 객체에 담아서 넘겨주면 된다 (object의 특정 필드의 경우 이렇게 한다)
            // 만약 object의 특정 필드가 아니라면 new ObjectError를 쓰면 된다 -> 인자의 개수가 다름에 주의
        }
        if(item.getPrice()==null || item.getPrice()<1000 || item.getPrice()>1000000){
            bindingResult.addError(new FieldError("item","price","가격은 1,000원 ~ 1,000,000 까지 허용합니다."));
        }
        if(item.getQuantity()==null || item.getQuantity()>=9999){
            bindingResult.addError(new FieldError("item","quantity","수량은 최대 9999까지만 가능"));
        }
        // 비지니스 검증 로직(복합 룰 검증)
        if(item.getPrice()!=null && item.getQuantity()!=null){
            int resultPrice = item.getPrice()*item.getQuantity();
            if(resultPrice<10000) {
                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값"));
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){ // 에러가 있다면
            log.info("errors = {}",bindingResult);
            return "validation/v2/addForm"; // 스프링에서 제공하는 bindingResult는 자동으로 모델에 담겨짐
        }

        // 검증 성공시 PRG 패턴 적용됨
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /*
    addError(new FieldError의 파라미터 정보
    objectName : 오류가 발생한 객체 이름
    field : 오류 필드
    rejectedValue : 사용자가 입력한 값(거절된 값)
    bindingFailure : 타입 오류 같은 바인딩 실패인지, 검증 실패인지 구분 값
    codes : 메시지 코드 ( errors.properties에서 사용하는 메시지 값),String 배열로 넘겨야함 -> 사용안하면 null로
    arguments : 메시지에서 사용하는 인자 -> 메시지에서 파라미터 사용시 넣어주는 값 (object 배열로 넘겨야함)
    defaultMessage : 기본 오류 메시지
     */
    //@PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.addError(new FieldError("item","itemName",item.getItemName(),false,null,null,"상품 이름은 필수"));
            // 만약 사용자가 넘겨준 값을 유지하고 싶으면 오버로딩된 다른 메소드를 사용하면 된다 그 메소드는
            // 필드 이름 다음으로 item.getXXX로 사용자가 입력한 값을 찾고 false null null 을 넣어주면 사용 가능하다
            // 참고로 false로 넘긴 이유는 사용자가 넘긴 값이 일단은 올바른 값이라고 가정했기 때문이다
        }
        if(item.getPrice()==null || item.getPrice()<1000 || item.getPrice()>1000000){
            bindingResult.addError(new FieldError("item","price",item.getPrice(),false,null,null,"가격 정보 이상함"));
            /*
             사실상 item.getPrice()는 존재하지 않을 수 있다(만약 문자열을 넣었다면 price에 안들어갔으니까 이럴때는 스프링이 알아서 넣어준다) 정리하자면 아래와 같다
             타입 오류로 바인딩에 실패하면 스프링은 FieldError 를 생성하면서 사용자가 입력한 값을 넣어둔다.
             그리고 해당 오류를 BindingResult 에 담아서 컨트롤러를 호출한다. 따라서 타입 오류 같은 바인딩
             실패시에도 사용자의 오류 메시지를 정상 출력할 수 있다
             */
        }
        if(item.getQuantity()==null || item.getQuantity()>=9999){
            bindingResult.addError(new FieldError("item","quantity",item.getQuantity(),false,null,null,"수량 정보 이상함"));
        }
        // 비지니스 검증 로직(복합 룰 검증)
        if(item.getPrice()!=null && item.getQuantity()!=null){
            int resultPrice = item.getPrice()*item.getQuantity();
            if(resultPrice<10000) {
                bindingResult.addError(new ObjectError("item",null,null, "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값"));
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){ // 에러가 있다면
            log.info("errors = {}",bindingResult);
            return "validation/v2/addForm"; // 스프링에서 제공하는 bindingResult는 자동으로 모델에 담겨짐
        }

        // 검증 성공시 PRG 패턴 적용됨
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    //@PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.addError(new FieldError("item","itemName",item.getItemName(),false,new String[]{"required.item.itemName"},null,null));
        }
        if(item.getPrice()==null || item.getPrice()<1000 || item.getPrice()>1000000){
            bindingResult.addError(new FieldError("item","price",item.getPrice(),false,new String[]{"range.item.price"},new Object[]{1000,1000000},null));
        }
        if(item.getQuantity()==null || item.getQuantity()>=9999){
            bindingResult.addError(new FieldError("item","quantity",item.getQuantity(),false,new String[]{"max.item.quantity"},new Object[]{9999},null));
        }
        // 비지니스 검증 로직(복합 룰 검증)
        if(item.getPrice()!=null && item.getQuantity()!=null){
            int resultPrice = item.getPrice()*item.getQuantity();
            if(resultPrice<10000) {
                bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"},new Object[]{10000, resultPrice}, null));
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){ // 에러가 있다면
            log.info("errors = {}",bindingResult);
            return "validation/v2/addForm"; // 스프링에서 제공하는 bindingResult는 자동으로 모델에 담겨짐
        }

        // 검증 성공시 PRG 패턴 적용됨
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    // rejectValue의 경우 field에러면 rejectValue(필드명,에러메시지,파라미터{new Object[]로 해야함})
    // 단 에러메시지의 경우 required라고 지정하면 required.+ModelAttribute의 이름(item). + 필드명이 자동으로 붙는다
    // 예를들어 required라고 하면 required.item(ModelAttribute이름).itemName(field명)이다.
    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.rejectValue("itemName","required"); // rejectValue는 사실상 new FieldError를 만들어줌
            // 필드값, 에러메시지값을 넣는다 물론 뒤에 파라미터 추가 가능(object[]로)
        }
        if(item.getPrice()==null || item.getPrice()<1000 || item.getPrice()>1000000){
            bindingResult.rejectValue("price","range",new Object[]{1000,1000000},null);
        }
        if(item.getQuantity()==null || item.getQuantity()>=9999){
            bindingResult.rejectValue("quantity","max",new Object[]{9999},null);
        }
        // 비지니스 검증 로직(복합 룰 검증)
        if(item.getPrice()!=null && item.getQuantity()!=null){
            int resultPrice = item.getPrice()*item.getQuantity();
            if(resultPrice<10000) {
                bindingResult.reject("totalPriceMin",new Object[]{10000,resultPrice},null);
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){ // 에러가 있다면
            return "validation/v2/addForm"; // 스프링에서 제공하는 bindingResult는 자동으로 모델에 담겨짐
        }

        // 검증 성공시 PRG 패턴 적용됨
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }
}