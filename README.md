# Tarot Insight

타로 카드를 기반으로 한 **온라인 타로 상담 플랫폼**입니다.  
사용자는 타로 카드를 뽑아 리딩 결과를 확인할 수 있으며, 상담사를 선택해 **예약 상담 및 실시간 채팅 상담**을 진행할 수 있습니다.

또한 상담 기록과 타로 리딩 기록을 저장하여 **개인 히스토리를 관리**할 수 있습니다.

---

# 1. Project Overview

### 주요 목표

- 실시간 상담 시스템 구현
- 예약 시스템과 동시성 문제 해결
- Redis 기반 캐싱 및 세션 관리
- 확장 가능한 서비스 구조 설계

---

# 2. Tech Stack

## Backend
- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- QueryDSL

## Database
- MySQL
- Redis

## Infra
- AWS EC2
- AWS RDS
- AWS S3
- Docker

## Realtime
- WebSocket (STOMP)

## Documentation
- Swagger (OpenAPI)

---

# 3. System Architecture

```
Client (React / Next.js)
        ↓
Spring Boot API Server
        ↓
Redis (Cache / Session / PubSub)
        ↓
MySQL (RDS)
        ↓
AWS S3 (Image Storage)
```

---

# 4. Core Features

## 4.1 User System

- 회원가입
- 로그인 (JWT 인증)
- 사용자 프로필 관리

---

## 4.2 Tarot Card Reading

- 랜덤 카드 뽑기
- 카드 의미 조회
- 리딩 결과 저장
- 타로 히스토리 조회

---

## 4.3 Tarot Consultation

- 상담사 목록 조회
- 상담 예약
- 상담 취소
- 상담 기록 조회

---

## 4.4 Real-time Consultation

- WebSocket 기반 채팅 상담
- 상담 종료 후 채팅 기록 저장

---

## 4.5 Review System

- 상담 리뷰 작성
- 상담사 평점 관리

---

# 5. Database Design (ERD)

## User

```
user
----
id (PK)
email
password
nickname
role
created_at
updated_at
```

---

## Tarot Card

```
tarot_card
----------
id (PK)
name
description
image_url
```

78장의 타로 카드 정보를 저장합니다.

---

## Tarot Reading

```
tarot_reading
-------------
id (PK)
user_id (FK)
tarot_card_id (FK)
question
result_text
created_at
updated_at
```

사용자가 뽑은 타로 카드 기록을 저장합니다.

---

## Tarot Reader (상담사)

```
tarot_reader
------------
id (PK)
user_id (FK)
profile
experience_year
rating
created_at
```

---

## Consultation Reservation

```
consultation_reservation
------------------------
id (PK)
user_id (FK)
reader_id (FK)
reservation_time
status
created_at
```

### status

- RESERVED
- CANCELLED
- COMPLETED

---

## Chat Message

```
chat_message
------------
id (PK)
reservation_id (FK)
sender_id
message
created_at
```

---

## Review

```
review
------
id (PK)
reservation_id (FK)
user_id (FK)
reader_id (FK)
rating
comment
created_at
```

---

# 6. API Design

## Auth

회원가입

```
POST /api/auth/signup
```

로그인

```
POST /api/auth/login
```

---

## Tarot

랜덤 카드 뽑기

```
GET /api/tarot/random
```

타로 기록 저장

```
POST /api/tarot/reading
```

사용자 타로 기록 조회

```
GET /api/tarot/history
```

---

## Tarot Reader

상담사 목록 조회

```
GET /api/readers
```

상담사 상세 조회

```
GET /api/readers/{id}
```

---

## Reservation

상담 예약

```
POST /api/reservations
```

예약 취소

```
DELETE /api/reservations/{id}
```

예약 목록 조회

```
GET /api/reservations
```

---

## Chat

WebSocket Endpoint

```
/ws/chat
```

메시지 전송

```
/pub/chat
```

메시지 구독

```
/sub/chat/{reservationId}
```

---

# 7. Redis Usage

## Cache

- 상담사 목록 캐싱
- 인기 타로 카드 캐싱

## Pub/Sub

- WebSocket 메시지 브로드캐스트

---

# 8. Concurrency Handling

상담 예약 시 동일 시간 중복 예약 문제를 방지하기 위해 **Optimistic Lock**을 적용합니다.

```java
@Version
private Long version;
```

트랜잭션 처리를 통해 데이터 정합성을 유지합니다.

---

# 9. Deployment

AWS 기반 배포 구조

```
EC2
 └ Spring Boot

RDS
 └ MySQL

Redis
 └ ElastiCache
```

Docker 기반 컨테이너 배포를 적용합니다.

---

# 10. Future Improvements

- 상담 알림 시스템
- 상담 일정 캘린더
- 타로 카드 추천 시스템
- 상담 통계 대시보드

---

# 11. Key Points

이 프로젝트를 통해 다음 기술을 경험하고 설명할 수 있습니다.

- WebSocket 기반 실시간 채팅
- 예약 시스템 동시성 처리
- Redis 캐싱
- JPA 기반 데이터 설계
- AWS 기반 서비스 배포