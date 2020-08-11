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
- Test Code Function
    - ```MockMvc mockMvc;```
        - 웹 API를 테스트할 때 사용합니다.
        - 이 클래스를 통해 HTTP GET, POST 등에 대한 API 테스트를 할 수 있습니다.
    - ```mockMvc.perform(get("/hello"))```
        - ```MockMvc```를 통해 /hello 주소로 HTTP GET 요청을 합니다.
    - ```.andExpect(status().isOk())```
        - ```mockMvc.perform```의 결과에서 나오는 __HTTP Header의 Status__ 를 검증합니다.
        - ```isOk()```이므로 200인지 아닌지를 확인합니다.
    - ```.andExpect(content().string(hello))```
        - ```mockMvc.perform```의 결과에서 나오는 __응답 본문의 내용__ 을 검증합니다.
    - ```.andExpect(jsonPath("$.name").isEqualTo("glory"))```
        - JSON 응답값을 필드별로 검증할 수 있는 메서드입니다.
        - $를 기준으로 필드명을 명시합니다.
        
#### JPA : 관계형 DB와 객체지향 프로그래밍 사이에서

- 패러다임 불일치
    - 관계형 DB : 어떻게 데이터를 저장할지에 초점이 맞춰진 기술
    - 객체지향 : 메시지를 기반으로 기능과 속성을 한 곳에서 관리하는 기술
- JPA
    - 서로 지향하는 바가 다른 2개 영역을 중간에서 패러다임 일치를 시켜주기 위한 기술
    - 구현체 교체의 용이성 : Hibernate 외에 다른 구현체로 쉽게 교체하기 위함 (Jedis에서 Lettuce, ...)
    - 저장소 교체의 용이성 : 관계형 DB 외에 다른 저장소로 쉽게 교체하기 위함 (MongoDB로의 교체, Redis로의 교체, ...)
    - ```extends JpaRepository<Entity 클래스, PK 타입> ```
        - 기본적인 CRUD 메서드가 자동으로 생성됩니다.
        - Entity 클래스와 EntityRepository가 같은 위치에 있다면, ```@Repository```를 선언할 필요가 없다.
    - EntityRepository의 함수
        - ```save(Entity entity)```
            - insert / update 쿼리. id가 있다면 update, id가 없다면 update
        - ```findAll()```
            - entity에 있는 모든 데이터를 조회해오는 메서드 

#### API를 만들기 위한 총 3개의 클래스

![image](https://user-images.githubusercontent.com/52440668/89849264-1bd0dd00-dbc3-11ea-9188-5dc848bc07ae.png)
- Web Layer
    - 컨트롤러와 JSP/Freemarker 등의 뷰 템플릿 영역입니다.
    - 이외에도 필터, 인터셉터, 컨트롤러 어드바이스 등 외부 요청과 응답에 대한 전반적인 영역을 이야기합니다.
- Service Layer
    - ```@Service```나 ```@Transactional```에 사용되는 서비스 영역입니다.
    - 일반적으로 컨트롤러와 Dao의 중간 영역입니다.
    - __트랜잭션, 도메인 간 순서 보장의 역할만__
- Repository Layer
    - DB와 같이 데이터 저장소에 접근하는 영역입니다.
- Dto
    - 계층 간에 데이터 교환을 위한 객체
- Domain Model
    - 도메인을 모두가 쉽게 이해할 수 있도록 단순화 시킨 것을 도메인 모델이라고 합니다.
    - __비즈니스 로직을 처리__

#### Annotation

- ```@SpringBootApplication```
    - 스프링 부트의 자동 설정, 스프링 빈 읽기와 생성을 모두 자동으로 설정됩니다.
    - ```@SpringBootApplication```이 있는 위치부터 설정을 읽어가기 때문에 이 애노테이션을 포함한 클래스는 항상 프로젝트의 최상단에 위치해야만 합니다.
- ```@RestController```
    - 컨트롤러를 JSON으로 반환하는 컨트롤러를 만들어줍니다.
    - 클래스 내에 있는 모든 메소드에 ```@ResponseBody```를 얹어준 효과와 같습니다.
- ```@Autowired```
    - 스프링이 관리하는 빈을 주입 받습니다.
- ```@RequestParam```
    - 외부에서 API로 넘긴 파라미터를 가져오는 애노테이션
    - [사용 예시](https://github.com/96glory/glory-springboot-webservice/blob/master/src/main/java/me/glory/springboot/web/HelloController.java)
- Test
    - ```@WebMvcTest```
        - 여러 스프링 테스트 애노테이션 중, Web MVC에 집중할 수 있는 애노테이션
        - ```@Controller```, ```@ControllerAdvice``` 등을 사용할 수 있지만, ```@Service```, ```@Component```, ```@Repository``` 등을 사용할 수 없습니다.
        - JPA test가 불가능하므로 ```@SpringBootTest```와 RestTemplate로 대체할 수 있다.
    - ```@After```
        - Junit의 단위 테스트가 끝날 때마다 수행되는 세더르를 지정
        - 보통 배포 전 전체 테스트를 수행할 때 테스트 간 데이터 침범을 막기 위해 사용합니다.
- Lombok
    - ```@Getter```
        - 선언된 모든 필드의 get 메소드를 생성해 줍니다.
    - ```@RequiredArgsConstructor```
        - 선언된 모든 final 필드가 포함된 생성자를 생성해 줍니다.
        - final이 없는 필드는 생성자에 포함되지 않습니다.
    - ```@NoArgsConstructor```
        - 기본 생성자 자동 추가
        - ```public Posts() {}``` 와 같은 효과.
    - ```@Builder```
        - 해당 클래스의 빌더 패턴 클래스를 생성
        - 생성자 상단에 선언 시 생성자에 포함된 필드만 빌더에 포함
- JPA
    - ```@Entity```
        - 테이블과 링크될 클래스임을 나타냅니다.
        - 기본값으로 클래스의 카멜케이스 이름을 언더스코어 네이밍으로 테이블 이름을 매칭합니다.
    - ```@Id```
        - 해당 테이블의 Primary Key 필드를 나타냅니다.
    - ```@GeneratedValue```
        - Primary Key의 생성 규칙을 나타냅니다.
    - ```@Column```
        - 테이블의 칼럼을 나타내며, 굳이 선언하지 않더라도 해당 클래스의 필드는 모두 칼럼이 됩니다.

#### Gradle Dependency

- ```compile('org.springframework.boot:spring-boot-starter-web')```
- ```testCompile('org.springframework.boot:spring-boot-starter-test')```
- ```compile('org.projectlombok:lombok')```
    - Lombok을 사용하기 위한 의존성
- ```compile('org.springframework.boot:spring-boot-starter-data-jpa')```
    - 스프링 부트 용 Spirng Data Jpa 추상화 라이브러리
- ```compile('com.h2database:h2')```
    - h2 : 인메모리 관계형 데이터베이스
    - 메모리에서 실행되기 때문에 애플리케이션을 재시작할 때마다 초기화되는 점을 이용하여 테스트 용도로 많이 사용됩니다.
