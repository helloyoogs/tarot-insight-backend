# 🔮 Tarot Insight (타로 인사이트)

> **"분산 환경의 실시간 통신, 고정밀 동시성 제어, 그리고 완벽한 데이터 정합성을 보장하는 타로 상담 플랫폼"**

**Tarot Insight**는 사용자와 타로 상담사를 실시간으로 연결하는 전문 상담 플랫폼입니다. 최신 **Spring Boot 4.0** 환경을 기반으로 하며, **Redisson 분산 락**, **Redis 캐싱**, 그리고 **JWT Refresh Token Rotation & Blacklist** 보안 체계를 결합하여 대규모 트래픽에서도 안정적이고 안전한 서비스를 제공합니다.

---

## 1. 🛠 핵심 기술적 성취 (Technical Focus)

본 프로젝트는 백엔드 설계의 핵심인 **실시간성, 확장성, 정합성, 그리고 보안**을 해결하는 데 집중했습니다.

* **고가용성 동시성 제어 (Redisson):** Redis 기반 분산 락을 구현하여 1:1 상담 예약의 중복 발생을 원천 차단. **100인 멀티쓰레드 테스트**를 통해 정합성 검증 완료.
* **지능형 동적 검색 (QueryDSL):** 복잡한 상담사 필터링 기능을 `BooleanExpression` 조각으로 구현하여 유연성 극대화 및 **Fetch Join을 통한 N+1 문제 원천 차단.**
* **성능 및 데이터 정합성 보장 (Cache-Aside & Evict):** 상담사 목록에 Redis 캐시를 적용하여 응답 속도를 극대화하고, 리뷰 작성 시 `@CacheEvict`를 통해 캐시와 DB 간의 정합성 유지.
* **보안 고도화 (Refresh Token Rotation):** Access Token의 짧은 수명을 보완하기 위해 RTR(Rotation) 전략 도입. 재발급 시마다 Refresh Token을 갱신하여 토큰 탈취 위험 방어.
* **Stateless 보안 강화 (JWT Logout Blacklist):** 로그아웃된 토큰을 Redis 블랙리스트에 저장하여 즉각적인 접근 차단 구현.

---

## 2. 💻 Tech Stack

### Backend
* **Core:** Java 17, **Spring Boot 4.0.3**
* **Concurrency & Cache:** **Redisson (Distributed Lock)**, **Spring Cache (Redis)**
* **Data:** Spring Data JPA, **QueryDSL 6.9 (Custom Repository Pattern)**, MySQL 8.0
* **Security:** Spring Security, **JWT (Access/Refresh with Rotation)**, BCrypt, Redis Blacklist
* **Docs:** Springdoc OpenAPI 3.0.2 (Swagger UI)

---

## 3. 🏗 System Architecture

```mermaid
graph TD
    Client[Client] -->|Auth / HTTP| Server[Spring Boot API Server]
    Server -->|Blacklist / Rotation / Lock / Cache / Search| Redis[(Redis Memory)]
    Server -->|Auditing / Save| MySQL[(MySQL RDS)]
    Redis -->|Pub/Sub| Server
    Server -->|Real-time| Client
```

---

## 4. 🚀 Core Features & Implementation

### 4.1 Redis 기반 JWT 보안 시스템
* **Refresh Token Rotation:** 로그인 시 Access/Refresh 토큰 동시 발급 및 Redis 저장. 재발급 요청 시 Redis 토큰 검증 후 두 토큰을 모두 갱신하여 보안성 강화.

### 4.2 Redisson 분산 예약 및 리뷰 캐싱
* **Distributed Lock:** Facade 패턴을 활용하여 트랜잭션과 락의 생명주기를 분리. **Redisson Watchdog**을 활용해 작업 완료 시까지 락 유지 보장.

### 4.3 QueryDSL 기반 지능형 필터링
* **Custom Repository:** 인터페이스와 구현체(`Impl`)를 분리하는 정석 패턴 적용.
* **Dynamic Query:** `nickname`, `minExperience`, `minRating` 등 선택적 파라미터를 조합한 동적 쿼리 수행.
* **Performance:** 유저 엔티티와의 일대일 관계를 `fetchJoin`으로 처리하여 조회 성능 최적화.

---

## 5. 🚨 Troubleshooting (문제 해결 경험)

### 5.1 분산 락 환경에서의 트랜잭션 커밋 타이밍 이슈
* **Issue:** 100인 동시성 테스트 시 간헐적으로 중복 예약이 발생하는 현상 확인.
* **Solution:** 락 해제 전 DB 반영을 보장하기 위해 서비스 레이어에서 `flush()` 호출 및 Redisson Watchdog 활성화로 정합성 100% 확보.

### 5.2 QueryDSL QClass 관리 및 인식 문제
* **Issue:** 빌드 환경에 따른 자동 생성 클래스(QClass) 충돌 및 인텔리제이 미인식.
* **Solution:** `build.gradle`에 `generatedSourceOutputDirectory` 명시적 설정 및 `.gitignore`에 `src/main/generated/` 추가하여 클린 코드 관리.

### 5.3 Spring Boot 4.0 & Swagger Jackson 버전 충돌
* **Issue:** Jackson 3.x 간의 클래스 로딩 충돌 및 보안 취약점 보고.
* **Solution:** `constraints` 설정을 통해 Jackson 버전을 안전한 범위로 고정하고 `springdoc-openapi` 마이그레이션 완료.

---

## 6. 🗄 Database Design

* **공통 사항**: 모든 엔티티는 `BaseTimeEntity`를 상속받아 `created_at`, `updated_at` 관리
* **`users`**: 사용자 정보 및 권한(UserRole) 관리
* **`tarot_readers`**: 상담사 프로필 및 실시간 평점 관리
* **`consultation_reservation`**: 예약 상태 및 분산 락 관리

---
*Last Updated: 2026.03.10*