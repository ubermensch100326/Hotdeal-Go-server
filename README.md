> # Hotdeal-Go-server

## 수정사항

### 2023. 12. 16.

- 테스트용으로 액세스 토큰 유효기간 1분, 리프레시 토큰 유효기간 5분으로 수정
- 토큰 네이밍 컨벤션 kebap case에서 camel case로 수정 : access-token -> accessToken, refresh-token -> refreshToken
- 로그인하면 사용자 정보 중 닉네임도 반환 : nickname, accessToken, refreshToken, memberId이 반환
  ```
  {
  "nickname": "김싸피",
  "accessToken": "eyJ0eXAiOi ...",
  "refreshToken": "eyJ0eXAiOi ...",
  "memberId": "23hotdealgo"
  }
  ```
- 토큰 관련 모든 요청에 Bearer 인증 타입 검증 추가 (스웨거에서도 Authorize 누르고 토큰 앞에 Bearer을 붙여서 "Bearer eyJ0eXAiOi ..."와 같이 요청)
  ```
  curl -X GET "http://13.125.124.61:80/member/token" -H "accept: */*" -H "Authorization: Bearer eyJ0eXAiOi ..."
  ```
- 리프레시 토큰을 이용한 액세스 토큰 재발급 추가 : "http://13.125.124.61:80/member/token"에 GET 요청을 하면 액세스 토큰과 리프레시 토큰이 반환 (리프레시 토큰은 요청할 때 전달한 리프레시 토큰과 동일)
  ```
  {
  "accessToken": "eyJ0eXAiOi ...",
  "refreshToken": "eyJ0eXAiOi ...",
  }
  ```
- 리프레시 토큰을 액세스 토큰으로 사용할 수 있는 문제와 액세스 토큰을 리프레시 토큰으로 사용할 수 있었던 문제 수정
- 로그아웃, 회원탈퇴 기능 추가

## 수정계획

- 공지사항 CRUD 추가
- 관리자 권한 추가
- 유효성 검증 (@Valid, BindingResult 등 이용)
- 비밀번호 암호화 알고리즘 추가
- 소셜 회원들을 사이트 자체 회원으로 통합시켜야 한다면 어떻게 할 수 있을지 생각
- 관심물품 등록되면 알림
- 특정 사이트 크롤링 불가 관련 처리
- @RequestHeader 사용법 숙지 및 적용
- 네이버로 계속 기능 추가
- DB 모델링
- REST하게 URL 수정
