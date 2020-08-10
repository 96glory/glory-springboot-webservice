# glory-springboot-webservice
> [도서](https://book.naver.com/bookdb/book_detail.nhn?bid=15871738)

### 사용 스택

- Java & JUnit4
- Spring & Spring Boot
- Spring Web MVC
- Spring JPA
- Lombok

### 요구사항

- 게시판 기능
    - 게시판 조회
    - 게시글 등록
    - 게시글 수정
    - 게시글 삭제
- 회원 기능
    - 구글 / 네이버 소셜 로그인
    - 로그인한 사용자 글 작성 권한
    - 본인 작성 글에 대한 권한 관리

### 배운 내용 기록

#### TDD & Unit Test

- TDD : 테스트가 주도하는 개발
    - TDD에 대한 자세한 이야기 : [링크](https://repo.yona.io/doortts/blog/issue/1)

#### JPA : 관계형 DB와 객체지향 프로그래밍 사이에서

- 패러다임 불일치
    - 관계형 DB : 어떻게 데이터를 저장할지에 초점이 맞춰진 기술
    - 객체지향 : 메시지를 기반으로 기능과 속성을 한 곳에서 관리하는 기술
- JPA
    - 서로 지향하는 바가 다른 2개 영역을 중간에서 패러다임 일치를 시켜주기 위한 기술
    - 구현체 교체의 용이성 : Hibernate 외에 다른 구현체로 쉽게 교체하기 위함 (Jedis에서 Lettuce, ...)
    - 저장소 교체의 용이성 : 관계형 DB 외에 다른 저장소로 쉽게 교체하기 위함 (MongoDB로의 교체, Redis로의 교체, ...)

#### Annotation

- @SpringBootApplication
    - 스프링 부트의 자동 설정, 스프링 빈 읽기와 생성을 모두 자동으로 설정됩니다.
    - @SpringBootApplication이 있는 위치부터 설정을 읽어가기 때문에 이 애노테이션을 포함한 클래스는 항상 프로젝트의 최상단에 위치해야만 합니다.
- @RestController
    - 컨트롤러를 JSON으로 반환하는 컨트롤러를 만들어줍니다.
    - 클래스 내에 있는 모든 메소드에 @ResponseBody를 얹어준 효과와 같습니다.
- @Autowired
    - 스프링이 관리하는 빈을 주입 받습니다.
- @RequestParam
    - 외부에서 API로 넘긴 파라미터를 가져오는 어노테이션
    - [사용 예시](https://github.com/96glory/glory-springboot-webservice/blob/master/src/main/java/me/glory/springboot/web/HelloController.java)
- Test
    - @WebMvcTest
        - 여러 스프링 테스트 애노테이션 중, Web MVC에 집중할 수 있는 애노테이션
        - @Controller, @ControllerAdvice 등을 사용할 수 있지만, @Service, @Component, @Repository 등을 사용할 수 없습니다.
- Lombok
    - @Getter
        - 선언된 모든 필드의 get 메소드를 생성해 줍니다.
    - @RequiredArgsConstructor
        - 선언된 모든 final 필드가 포함된 생성자를 생성해 줍니다.
        - final이 없는 필드는 생성자에 포함되지 않습니다.