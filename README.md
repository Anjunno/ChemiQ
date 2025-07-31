## 🗓️ 개발 일지
### 2025-07-31

## 📌 프로젝트 개요
IoT 기반 감정 공유 무드등 프로젝트 (Emolink)

## 🚀 진행 내역
- Spring Boot 초기 세팅
- Member 테이블 설계 및 JPA 기반 회원가입 로직 구현
- Spring Security 설정 (기본 로그인 페이지 및 CSRF 비활성화)

## 📝 개발 회고
- Spring Security 설정 없이 실행했더니 로그인 페이지가 떠서 당황했지만, `SecurityConfig`로 해결
- 엔티티에 기본 생성자가 없어 오류가 발생했는데, JPA의 요구 사항이라는 점을 배움
- Postman으로 API 테스트하면서 JSON 구조와 DTO 연동 개념이 더 명확해짐
