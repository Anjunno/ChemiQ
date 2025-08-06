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
