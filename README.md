# glory-springboot-webservice
> [도서](https://book.naver.com/bookdb/book_detail.nhn?bid=15871738)

### 사용 스택

- Java & JUnit4
- Spring & Spring Boot
- Spring Web MVC
- Spring JPA
- Spring Security with Google OAuth2
- Lombok
- Mustache
- AWS EC2
- AWS RDS with MariaDB

### 요구사항

- 게시판 기능
    - 게시판 조회
    - 게시글 등록
    - 게시글 수정
    - 게시글 삭제
- 회원 기능
    - 구글 소셜 로그인
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
- 기존 테스트에 시큐리티 적용으로 문제될 수 있음.
    - 첫 번째 요인 : 구글 key를 작성한 ```application-oauth.properties```는 test로 넘어가지 못함
        - sol : 가짜 설정값을 등록합니다.
    - 두 번째 요인 : 302 Status Code
        - 원인 : 스프링 시큐리티 설정 때문에 인증되지 않은 사용자의 요청은 이동시키기 때문입니다.
        - sol : 임의로 인증된 사용자를 추가하여 API만 테스트해 볼 수 있게 한다.
    - 세 번째 요인 : ```@WebMvcTest```에서 ```CustomOAuth2UserService```를 찾을 수 없음
        - 원인 : ```@WebMvcTest```에서 ```CustomOAuth2UserService```를 스캔하지 않기 때문입니다.
        - sol : 스캔 대상에서 ```SecurityConfig```를 제거합니다.

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
    - 영속성 컨텍스트
        - 엔티티를 영구 저장하는 환경
        - JPA의 핵심 내용은 엔티티가 영속성 컨텍스트에 포함되어 있냐 아니냐로 갈립니다.
        - JPA의 기본 설정으로 값으로 엔티티 매니저가 활성화된 상태가 됩니다. 트랜잭션 중 DB에서 데이터를 가져오면 이 데이터는 영속성 컨텍스트가 유지된 상태입니다. 이 상태에서 해당 데이터의 값을 변경하면 트랜잭션이 끝나는 시점에 해당 테이블에 변경분을 반영합니다. __즉, 엔티티의 값만 변경하면 별도로 업데이트 쿼리를 날릴 필요가 없다는 것입니다.__ (Dirty Checking)

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

#### 서버 템플릿 엔진과 머스태치

- 템플릿 엔진
    - 지정된 템플릿 양식과 데이터가 합쳐져 HTML 문서를 출력하는 소프트웨어
    - 종류
        - 서버 템플릿 엔진
            - 서버에서 DB 혹은 API에서 가져온 데이터를 미리 정의된 템플릿에 넣고, HTML 문서로 그려서 클라이언트에 전달해주는 역할을 한다.
            - JavaScript 코드가 실행되는 장소는 브라우저다.
            - Thymeleaf, JSP, Freemarker, ... 
        - 클라이언트 템플릿 엔진
            - 서버는 JSON 혹은 XML 형식의 데이터만 브라우저에 전달하고, 브라우저에서 화면을 생성함.
            - Mustache, React, Vue, ...
- Mustache
    - [자세한 정보](http://mustache.github.io/)
    - intellij에서 mustache 플러그인 설치하고, ```compile('org.springframework.boot:spring-boot-starter-mustache')``` 의존성 추가하기
    - 문법
        - ```{{>layout/header}}```
            - ```{{>}}```는 현재 머스테치 파일을 기준으로 다른 파일을 가져옵니다.
        - ```{{#posts}}```
            - posts라는 list를 순회합니다. java의 for 문과 비슷하다고 생각하면 됩니다.
        - ```{{id}}```
            - list에서 뽑아낸 객체의 필드를 사용합니다.
        -```{{#userName}}``` ~ ```{{/userName}}```
            - 머스테치는 다른 언어와 같은 if문을 제공하지 않습니다. true/false 여부만 판단할 뿐입니다.
            - 그러므로, 머스테치에서는 항상 최종값을 넘겨주어야 합니다.
            - userName이 존재한다면, ```#``` 부터 ```/``` 안에 있는 HTML 코드를 사용자에게 보여줍니다.
        - ```{{^userName}}``` ~ ```{{/userName}}```
            - 머스테치에서 해당 값이 존재하지 않는 경우에는 ```^```를 사용합니다.
            - - userName이 없다면, ```#``` 부터 ```/``` 안에 있는 HTML 코드를 사용자에게 보여줍니다.
        
#### Spring Security

- Spring Security
    - 막강한 인증과 인가 기능을 가진 프레임워크
    - 스프링 애플리케이션에서는 보안을 위한 표준
- 소셜 로그인 기능을 사용하지 않으면 구현해야 하는 것
    - 로그인 시 보안
    - 회원가입 시 이메일 혹은 전화번호 인증
    - 비밀번호 찾기, 비밀번호 변경, 회원정보 변경
    - 소셜 로그인으로 웹 애플리케이션을 개발하면 위 로그인 구현 사항을 구글, 네이버 등에 맡기면 되므로 서비스 개발에 집중할 수 있음.
- 소셜 로그인
    - [구글 소셜 로그인 추가](https://console.cloud.google.com/)
    - 스프링 부트 2 버전 시큐리티에서는 기본적으로 ```{도메인}/login/oauth2/code/{소셜서비스코드}```로 리다이렉트 URL을 지원하고 있습니다.
        - 개발자가 별도로 위 도메인을 위한 컨트롤러를 만들 필요는 없다.
    - ```application-{소셜서비스코드}.properties```에 client ID, secret code, scope를 넣는다. ```{소셜서비스코드}```라는 이름의 profile이 생성되어 스프링 부트에서 이 profile을 접근할 수 있게 된다.
        - github에 push할 때 개인정보가 올라가지 않도록 gitignore를 선언하자!
    - ```HttpSecurity http```
        - [코드의 주석 참고](https://github.com/96glory/glory-springboot-webservice/blob/558c1974a2/src/main/java/me/glory/springboot/config/auth/SecurityConfig.java)

#### AWS Cloud Service

- 24시간 작동하는 서버
    - 집에 PC를 24시간 구동시킨다.
    - 호스팅 서비스를 이용한다.
    - 클라우드 서비스를 이용한다. <- AWS
- AWS EC2
    - AWS에서 제공하는 성능, 용량 등을 유동적으로 사용할 수 있는 서버입니다.
    - 리전 확인 - 인스턴스 생성 - AMI 선택 - 인스턴스 유형 선택 - 인스턴스 세부 정보 구성 - 스토리지 추가 - 태그 추가 - pem 키 다운로드
    - 인스턴스를 중단하고 다시 시작할 때도 새 IP가 할당되기 때문에, 귀찮은 일을 줄이기 위해 고정 IP인 Elastic IP를 사용하면 된다. (탄력적 IP, EIP) 인스턴스와 주소 연결 해주면 사용할 수 있다.
    - putty나 xshell을 통해 내가 만든 인스턴스로 접속이 가능하다.
        - HostName : ec2-user@EIP
        - Port : 22
        - Connection Type : SSH
        - Auth : ```.ppk``` file
    - 필수로 해야할 일들
        - Java 8 install
            - ```sudo yum install -y java-1.8.0-openjdk-devel.x86_64```
            - ```sudo /usr/sbin/alternatives --config java``` & java version을 1.8.0 으로 바꾸기
            - ```sudo yum remove jre-1.7.0-openjdk```
            - ```java -version```
        - timezone 변경
            - ```sudo rm /etc/localtime```
            - ```sudo ln -s /usr/share/zoneinfo/Asia/Seoul /etc/localtime```
            - ```date```
        - hostname 변경
            - ```sudo vim /etc/sysconfig/network``` & HOSTNAME 부분을 내가 원하는 서비스명으로 바꾸기
            - ```sudo reboot```
            - ```sudo vim /etc/hosts``` & ```127.0.0.1 (내가 지정한 HOSTNAME)```을 추가한다.
            - ```curl (내가 지정한 HOSTNAME)``` -> 잘 등록되었다면 80포트로 접근이 안된다는 에러가 발생한다.
    - EC2에 프로젝트 Clone 받기
        - ```sudo yum install git``` & ```git --version```
        - ```git clone 깃헙주소``` or ```git pull```
        - ```chmod +x ./gredlew``` & ```./gradlew test``` : 프로젝트의 테스트 코드를 모두 수행한다.
- AWS RDS
    - AWS에서 지원하는 클라우드 기반 관계형 데이터베이스
    - RDS 인스턴스 생성
        - MariaDB 사용 : 저렴한 가격, 서비스가 커질 때 Amazon Aurora로의 교체 용이성
    - 필수로 해야할 일들
        - 파라미터 그룹 탭 - 파라미터 그룹 생성 - 파라미터 그룹 편집(아래 세 가지) - 인스턴스의 DB 파라미터 그룹 교체 - DB 재부팅
            - 타임존 설정 : ```time_zone = Asia/Seoul```
            - Character Set : ```{character-set-client, character-set-connection, character-set-database, character-set-filesystem, character-set-results, character-set-server} = utf8mb4```, ```{collation_connection, collation_server} = utf8mb4_general_ci```
            - Max Connection : ```max_connections = 60```
    - IntelliJ의 database plugin을 통해 RDS에 접속할 수 있다.
        - HostName : 인스턴스의 엔드포인트
    - EC2에서 RDS에서 접근 확인
        - EC2에 MySQL를 설치 : ```sudo yum install mysql```
        - EC2에서 내 RDS로 접속 : ```mysql -u 계정 -p h Host주소```
    - 스프링 부트 프로젝트로 RDS 접근하기
        - 진행해야할 작업
            - 테이블 생성 : H2는 테이블이 자동 생성되었지만, MariaDB에선 직접 쿼리를 이용해 생성해 주어야 합니다.
                - JPA가 사용할 테이블은 테스트 코드 수행 시 로그로 생성되는 쿼리를 이용하면 됩니다.
            - 프로젝트 설정 : 자바 프로젝트가 MariaDB에 접근하려면 DB driver가 필요합니다.
                - ```build.gradle```에 ```compile("org.mariadb.jdbc:mariadb-java-client")``` 추가
                - ```application-real.properties```를 생성하고, RDS 환경 profile를 추가한다.
                    ```properties
                    spring.profiles.include=oauth,real-db
                    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
                    spring.session.store-type=jdbc
                    ```
            - EC2 설정  : EC2 서버 내부에서 RDS 접속 정보를 관리하도록 설정합니다.
                - EC2 서버에 ```application-real-db.properties```를 생성하고, 아래 내용을 추가한 뒤 ```deploy.sh```에 real profile을 쓸 수 있도록 개선한다.
                    ```properties
                    spring.jpa.hibernate.ddl-auto=none
                    spring.datasource.url=jdbc:mariadb://rds주소:포트명(기본은 3306)/데이터베이스이름
                    spring.datasource.username=db계정
                    spring.datasource.password=db계정비밀번호
                    spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
                    ```

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
- ```@PathVariable```
    - 쿼리 파라미터의 값을 함수 파라미터로 가져옵니다.
- ```@EntityListeners(AuditingEntityListener.class)```
    - 클래스에 Auditing 기능을 포함시킵니다.
- Test
    - ```@WebMvcTest```
        - 여러 스프링 테스트 애노테이션 중, Web MVC에 집중할 수 있는 애노테이션
        - ```@Controller```, ```@ControllerAdvice``` 등을 사용할 수 있지만, ```@Service```, ```@Component```, ```@Repository``` 등을 사용할 수 없습니다.
        - JPA test가 불가능하므로 ```@SpringBootTest```와 RestTemplate로 대체할 수 있다.
    - ```@Before```
        - 매번 테스트가 시작되기 전에 수행되는 행위를 지정
    - ```@After```
        - Junit의 단위 테스트가 끝날 때마다 수행되는 행위를 지정
        - 보통 배포 전 전체 테스트를 수행할 때 테스트 간 데이터 침범을 막기 위해 사용합니다.
    - ```@WithMockUser(roles="USER")```
        - 인증된 가짜 사용자를 만들어서 사용합니다.
        - 이 어노테이션으로 인해 ROLE_USER 권한을 가진 사용자가 API를 요청하는 것과 동일한 효과를 가지게 됩니다.
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
    - ```@MappedSuperClass```
        - JPA Entity 클래스들이 ```@MappedSuperClass```가 선언된 클래스를 상속할 경우, ```@```가 선언된 클래스 내의 필드들도 Entity 클래스의 칼럼으로 인식하도록 합니다.
    - ```@Enumerated(EnumType.STRING)```
        - JPA로 DB에 저장할 때 Enum 값을 어떤 형태로 저장할지를 결정합니다.
        - 기본적으로 integer로 저장되나, Enum이 String일 경우 integer로 보면 무슨 뜻인지 모르기 때문에, String으로 바꿀 수 있습니다.
- Security
    - ```@EnableWebSecurity```
        - Spring Security 설정들을 활성화시켜 줍니다.
- make new Annotation
    - ```@Target(ElementType.PARAMETER)```
        - 이 어노테이션이 생성될 수 있는 위치를 지정합니다.
        - PARAMETER로 지정하게 되면 메소드의 파라미터로 선언된 객체에서만 사용할 수 있습니다.
    - ```@interface```
        - 이 파일을 어노테이션 클래스로 지정합니다.
    - ```@Retention(RetentionPolicy.RUNTIME)```
        - 어떤 시점까지 어노테이션이 영향을 미치는지 결정합니다.
        - RUNTIME으로 지정하게 되면 위 어노테이션은 RUNTIME까지 영향을 미치게 됩니다.


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
- ```compile('org.springframework.boot:spring-boot-starter-mustache')```
    - 머스테치를 편하게 사용할 수 있게 함.
- ```compile('org.springframework.boot:spring-boot-starter-oauth2-client')```
    - 소셜 로그인 등 클라이언트 입장에서 소셜 기능 구현 시 필요한 의존성
    
#### ETC

##### CRUD

- 생성 - create - POST
- 읽기 - read - GET
- 수정 - update - PUT
- 삭제 - delete - DELETE

##### 세션 저장소

- 톰캣 세션을 사용한다.
    - 두 대 이상의 WAS가 구동되는 환경에서는 톰캣들 간의 세션 공유를 위한 추가 설정이 필요합니다.
- MySQL과 같은 데이터베이스를 세션 저장소로 사용한다.
    - 여러 WAS 간의 공용 세션을 사용할 수 있는 가장 쉬운 방법입니다.
    - 로그인 요청마다 DB IO가 발생하여 성능상 이슈가 발생할 수 있습니다.
    - 보통 로그인 요청이 많이 없는 백오피스, 사내 시스템 용도에서 사용합니다.
- Redis, Memcached와 같은 메모리 DB를 세션 저장소로 사용한다.
    - 실제 서비스에서 주로 사용하는 방식입니다.