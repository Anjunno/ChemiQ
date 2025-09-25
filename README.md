<p align="center">
  <img src="/chemiQQ.png" width="280"/>
</p>

<h1 align="center">ChemiQ(케미퀘스트)</h1>

## 하루의 미션, 우리의 케미
**ChemiQ**는 하루하루 주어지는 랜덤 미션을 통해 **파트너와 함께 즐기며 케미를 쌓는 1:1 미션 공유 앱**입니다.  
사진과 짧은 텍스트로 미션을 수행하고, 상대방의 결과물에 점수와 코멘트를 남기며 **관계를 게임처럼 재미있게 강화**할 수 있습니다.  

하루하루의 작은 도전이 쌓여, 파트너와의 특별한 순간과 추억을 기록하게 됩니다.

---
##  기술 스택

| 기술 | 사용한 기술 |
|------|------------|
| 📱 프론트엔드 & 모바일 | ![Flutter](https://img.shields.io/badge/Flutter-02569B?style=flat&logo=flutter&logoColor=white) ![Dart](https://img.shields.io/badge/Dart-0175C2?style=flat&logo=dart&logoColor=white) ![Material Design](https://img.shields.io/badge/Material%20Design-757575?style=flat&logo=materialdesign&logoColor=white) |
| ☕ 백엔드 | ![Spring Boot](https://img.shields.io/badge/SpringBoot-6DB33F?style=flat&logo=springboot&logoColor=white) ![Java](https://img.shields.io/badge/Java-007396?style=flat&logo=openjdk&logoColor=white) ![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=flat&logo=spring&logoColor=white) ![REST API](https://img.shields.io/badge/REST%20API-6DB33F?style=flat&logo=swagger&logoColor=white) ![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat&logo=swagger&logoColor=white) |
| 🗄 데이터베이스 | ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white) ![Amazon RDS](https://img.shields.io/badge/Amazon%20RDS-527FFF?style=flat&logo=amazonrds&logoColor=white) |
| ☁ 클라우드 & DevOps | ![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF9900?style=flat&logo=amazonec2&logoColor=white) ![AWS S3](https://img.shields.io/badge/AWS%20S3-569A31?style=flat&logo=amazons3&logoColor=white) ![AWS Lambda](https://img.shields.io/badge/AWS%20Lambda-FF9900?style=flat&logo=awslambda&logoColor=white) ![AWS Route53](https://img.shields.io/badge/AWS%20Route%2053-8C4FFF?style=flat&logo=amazonroute53&logoColor=white) |
| ⚙ CI/CD & 협업 | ![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=flat&logo=githubactions&logoColor=white) ![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat&logo=github&logoColor=white) |
| 🔐 인증 & 보안 | ![JWT](https://img.shields.io/badge/JWT-000000?style=flat&logo=jsonwebtokens&logoColor=white) ![HTTPS](https://img.shields.io/badge/HTTPS-0078D7?style=flat&logo=ssl&logoColor=white) ![Nginx](https://img.shields.io/badge/NGINX-009639?style=flat&logo=nginx&logoColor=white) ![TLS](https://img.shields.io/badge/TLS-000000?style=flat&logo=letsencrypt&logoColor=white) ![Certbot](https://img.shields.io/badge/Certbot-003A70?style=flat&logo=letsencrypt&logoColor=white) ![Let's Encrypt](https://img.shields.io/badge/Let's%20Encrypt-003A70?style=flat&logo=letsencrypt&logoColor=white) |
| 💻 운영체제 & 환경 | ![Ubuntu](https://img.shields.io/badge/Ubuntu-E95420?style=flat&logo=ubuntu&logoColor=white) |

---
<h1>시스템 아키텍처</h1>
<p align="center">
  <img src="/chemiq아키텍처.drawio.png" />
</p>

---

## 📘 개발 로그

<details>
<summary>🗓️ 2025-07-31 - 프로젝트 초기 설정</summary>

**📌 개발 일지**
- Spring Boot 초기 세팅  
- Member 테이블 설계 및 JPA 기반 회원가입 로직 구현  
- Spring Security 설정 (기본 로그인 페이지 및 CSRF 비활성화)

**📝 개발 회고**
- Spring Security 설정 없이 실행했더니 로그인 페이지가 떠서 당황했지만, `SecurityConfig`로 해결  
- 엔티티에 기본 생성자가 없어 오류가 발생했는데, JPA의 요구 사항이라는 점을 배움  
- Postman으로 API 테스트하면서 JSON 구조와 DTO 연동 개념이 더 명확해짐

</details>

<details>
<summary>🗓️ 2025-08-02 - Swagger 적용</summary>

**📌 개발 일지**
- Swagger3 설정 및 문서화 도입 (`springdoc-openapi` 적용)  
- `SwaggerConfig` 클래스 생성 및 API 문서 기본 설정 구성  
- 회원가입 API에 `@Operation`, `@ApiResponses` 등 어노테이션 적용  
- 회원가입 응답 메시지를 위한 `MemberSignUpResponse` DTO 생성 및 예시 작성  
- 요청 데이터 검증 및 문서화를 위해 `MemberSignUpRequest` DTO에 `@Schema` 어노테이션 추가 (필드별 설명 포함)

**📝 개발 회고**
- API 문서 관리를 위해 처음으로 Swagger를 도입해봄  
- 기존 문자열 응답을 DTO로 변경하면서 응답 구조의 일관성과 확장성에 대해 고민해보게 됨  
- Swagger 예시 작성이 생각보다 번거로웠지만, 한 번 정리해두면 문서 유지보수가 훨씬 쉬워질 것 같음

</details>

<details>
<summary>🗓️ 2025-08-06 - JWT 인증 구조 도입</summary>

**📌 개발 일지**
- Spring Security 기반 JWT 인증 구조 설정 시작  
- `SecurityConfig`에서 `AuthenticationManager` 및 커스텀 로그인 필터(`LoginFilter`) 등록  
- 사용자 인증을 위한 `CustomUserDetails`, `CustomUserDetailsService` 클래스 생성  
- `UsernamePasswordAuthenticationFilter`를 상속한 `LoginFilter`에서 `memberId`, `password` 기반 로그인 시도 처리 구현

**📝 개발 회고**
- Spring Boot로 JWT 인증을 구현하는 것은 처음이라 생소한 개념이 많았음  
- YouTube 강의를 참고해 따라 구현해보았지만 `AuthenticationManager`, `Filter`, `UserDetailsService`의 역할과 흐름이 아직 명확하게 잡히지 않음  
- 이해가 부족한 부분은 문서와 샘플 프로젝트를 통해 더 공부하고 흐름을 정리해볼 계획

</details>

<details>
<summary>🗓️ 2025-08-09 - JWT 토큰 발급 로직 구현</summary>

**📌 개발 일지**
- JWT 유틸리티 클래스(`JWTUtil`) 구현: 토큰 생성, Claim 파싱, 만료 검증 기능 포함  
- 로그인 성공 시 JWT 토큰을 생성하여 응답 헤더에 추가하는 로직 구현 (`LoginFilter`의 `successfulAuthentication` 오버라이드)  
- 사용자 인증에 성공하면 `memberId`, `role` 정보를 담은 JWT를 `Authorization: Bearer <token>` 형식으로 응답  
- `CustomUserDetails`에서 사용자 정보를 추출하고, SecurityContext에서 권한 확인 가능하도록 처리  

**📝 개발 회고**
- 이전 25.08.06 개발 당시에 이해하기 어려웠던 Spring Security의 인증 처리 흐름을 다시 확인해보며 전보다 해당 흐름을 이해할 수 있도록 노력해보았음.  
- 특히 아래와 같은 순서로 인증이 이루어짐을 정리하며 구조를 잘 잡을 수 있었음:

  1. 클라이언트가 `/login`으로 `POST` 요청을 보냄  
  2. `LoginFilter`가 요청을 가로채고, `attemptAuthentication()`에서 `memberId`와 `password`를 추출  
  3. `AuthenticationManager`가 `CustomUserDetailsService`의 `loadUserByUsername()` 호출  
  4. 해당 메서드에서 DB 조회 후 `CustomUserDetails` 객체 반환  
  5. Security 내부적으로 아이디와 비밀번호를 비교 (`UsernamePasswordAuthenticationToken`과 `UserDetails` 기반)  
  6. 인증 성공 시 `successfulAuthentication()` 실행 → JWT 토큰 생성 및 응답 헤더에 삽입  
  7. 인증 실패 시 `unsuccessfulAuthentication()` 호출

- Spring Security의 흐름이 처음엔 복잡하게 느껴졌지만, 이전보다는 나아진 것 같음.
- POSTMAN을 통해 테스트 로그인 시에 응답코드(200)과 함께 응답 헤더에 JWT 토큰이 정상적으로 포함되어 있는 것을 확인했음.
- 앞으로는 발급된 토큰을 활용해 인가(Authorization) 처리 및 Refresh Token 전략 구현까지 이어갈 예정

</details>

<details>
<summary>🗓️ 2025-08-10 - JWT 토큰 검증 필터 구현</summary>

**📌 개발 일지**
- JWT 토큰 검증을 위한 `JWTFilter` 클래스 작성 
- `OncePerRequestFilter`를 상속받아 모든 요청에 대해 JWT 토큰 유효성 검사 수행 (/login, /signup 제외) 
- 토큰이 없거나 만료된 경우 필터 체인을 통해 다음 요청으로 정상 진행하도록 예외 처리
- 유효한 토큰에서 사용자 정보 추출 후 `SecurityContextHolder`에 인증 정보 세팅

**📝 개발 회고**
- `SecurityContextHolder`에 인증 정보를 설정하는 과정과 임의로 생성한 사용자 객체의 역할이 혼란스러웠음
- 처음에는 `Member` 객체에 임의 데이터를 넣는 것이 잘못된 것 같아 의문이 들었으나, 스프링 시큐리티의 `SecurityContextHolder` 역할과 JWT 정보 기반 다른 자료들을 통해 학습하며 점차 이해하게 됨
- 특히, JWT를 통해 사용자 식별과 권한 정보만 있으면 매 요청마다 DB에서 사용자 전체 정보를 가져올 필요 없이 인증 상태를 유지할 수 있음을 알게 됨
- 아직 JWT 토큰 만료 후 갱신 처리 등 보완할 부분이 많아 추가 학습과 구현이 필요함
- Postman을 통해 토큰이 없는 요청과 있는 요청을 테스트를 해보며 잘 동작함을 확인했음 
</details>


<details>
<summary>🗓️ 2025-08-18 - JWT Refresh Token 발급 및 재발급 API 구현</summary>

**📌 개발 일지**
- 로그인 성공 시 Access Token과 함께 긴 만료 시간을 가진 Refresh Token을 발급하도록 `LoginFilter` 수정
- JWT Payload에 `category` 클레임을 추가하여 토큰의 종류('access', 'refresh')를 명확히 구분
- `POST /reissue` 엔드포인트를 통해 Refresh Token으로 새로운 Access Token을 발급하는 `ReissueController` 작성
- 재발급 로직에서 발생 가능한 역할 정보 추출 버그를 수정하고, 만료된 토큰에 대한 예외 처리 로직 추가
- Refresh Token을 DB에 저장하는 로직을 `MemberService`에 추가하고 `LoginFilter`에 주입을 시도하던 중 순환 참조 문제 발견

**📝 개발 회고**

- Refresh Token을 도입하여 사용자가 매번 재로그인해야 하는 불편함을 개선하는 첫 단계를 성공적으로 구현했음. `category` 클레임을 활용하여 토큰의 역할을 명시적으로 구분하는 방식이 각 로직에서 토큰을 검증할 때 매우 유용하다는 것을 느낌.
- Refresh Token의 상태 관리를 위해 DB 저장 로직을 `MemberService`에 구현하고, 이를 `LoginFilter`에 주입하는 과정에서 `SecurityConfig`와의 순환 참조(Circular Dependency) 문제를 발견함.
- 처음에는 문제의 원인을 파악하기 어려웠고, Spring Bean의 생명주기와 의존성 주입(DI)에 대한 더 깊은 이해가 필요함을 느낌. 단순히 설정을 변경하여 문제를 회피하기보다는, 근본적인 원인을 이해하고 올바른 설계 방법을 학습하기 위해 관련 내용을 더 깊이 알아보기로 결정함.
- 따라서 현재 커밋은 순환 참조 해결 이전, Refresh Token의 발급과 재발급 기능의 핵심 로직이 구현된 상태임. 다음 단계로 순환 참조 문제에 대해 학습하고 올바른 해결책을 적용할 예정.
- Postman을 통해 로그인 시 두 종류의 토큰이 정상적으로 발급되고, `/reissue` API가 유효한 Refresh Token에 대해 새로운 Access Token을 발급하는 것을 확인하며 기능의 기본 골격은 완성했음.

</details>


<details>
<summary>🗓️ 2025-08-24 - Refresh Token DB 저장 및 순환(Rotation) 전략 구현</summary>

**📌 개발 일지**
- `ReissueController`의 비즈니스 로직을 `ReissueService`로 분리하여 역할과 책임을 명확히 함.
- (순환 참조 해결) 기존 `Member` 테이블에 있던 `refreshToken` 필드를 제거하고, `RefreshToken` 엔티티를 새로 생성하여 테이블을 분리함
- Refresh Token을 DB에 저장하기 위한 `RefreshToken` 엔티티 및 `RefreshTokenRepository` 구현.
- 로그인 성공 시, 발급된 `Refresh Token`을 DB에 저장하여 서버가 각 세션을 관리할 수 있는 기반 마련.
- 보안 강화를 위해 토큰 재발급 시 기존 Refresh Token을 무효화하고 새로운 토큰을 발급하는 `토큰 순환(Rotation)` 전략 적용.

**📝 개발 회고**
- `ReissueController`의 로직을 `ReissueService`로 분리하니, 코드가 간결한 구조가 되었음.
-`Refresh Token`을 DB에 저장하고 `토큰 순환(Rotation)` 전략을 사용함으로써 발급했던 모든 Refresh Token를 기억한 뒤, Refresh Token을 1번만 사용할 수 있게 하여 보안성을 강화하였음.
- 여러가지 로직을 구현하다보니 점점 복잡해지는 것 같다. 내가 작성한 코드의 흐름을 한번 더 확인하고 명확히 파악해야겠음.
- `POSTMAN`으로 로그인 시 DB에 `Refresh Token`이 잘 저장됨을 확인했고, 토큰 재발급시에 Access, Refresh Token이 올바르게 갱신됨을 확인하였다. 또한, `Refresh Token`이 DB에 갱신됨도 확인함.


</details>

<details>
<summary>🗓️ 2025-08-25 - JWT 로그아웃 구현 및 API DTO 리팩토링</summary>

**📌 개발 일지**
- JWT Refresh Token 기반의 로그아웃 기능 구현 (`CustomLogoutFilter` 및 `RefreshService` 사용).
- `/signup`, `/reissue` API의 요청/응답을 `Map`에서 전용 DTO(`MemberSignUpRequest/Response`, `ReissueRequest/Response` 등)로 리팩토링.
- 일관된 에러 처리를 위해 `ErrorResponse` DTO를 도입하고, 컨트롤러의 예외 처리 로직에 적용.
- Swagger(OpenAPI)를 사용하여 `/signup`, `/reissue` 엔드포인트에 대한 API 명세를 문서화.

**📝 개발 회고**
- 처음에는 요청과 응답에 DTO를 사용하는 것이 단순히 코드를 늘리는 번거로운 작업이라 생각했음. 특히 서비스 계층과 컨트롤러 계층에서 각각 어떤 DTO를 사용해야 할지 구분하는 것이 혼란스러웠으나, 각 계층의 역할에 맞는 DTO를 설계하고 나니 오히려 코드의 책임이 명확해지고 타입 안정성이 높아져 유지보수가 훨씬 쉬워진다는 것을 깨달음.
- 로그아웃 기능을 구현하면서, `Filter`에서 직접 DB에 접근하는 대신 `Service` 계층으로 로직을 위임하고 트랜잭션을 관리하는 것의 중요성을 다시 한번 느낌.
- 오늘 구현한 로그아웃 기능과 DTO로 리팩토링된 API들을 Postman으로 직접 테스트함. 특히 로그아웃 요청 시 DB에서 Refresh Token이 정상적으로 삭제되는 것을 확인했고, `/reissue` API가 새로운 Access Token을 헤더에, 새로운 Refresh Token을 Body에 정확히 담아 반환하는 것을 보며 구현에 대한 확신을 얻었음.
- 여러 기능을 한 번에 개발하고 리팩토링하면서 코드의 전체적인 구조와 흐름을 놓치지 않는 것이 중요하다는 것을 느낌. DTO 도입과 같은 리팩토링이 당장은 번거로워도, 장기적으로는 시스템의 안정성과 예측 가능성을 크게 높여준다는 것을 체감한 하루였음.

</details>

<details>
<summary>🗓️ 2025-08-29 - 필터 기반 인증 API Swagger 문서화</summary>

**📌 개발 일지**
- Spring Security Filter로 처리되어 Swagger UI에 자동으로 명세되지 않는 `/login`, `/logout` 엔드포인트를 문서화함.
- 실제 동작 로직은 없지만 Swagger 어노테이션을 작성하기 위한 용도의 '가짜(Dummy) 컨트롤러'인 `AuthControllerDoc`를 생성.

**📝 개발 회고**
- API를 개발하면서 Postman으로는 테스트가 가능했지만, 프론트엔드 개발자나 다른 협업자가 API 명세를 한눈에 파악하기 어렵다는 문제를 느낌.
- 특히 `@RestController`에 정의되지 않은 `/login`, `/logout` 같은 필터 기반 엔드포인트는 Swagger가 자동으로 인식하지 못해 문서화 방법이 막막했음.
- 실제 로직은 없지만 Swagger가 스캔할 수 있는 '가짜 컨트롤러'를 만드는 방법을 학습하고 적용함. 이 방법을 통해 필터가 처리하는 API까지 명세에 포함시켜 API의 가시성과 사용성을 크게 높일 수 있었음.
- Swagger UI를 통해 `/login`이 `form-data`를, `/logout`이 `json`을 요청 Body로 사용하는 것을 명확히 표현할 수 있었고, 응답 상태와 헤더까지 상세히 기술하여 협업 효율을 높일 수 있는 기반을 마련함.

</details>

<details>
<summary>🗓️ 2025-09-01 - 사용자 기기 등록 API 구현 및 인증 로직 디버깅</summary>

**📌 개발 일지**
- 사용자의 무드등 기기 정보를 저장하기 위한 `Device` 엔티티를 생성하고, `Member` 엔티티와 1:1 연관관계를 설정함.
- 기기 등록 비즈니스 로직을 처리하는 `DeviceService`를 구현함. (UUID 발급, 중복 등록 방지, DB 저장)
- `POST /api/device/register` 엔드포인트를 `DeviceController`에 추가하고, `@AuthenticationPrincipal`을 통해 인증된 사용자 정보를 활용.
- RESTful 원칙에 따라, 리소스 생성 성공 시 `201 Created` 상태 코드와 `Location` 헤더를 포함하여 응답하도록 구현.
- 사용자가 이미 기기를 등록한 경우에 대한 예외 처리를 추가하고, `409 Conflict` 상태 코드를 반환하도록 함.
- Swagger를 사용하여 API 명세를 상세히 문서화하고, `@SecurityRequirement`를 통해 JWT 인증이 필요한 API임을 명시함.

**📝 개발 회고 및 트러블슈팅**
- **트러블슈팅: `@AuthenticationPrincipal`에서 `memberNo`가 `null`로 반환되는 문제 해결**
  - **문제점:** 기기 등록 API를 개발하던 중, `@AuthenticationPrincipal`로 주입받은 `CustomUserDetails` 객체에서 `getMemberNo()`를 호출했을 때 `null` 값이 반환되는 문제를 마주함.
  - **원인 분석:** 원인 분석 결과, 문제는 최초 로그인 시점이 아닌, 로그인 이후의 모든 요청을 처리하는 `JWTFilter`에 있었음. 필터가 Access Token을 파싱할 때 `memberId`와 `role`만 추출하고, 정작 `memberNo`는 추출하지 않은 채 임시 `Member` 객체를 생성하여 `CustomUserDetails`를 만들고 있었음. 이 때문에 `SecurityContext`에 저장되는 인증 객체에 `memberNo` 정보가 누락되었던 것.
  - **해결:** `JWTFilter` 로직을 수정하여 토큰에서 `memberNo` 클레임을 명시적으로 추출하고, 이 값을 포함하여 `CustomUserDetails` 객체를 생성하도록 변경하여 문제를 해결함.

- **개발 회고**
  - 이번 트러블슈팅을 통해, `UserDetailsService`가 처리하는 최초 인증 과정뿐만 아니라, `JWTFilter`에서 매 요청마다 인증 정보를 '재구성'하는 과정의 정확성이 매우 중요하다는 것을 깨달음. JWT에 담긴 정보가 `SecurityContext`까지 온전히 전달되는 흐름을 디버깅하며 이해할 수 있었던 좋은 기회였음.
  - Postman으로 테스트 시, 수정한 `JWTFilter` 덕분에 컨트롤러에서 `memberNo`가 정상적으로 조회되는 것을 확인하였고, 이를 바탕으로 기기 등록 로직을 성공적으로 완성할 수 있었음.

</details>

<details>
<summary>🗓️ 2025-09-02 - 파트너 요청, 해제 및 관계 관리 기능 구현</summary>

**📌 개발 일지**
- `Partnership` 엔티티와 `PartnershipStatus` Enum(PENDING, ACCEPTED 등)을 통해 사용자 간 파트너 관계를 모델링함.
- `PartnershipService`에 파트너 관계를 요청하는 `createRequest` 메소드를 구현.
  - 요청 시 발생 가능한 다양한 엣지 케이스(자기 자신에게 요청, 이미 파트너인 경우, 처리 대기중인 요청 존재)를 검증하여 데이터 정합성 확보.
  - 기존에 거절되거나 취소된 관계가 있을 경우, 새로운 데이터를 생성하는 대신 기존 데이터를 업데이트하도록 구현하여 효율성 증대.
- `PartnershipService`에 파트너 관계를 해제하는 `cancelPartnership` 메소드를 구현.
  - DB에서 `ACCEPTED` 상태의 관계를 찾아 `CANCELED`로 상태를 변경하는 방식으로 구현.
- `PartnershipRepository`에 JPQL을 이용한 커스텀 쿼리를 추가하여 복잡한 조건의 관계 조회 로직을 처리함.
- `PartnershipController`를 통해 해당 기능들을 API 엔드포인트로 노출하고, 각 예외 상황에 맞는 HTTP 상태 코드를 반환하도록 처리.

**📝 트러블슈팅**
- **문제점:** 파트너 관계를 확인하기 위해 `existsBy...` 쿼리를 여러 번 사용하여 DB에 반복적으로 접근하는 비효율적인 로직이 있었음.
- **해결:** 두 사용자 사이의 관계를 한 번에 조회하는 커스텀 JPQL 쿼리(`findPartnershipBetween`)를 `PartnershipRepository`에 작성하여 DB 접근을 최소화하고, 서비스 로직을 더 간결하게 리팩토링함.

**📝 개발 회고**
- 파트너 관계 설정 기능을 처음 구상할 때는 단순히 두 사용자를 연결하는 간단한 작업이라고 생각했습니다. 하지만 실제 구현에 들어가면서, '요청-수락'이라는 상태 변화와 '이미 관계가 존재할 때', '자기 자신에게 요청할 때' 등 고려해야 할 엣지 케이스가 많다는 것을 깨달았습니다. 이는 단순한 CRUD를 넘어, 사용자의 상호작용과 데이터의 '상태'를 함께 관리해야 하는 복잡한 비즈니스 로직임을 체감했습니다.
- 이러한 복잡한 규칙들을 `PartnershipService`에 집중적으로 구현하면서, 각 규칙이 올바른 예외와 에러 메시지로 응답하는 것을 확인할 때마다 코드에 대한 신뢰가 쌓이는 것을 느꼈습니다.
- 이번 기능 구현을 통해 RESTful API 설계뿐만 아니라, 상태를 가지는 비즈니스 로직을 어떻게 견고하게 만들지에 대해 깊이 고민해볼 수 있었습니다.

</details>

<details>
<summary>🗓️ 2025-09-04 - 파트너십 수락 기능 구현 및 요청 로직 리팩토링</summary>

**📌 개발 일지**
- **(신규)** `POST /partnership/{partnershipId}/accept` API와 `acceptPartnership` 서비스 로직을 구현하여 파트너 요청 수락 기능을 추가함.
- **(리팩토링)** 이전에 작성했던 파트너 요청(`createRequest`) 서비스의 DB 조회 로직을 단일 쿼리로 최적화하고, CANCELED/REJECTED 상태의 관계를 재사용하도록 개선함.
- **(공통)** 컨트롤러 계층에서 `EntityNotFoundException`, `AccessDeniedException`, `IllegalStateException` 등 각 예외 상황에 맞춰 404, 403, 409 상태 코드를 반환하도록 처리함.
- **(공통)** 파트너십 수락 API에 대한 상세한 Swagger 명세를 작성함.

**📝 트러블슈팅**
- **문제점:** '요청 수락' 로직 구현 시, 사용자가 수락 버튼을 누르는 짧은 순간에 요청자나 수락자가 다른 사람과 파트너가 될 수 있는 **경쟁 상태(Race Condition)**가 발생할 수 있음을 발견. 이 경우 데이터 정합성이 깨져 한 사람이 두 명 이상의 파트너를 갖는 심각한 오류로 이어질 수 있음.
- **해결:** `Partnership`의 상태를 `ACCEPTED`로 변경하는 최종 단계 직전에, 서비스 로직 내에서 요청자와 수신자 양쪽의 파트너 상태를 DB에서 **다시 한번 조회**하는 방어 코드를 추가함. 이를 통해 수락 행위의 원자성을 보장하고 데이터 정합성을 확보.

**📝 개발 회고**
- 오늘은 새로운 기능(수락)을 구현함과 동시에, 이전에 작성했던 코드(요청)를 리팩토링하는 작업을 병행했습니다. 기존 요청 생성 로직의 비효율적인 DB 조회를 개선하면서, 처음 코드를 작성할 때부터 성능을 고려하는 습관의 중요성을 느꼈습니다.
- 새로운 수락 기능을 개발하면서는 보안(수락 권한)과 데이터 정합성(경쟁 상태)이라는 두 가지 엣지 케이스를 중점적으로 고민했습니다. 단순히 기능 구현을 넘어, 발생 가능한예외 상황을 예측하고 방어하는 역할이 정말 어렵지만 필요함을 느꼈습니다.
- Swagger UI를 사용하여 수락 기능의 다양한 시나리오(정상 수락, 권한 없는 사용자의 수락 시도, 이미 파트너가 있는 경우)를 테스트하며 방어 로직이 견고하게 동작함을 확인함으로써 코드에 대한 확신을 얻을 수 있었습니다.

</details>

<details>
<summary>🗓️ 2025-09-05 - 파트너십 관리 기능 구현 (조회, 거절, 취소)</summary>

**📌 개발 일지**
- **파트너십 조회 기능 (3종)**
  - `GET /partnerships/requests/received`: 로그인한 사용자가 받은 `PENDING` 상태의 요청 목록을 조회하는 API를 구현함.
  - `GET /partnerships/requests/sent`: 로그인한 사용자가 보낸 요청들의 목록과 현재 상태(`PENDING`, `ACCEPTED` 등)를 조회하는 API를 구현함.
  - `GET /partnerships`: 현재 `ACCEPTED` 상태인 파트너의 정보를 조회하는 API를 구현함.
- **파트너십 요청 처리 기능 (2종)**
  - `DELETE /partnerships/requests/{partnershipId}/reject`: 받은 파트너 요청을 거절하는 기능을 구현.
  - `DELETE /partnerships/requests/{partnershipId}/cancel`: 내가 보낸 파트너 요청을 취소하는 기능을 구현.
- **공통 작업**
  - 각 기능에 필요한 서비스 로직(`PartnershipService`) 및 커스텀 Repository 쿼리를 작성함.
  - 기능별 요청/응답에 맞는 DTO를 설계하고 적용함.
  - 각 API의 성공 및 모든 예외 케이스(403, 404, 409 등)에 대한 컨트롤러 로직과 Swagger 문서를 상세히 작성함.

**📝 개발 회고 및 트러블슈팅**

- **트러블슈팅 : API URI 설계의 일관성 문제**
  - **문제점:** '요청 거절'과 '요청 취소'는 서버 내부 동작은 다르지만, 사용자 입장에서는 '요청을 없앤다'는 비슷한 맥락의 행위였음. 초기에는 이를 다른 HTTP 메소드나 경로로 설계할지 고민함.
  - **해결:** 사용자 경험의 일관성을 위해, 두 기능 모두 **`DELETE /partnerships/requests/{partnershipId}`** 라는 동일한 형태의 URI를 사용하기로 결정함. 대신 서비스 로직 내부에서 요청을 보낸 사람(`requester`)과 요청을 받은 사람(`addressee`)을 구분하여 각각 `CANCELED`와 `REJECTED` 상태로 처리하도록 구현하여 API의 일관성과 명확성을 모두 잡음.

- **개발 회고:**
  - 파트너십 기능은 단순한 CRUD를 넘어, `PENDING` -> `ACCEPTED` -> `CANCELED` 등으로 변화하는 '상태(State)'를 관리하는 것이 핵심임을 깨달음. 각 상태에서 가능한 행위와 불가능한 행위를 정의하고, 모든 엣지 케이스를 방어하는 것이 서비스의 안정성을 크게 높인다는 것을 체감함.
  - Swagger UI의 'Authorize' 기능을 적극적으로 활용하여, 수락/거절/취소 권한이 없는 사용자의 접근(`403`), 이미 처리된 요청에 대한 중복 처리(`409`) 등 다양한 시나리오를 직접 테스트하며 로직의 완성도를 높일 수 있었음.

</details>

<details>
<summary>🗓️ 2025-09-06 - 프로젝트 전환 및 미션 기능 기반 설계</summary>

**📌 개발 일지**
- **(프로젝트 전환)** 기존 IoT 기반 'EmoLink' 프로젝트를 하드웨어 제작의 현실적인 제약으로 인해, 순수 모바일 앱 서비스인 'ChemiQ' (케미퀘스트)로 전환함.
  - 프로젝트 이름 및 관련 패키지 구조(`com.emolink` -> `com.chemiq`)를 변경하고, 하드웨어와 관련된 `Device` 엔티티 및 관련 코드들을 모두 제거함.
  - 기존에 구현했던 JWT 인증 및 파트너십 관리 기능은 새로운 프로젝트의 핵심 기반으로 그대로 유지함.
- **(신규 기능)** ChemiQ의 핵심 기능인 미션 시스템의 데이터베이스 기반을 설계함.
  - `Mission`, `DailyMission`, `Submission`, `Evaluation` 4개의 신규 엔티티를 생성하여, 미션 할당부터 수행, 평가까지의 전체 데이터 흐름을 모델링함.
  - 각 신규 엔티티에 대한 `JpaRepository` 인터페이스를 생성하여 데이터 접근 계층을 구현함.
  - `mission` 테이블에 기능 테스트를 위한 샘플 데이터를 추가함.

**📝 개발 회고**
- 하드웨어 제작의 현실적인 제약 앞에서 프로젝트를 중단하는 대신, 지금까지 만든 백엔드 코드를 재활용하여 새로운 가치를 창출하는 'ChemiQ' 프로젝트로 전환하기로 결정했습니다.
- 'ChemiQ'의 핵심 기능을 구현하기 위해, 가장 먼저 데이터의 흐름을 고민하고 ERD를 설계해보고 네 개의 핵심 엔티티를 도출했습니다. 기능 구현에 앞서 데이터 모델링을 탄탄하게 하는 것이 중요한 부분이라고 느꼈습니다.
- 아직 서비스 로직은 없지만, ERD와 엔티티 코드를 통해 '미션 할당 -> 수행 -> 평가'로 이어지는 서비스의 전체적인 흐름이 어느정도 머릿속에 그려지는 것 같아 기대가 됩니다.

</details>

<details>
<summary>🗓️ 2025-09-07 - CI/CD 파이프라인 구축 및 미션 기능 구현</summary>

**📌 개발 일지**
- **(CI/CD)** AWS EC2 서버 자동 배포를 위한 CI/CD 파이프라인을 구축함.
  - GitHub Actions 워크플로우 파일(`.github/workflows/deploy.yml`)을 작성하여, `main` 브랜치 push 시 Gradle 빌드, 테스트, 애플리케이션 배포 및 재시작 과정이 자동으로 수행되도록 설정함.
- **(스케줄러)** Spring의 `@Scheduled`를 사용하여 매일 자정, 모든 `ACCEPTED` 상태의 파트너십에게 랜덤 미션을 자동으로 할당하는 스케줄러를 구현함.
  - `MissionRepository`에 `RAND()`를 이용한 네이티브 쿼리를 추가하여 랜덤 미션을 조회하는 로직을 작성.
- **(API)** `GET /missions/today` 엔드포인트를 통해 로그인한 사용자가 오늘 할당받은 미션을 조회하는 기능을 구현함.
  - 서비스 계층에서 파트너 관계 및 오늘 할당된 미션 존재 여부를 검증하고, 없을 경우 `404 Not Found`를 반환하도록 처리.
  - `TodayMissionResponse` DTO를 사용하여 API 응답 데이터를 명확하게 정의함.
  - 해당 API에 대한 상세한 Swagger 명세를 작성함.

**📝 개발 회고**
- 지금까지는 수동으로 `build`하고 `jar` 파일을 서버에 옮겨 실행하는 번거로운 배포 과정을 거쳤습니다. GitHub Actions으로 CI/CD를 구축하고 나니, `git push` 한 번으로 모든 과정이 자동으로 처리되어 개발 경험이 극적으로 향상되었습니다. 앞으로는 코드 작성이라는 본질에 더 집중할 수 있게 되어, 개발 속도가 크게 빨라질 것으로 기대됩니다.
- 스케줄러 기능을 구현하면서, 사용자의 직접적인 요청 없이도 서버가 능동적으로 비즈니스 로직을 수행하는 백그라운드 작업에 대해 학습할 수 있었습니다. 특히 DB에 미션이 없거나, 활성 파트너가 없는 엣지 케이스를 처리하며 더 안정적인 코드를 작성하는 방법을 고민할 수 있었습니다.
- Swagger UI를 통해 오늘의 미션 조회 API를 직접 테스트하였고, 스케줄러가 할당한 미션 데이터가 정상적으로 반환되는 것을 확인하며 백엔드의 핵심 기능이 하나씩 완성되어 가는 것에 큰 성취감을 느꼈습니다.

</details>

<details>
<summary>🗓️ 2025-09-08 - 서버 HTTPS 적용 및 S3 파일 업로드 기능 구현</summary>

**📌 개발 일지**
- **(인프라)**
  - EC2 인스턴스에 도메인을 연결하고, HTTPS/SSL 암호화 통신을 적용함.
  - 미션 이미지 저장을 위한 AWS S3 버킷을 생성하고, 보안을 위해 모든 퍼블릭 액세스를 차단하도록 정책을 설정함.
- **(미션 제출 기능)**
  - Pre-signed URL 방식을 이용한 미션 결과(이미지) 제출 기능을 구현함.
  - URL 발급 API (`POST /submissions/presigned-url`)와 제출 완료 보고 API (`POST /submissions`)로 로직을 분리하여, 서버 부하를 최소화하고 확장성을 확보함.
  - URL 발급 전, 서버에서 사용자의 미션 제출 자격을 미리 검증하여 불필요한 파일이 S3에 업로드되는 것을 방지함.
  - Spring Boot와 AWS S3 연동을 위한 `S3Config` 및 `S3Service`를 구현.

**📝 개발 회고 및 트러블슈팅**
- **트러블슈팅 1: Spring Boot와 AWS SDK의 `Region` 설정 오류**
  - **문제점:** `application.properties`에 리전(Region)을 명시했음에도, Spring Boot가 설정을 읽지 못해 `Unable to load region` 에러가 지속적으로 발생함.
  - **해결:** 원인은 Spring Cloud AWS의 자동 설정(`Auto-Configuration`)과의 충돌로 파악. `S3Config` 클래스에서 `S3Client`와 `S3Presigner` Bean을 **명시적으로 생성**하고, 메인 클래스에서 `S3AutoConfiguration`을 **`exclude`** 하여 문제를 해결. 이를 통해 Spring의 자동 설정 원리와 수동 설정으로 문제를 해결하는 방법을 학습함.

- **트러블슈팅 2: Pre-signed URL 방식의 보안 허점**
  - **문제점:** 초기 설계에서는 URL 발급 API에 별도의 검증 로직이 없어, 파트너가 없는 사용자도 악의적으로 S3에 파일을 업로드할 수 있는 보안 허점을 발견함.
  - **해결:** URL을 발급하기 전에, 서비스 계층에서 사용자의 파트너 관계, 미션 할당 여부 등을 **미리 검증**하도록 로직을 수정하여 해결. API의 동작 순서에 따른 허점을 예측하고 방어하는 것의 중요성을 깨달음.

- **개발 회고:**
  - 오늘은 IP 주소로만 접근하던 테스트 서버에 **실제 도메인과 HTTPS를 적용**하며, 개발 프로젝트가 '실제 서비스'로 한 단계 나아가는 과정을 경험했습니다. 브라우저에 자물쇠 아이콘이 뜨는 것을 보며 뿌듯함을 느꼈습니다.
  - 백엔드 개발은 단순히 코드 작성에서 끝나는 것이 아니라, 코드가 동작할 서버 환경을 구성하고 보안을 책임지는 과정까지 포함한다는 것을 깊이 이해하게 되었습니다.
  - Swagger와 Postman을 이용해 2단계로 이루어진 복잡한 API 흐름을 직접 테스트하고, EC2에 배포된 서버에서 정상적으로 S3 연동이 동작하는 것을 확인하며 큰 성취감을 느꼈습니다.

</details>

<details>
<summary>🗓️ 2025-09-09 - 핵심 기능 구현 완료 (타임라인, 평가, 스트릭 시스템)</summary>

**📌 개발 일지**
- **(기능) 공유 타임라인 조회 API 구현 (`GET /timeline`)**
  - 파트너와 함께한 모든 미션 기록을 최신순으로 조회하는 기능을 페이징(Paging)을 적용하여 구현.
  - '하루치 미션'을 하나의 단위로 묶어, 사용자와 파트너의 제출물을 각각 포함하는 `DailyMissionResponseDto` 형태로 응답하도록 설계.
- **(기능) 오늘의 미션 현황 조회 API 구현 (`GET /timeline/today`)**
  - 앱 메인 화면을 위해, 오늘 할당된 미션과 제출 현황을 한번에 조회하는 기능을 구현.
- **(기능) 평가 및 스트릭/케미 지수 시스템 구현**
  - 파트너의 미션 제출물에 점수와 코멘트를 남기는 '평가' 기능(`POST /submissions/{id}/evaluations`)을 구현.
  - 두 파트너가 서로 평가까지 모두 완료하면, `Partnership`의 `streakCount`가 1 증가하고 `chemiScore`가 업데이트되도록 구현.
  - 매일 자정 스케줄러가 전날 미션 미완료 시 `streakCount`를 0으로 초기화하고 점수 패널티를 부여하는 로직을 추가.
- **(리팩토링)**
  - `Partnership` 수락 로직에 경쟁 상태(Race Condition) 방어 로직 및 다른 요청 자동 정리 기능을 추가하여 데이터 정합성을 강화.
  - 모든 컨트롤러의 `try-catch` 블록을 제거하고, `@RestControllerAdvice`를 이용한 전역 예외 처리기로 코드를 중앙화하고 간결하게 개선.
  - JPA 엔티티 모델의 제약조건을 보완하고, 비즈니스 메소드를 추가하여 객체지향적으로 개선.
  - 타임라인 조회 시 발생할 수 있는 N+1 쿼리 문제를 `JOIN FETCH`를 사용하여 해결하고 성능을 최적화.

**📝 개발 회고 및 트러블슈팅**
- **트러블슈팅: 스트릭(Streak) 업데이트 시 NullPointerException 발생**
  - **문제점:** 미션 완료 후 `partnership.increaseStreak()` 메소드(`streakCount++`) 호출 시 `NullPointerException`이 발생.
  - **원인 분석:** 기존 `Partnership` 엔티티에 `Integer streakCount` 필드를 새로 추가하고 애플리케이션을 재시작하자, JPA가 `partnership` 테이블에 `streak_count` 컬럼을 추가했지만, **기존에 이미 존재하던 데이터**들의 이 새 컬럼 값은 **`NULL`**로 채워졌습니다. 이 `null` 값을 가진 `streakCount` 필드에 `++` 연산을 시도하자 `NullPointerException`이 발생했습니다.
  - **해결:**
    1.  **기존 데이터 수정 (DB):** `UPDATE partnership SET streak_count = 0 WHERE streak_count IS NULL;` SQL을 실행하여, 이미 `NULL`로 들어가 있는 기존 데이터들을 `0`으로 업데이트하여 문제를 즉시 해결했습니다.
    2.  **향후 데이터 방지 (Java Entity):** `Partnership` 엔티티의 `streakCount` 필드에 **`@Builder.Default`** 어노테이션과 함께 **초기값 `0`을 명시**했습니다. 이를 통해 앞으로 새로 생성되는 모든 `Partnership` 객체는 `streakCount` 값이 `null`이 아닌 `0`으로 시작하도록 보장하여, 같은 문제가 재발하는 것을 원천적으로 방지했습니다.
- 
- **개발 회고:**
  - 이번 기능들을 구현하며 '상태(State)'가 변화하고 여러 비즈니스 규칙이 얽혀있는 복잡한 워크플로우를 설계하는 경험을 했습니다.
  - 다양한 엣지 케이스를 고려한 서비스 로직이 Swagger를 통한 테스트에서 예상대로 동작하는 것을 확인하며 뿌듯함을 느꼈습니다.

</details>

<details>
<summary>🗓️ 2025-09-10 - 마이페이지 및 프로필 관리 기능 구현</summary>

**📌 개발 일지**
- **(마이페이지 조회)**
  - `GET /members/me/info` API를 구현하여, 로그인된 사용자의 정보, 파트너 정보, 파트너십 정보(스트릭, 케미 지수)를 한 번에 제공하는 '화면 맞춤형' API를 설계함.
  - 파트너가 없는 경우에도 안전하게 응답할 수 있도록 서비스 로직을 구현.
  - `Partnership` 엔티티에 `acceptedAt` 필드를 추가하여, 파트너 관계가 수락된 날짜를 명확하게 기록하고 조회할 수 있도록 개선.
- **(프로필 사진 관리)**
  - S3 Pre-signed URL을 이용한 2단계 업로드 방식으로 프로필 사진을 등록/변경하는 `POST /members/me/profile-image/...` API 2종을 구현.
  - 사진 변경 시, S3에 남아있는 기존 이미지를 삭제하여 불필요한 스토리지 사용을 방지하는 로직을 추가.
- **(프로필 정보 수정)**
  - `PATCH /members/me/nickname` API를 구현하여, DTO와 `@Valid`를 통해 유효성을 검증하며 닉네임을 변경하는 기능을 추가.
  - `PATCH /members/me/password` API를 구현하여, `bCryptPasswordEncoder`를 이용한 현재 비밀번호 확인 등 안전한 비밀번호 변경 기능을 추가.
- **(공통)**
  - 모든 신규 API에 대해 상세한 Swagger 명세를 작성하고, 전역 예외 처리기(`@RestControllerAdvice`)를 통해 에러를 처리하도록 구성.

**📝 개발 회고 및 트러블슈팅**
- **트러블슈팅: 마이페이지 조회 시 `NullPointerException` 발생 문제**
  - **문제점:** 마이페이지 정보 조회 시, 파트너가 없는 사용자의 경우 `partnership` 객체가 `null`이 되어, 이 `null` 객체를 DTO 생성자에 전달하면서 `NullPointerException`이 발생.
  - **해결:** 서비스 로직에서 `Optional<Partnership>`을 사용하여 파트너십 존재 여부를 확인하고, `if (partnershipOpt.isPresent())` 분기문을 통해 파트너가 있는 경우와 없는 경우에 각각 다른 DTO를 생성하도록 명확하게 로직을 분리하여 문제를 해결.

- **개발 회고:**
  - 이번 기능 구현을 통해, 특정 화면(마이페이지)을 위한 데이터를 여러 번의 API 호출로 가져오는 대신, **하나의 '화면 맞춤형' API로 묶어 제공**하는 것이 클라이언트의 부담을 줄이고 성능을 향상시키는 좋은 방법임을 배움.
  - 사용자 프로필 이미지 기능을 구현하며, 서버를 경유하지 않고 클라이언트가 S3에 직접 파일을 업로드하는 **Pre-signed URL 방식의 효율성을 체감**할 수 있었음.

</details>

<details>
<summary>🗓️ 2025-09-11 ~ 2025-09-17 - 미션 기능 고도화 및 도전과제 시스템 기반 구축</summary>

**📌 개발 일지**
- **(미션/타임라인 기능 고도화)**
  - `GET /missions/weekly-status`: 주간 미션 현황(성공/실패/진행중) 조회 API를 구현함.
  - `GET /missions/today`: 오늘의 미션 조회 시, 자정 이후에 파트너가 된 사용자를 위해 미션이 없으면 즉시 자동 생성하는 'Get-or-Create' 로직을 적용하여 안정성을 높임.
  - `GET /submissions/{id}/evaluation`: 특정 제출물에 대한 파트너의 평가 내용을 상세 조회하는 API를 구현함.
- **(게임화 시스템)**
  - `EvaluationService`와 `MissionScheduler`에 미션 완료/실패 여부에 따라 `Partnership`의 스트릭과 케미 지수가 증감/초기화되는 로직을 구현함.
  - 도전과제 시스템의 기반을 설계하고 `Achievement`, `MemberAchievement` 엔티티 및 리포지토리를 생성함.
  - `SubmissionService`에 Spring의 `ApplicationEventPublisher`를 이용한 이벤트 기반 아키텍처를 도입하여, 향후 도전과제, 알림 등 여러 기능으로 확장할 수 있는 구조를 마련함.
  - `GET /members/me/info` (마이페이지) API 응답에 달성한 도전과제 목록을 포함하도록 기능을 확장함.

**📝 개발 회고**
- 이번 주에는 파트너십과 미션의 핵심 로직을 바탕으로, 사용자의 재미와 몰입도를 높이는 시스템(도전과제, 스트릭 관리)의 기반을 다졌습니다. 특히, 기능 간의 결합도를 낮추기 위해 Spring의 이벤트 기반 아키텍처를 처음으로 도입해보았고, 'Get-or-Create' 패턴을 적용하여 엣지 케이스를 처리하는 등 코드의 안정성을 높이는 데 집중했습니다.

</details>

<details>
<summary>🗓️ 2025-09-18 - 인프라 개선(RDS 이전) 및 이미지 처리 파이프라인 구축</summary>

**📌 개발 일지**
- **(데이터베이스 마이그레이션)**
  - EC2 인스턴스에서 직접 운영하던 MySQL 데이터베이스를 **AWS RDS로 성공적으로 이전**하여, 데이터베이스의 안정성, 확장성, 관리 용이성을 확보함.
  - `mysqldump`로 기존 데이터를 백업하고, RDS 보안 그룹 및 Spring Boot의 `application-prod.properties` 설정을 변경하여 마이그레이션을 완료.
- **(이미지 처리 파이프라인)**
  - S3 Pre-signed URL을 통해 HEIC 파일 업로드 시, S3 이벤트를 트리거로 **AWS Lambda 함수를 실행하여 JPG로 자동 변환**하는 파이프라인을 구축함.
  - 변환 완료 후 Lambda가 DB의 파일 키를 안전하게 업데이트할 수 있도록, 비밀 키로 인증하는 **내부용 API(`PUT /api/internal/...`)**를 구현함.

**📝 개발 회고 및 트러블슈팅**
- **트러블슈팅: iOS-Android 간 이미지 포맷 비호환 문제**
  - **문제점:** iOS의 기본 카메라 포맷인 HEIC(.heic) 파일은 일부 구형/보급형 안드로이드 기기에서 보이지 않는 심각한 호환성 문제가 있음을 발견. 이 문제를 해결하지 않으면, iOS 사용자가 올린 사진을 안드로이드 파트너가 볼 수 없는 치명적인 사용자 경험 저하가 발생.
  - **원인 분석:** 문제의 원인을 클라이언트(앱) 레벨에서 해결하기보다, 서버에서 모든 이미지를 보편적인 포맷(JPG)으로 통일시켜주는 것이 더 안정적이고 확장성 있다고 판단. 클라이언트는 어떤 포맷이든 올리기만 하면 되고, 서버가 호환성을 책임지는 구조를 선택.
  - **해결:** **AWS Lambda와 S3 이벤트 트리거**를 사용하여 서버리스 이미지 처리 파이프라인을 구축. 1) S3에 `.heic` 파일이 업로드되면, 2) Lambda 함수가 자동으로 실행되어 `Pillow` 라이브러리를 통해 JPG로 변환하고, 3) 변환된 JPG 파일을 다시 S3에 저장한 뒤, 4) EC2 서버의 내부 API를 호출하여 DB에 저장된 파일 키를 `.jpg`로 업데이트하도록 구현. 이 과정을 통해 서버 부하 없이 확장 가능한 이미지 호환성 문제를 해결함.

**📝 개발 회고 및 트러블슈팅**
- **트러블슈팅: iOS-Android 간 이미지 포맷 비호환 및 처리 파이프라인 구축**
  - **문제점:** 초기 설계 검토 중, iOS의 기본 카메라 포맷인 HEIC(.heic) 파일이 일부 안드로이드 기기에서 표시되지 않는 심각한 호환성 문제가 있음을 발견. 이 문제를 해결하지 않으면, 플랫폼이 다른 파트너 간에 이미지 공유가 불가능한 치명적인 오류가 발생할 수 있었습니다.
  - **해결 과정:** 단순히 클라이언트(앱)에서 변환하는 방식보다, 서버 측에서 모든 이미지를 보편적인 포맷(JPG)으로 통일하는 것이 장기적으로 더 안정적이고 확장성 있다고 판단. 이를 위해 AWS의 서버리스 기술을 도입하여 아래와 같은 이미지 처리 파이프라인을 구축하여 해결했습니다.
    1.  **S3 이벤트 트리거:** S3 버킷의 특정 경로(`submissions/`)에 `.heic` 또는 `.heif` 파일이 업로드되면 이벤트가 발생하도록 설정.
    2.  **AWS Lambda 함수 구현:** 해당 이벤트를 받아 실행되는 Python 기반 Lambda 함수를 작성. 이 함수는 이미지 처리 라이브러리(`Pillow`, `pillow-heif`)를 사용하여 HEIC 파일을 JPG로 변환하고, 변환된 파일을 다시 S3에 업로드한 뒤 원본은 삭제함.
    3.  **내부 API 연동:** Lambda 함수가 변환 완료 후, EC2 서버의 내부용 API(`PUT /api/internal/...`)를 호출하여 `Submission` 테이블에 저장된 파일 키를 새로운 `.jpg` 키로 안전하게 업데이트하도록 설계하여 데이터 정합성을 유지.
  - **추가 트러블슈팅:** Lambda Layer 의존성 패키징 과정에서, 로컬 개발 환경(macOS/Windows)과 Lambda 실행 환경(Amazon Linux)의 CPU 아키텍처 및 Python 버전 차이로 인해 C언어 기반 라이브러리가 동작하지 않는 문제를 겪음. **ec2를 통해 Lambda와 동일한 환경에서 라이브러리를 빌드**하여 Layer를 생성함으로써 이 호환성 문제를 해결.

- **개발 회고:**
  - 오늘은 백엔드 개발자의 역할이 단순히 API를 만드는 것을 넘어, 안정적인 데이터 관리(RDS), 다른 클라우드 서비스와의 연계(Lambda), 그리고 플랫폼 간의 차이점(HEIC)까지 고려하는 아키텍트의 역할도 중요함을 깊이 체감한 하루였습니다.
  - 특히 Lambda의 실행 환경(OS, CPU, Python 버전) 차이로 인해 라이브러리 호환성 문제를 겪고, EC2를 이용해 빌드 환경을 맞춰 해결하는 과정을 통해 클라우드 네이티브 개발 환경에 대한 이해도를 높일 수 있었습니다.
  - ChemiQ를 개발하면서 부족한 점들이 많이 보였고, 이를 해결하기 위해 RDS, Lambda와 같은 새로운 서비스들을 접하면서 많은 시간이 걸리고 여러 시행착오를 겪어야 했습니다. 솔직히 많이 힘든 시간이었지만, 문제를 해결해내고 시스템이 동작하는 것을 보며 많은 공부가 되고 큰 성취감을 느낄 수 있었습니다.
  - 지금까지 Spring 백엔드와 Flutter 앱 개발을 병행해왔지만, 이제는 **Flutter 앱(ChemiQ)의 완성 및 배포**를 최우선 목표로 삼으려고 합니다. 앞으로는 앱 개발을 중심으로 진행하면서, 백엔드에서는 앱 개발에 꼭 필요하다고 생각되는 부분들을 보완해나갈 예정입니다.

</details>