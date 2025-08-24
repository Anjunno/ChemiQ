##  프로젝트 소개 - Emolink

Emolink는 **IoT 기반 감정 공유 무드등 프로젝트 서비스**로,  
빛과 진동을 통해 감정을 직관적으로 표현하고 공유합니다.  
서로의 마음을 시공간의 제약 없이 연결하며,  
감정을 시각과 촉각으로 전하는 새로운 소통 방식을 제시합니다.

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