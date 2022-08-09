## 개요
사용자의 요청 정보가 올바르지 않을 수 있습니다. 그런 경우 프론트에서 요청 정보를 필터링 할 수 있지만 프론트에서만 
해당 정보를 필터링 한다면 매우 위험할 수 있습니다. HTTP 요청의 경우 조작이 가능하기에 반드시 백엔드 쪽에서 사용자의 요청 정보에 대해서 필터링을 해주어야 합니다
해당 리포지토리에서는 스프링에서 사용자의 요구사항을 검증하는 방법에 대해서 학습을 하였습니다

## 기술
java,Spring MVC

## 학습한 내용
1. BindingResult
-> 스프링에서는 검증 오류를 처리하기 위해 BindingResult라는 것을 제공을 합니다 해당 BindingResult를 체크하려는 대상의 뒤에다가 선언을 하고 사용하면 됩니다
예를들어 item 객체를 검증하려 하면 test(Item item,BindingResult bindingResult) 이런식으로 작성하면 됩니다
##
2. FieldError, ObjectError
-> BindingResult는 FieldError와 ObjectError를 설정할 수 있습니다 FieldError의 경우 특정 객체의 필드값이 특정 조건을 만족하지 않는 경우 지정할 수 있고
ObjectEror의 경우 비지니스 적으로 특정 조건을 만족하지 않는 경우 지정할 수 있습니다
##
3. Bean Validation
-> 해당 객체의 클래스 파일에 검증기능을 적용할 수 있습니다. @NotBlank,@NotNull등의 애노테이션을 사용할 수 있습니다 이렇게 사용하는 경우
test(@Validated Item item) 이런식으로 검증할 객체의 앞에 @Validated나 @Valid 애노테이션을 주어야 합니다
