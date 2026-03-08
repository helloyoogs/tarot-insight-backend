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
is_active
rating
created_at
updated_at
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
version
created_at
updated_at
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

--------- 2026.03.08 업데이트 내역

# 🔮 Tarot Insight (타로 인사이트)

타로 카드를 기반으로 한 **온라인 타로 상담 및 실시간 리딩 플랫폼**입니다.  
사용자는 랜덤으로 타로 카드를 뽑아 결과를 확인할 수 있으며, 전문 상담사와 **실시간 채팅 상담** 및 **예약 시스템**을 통해 깊이 있는 상담을 경험할 수 있습니다.

---

## 🛠️ Tech Stack

### Backend
- **Core:** Java 17, Spring Boot 3.x
- **Security:** Spring Security, JWT (Stateless)
- **Data:** Spring Data JPA, MySQL, QueryDSL
- **Real-time:** WebSocket, STOMP, SockJS

---

## 🚀 Core Features

### 1. User & Security
- **JWT 기반 인증:** 로그인/회원가입 및 권한별 접근 제어 (User, Reader).
- **프로필 관리:** 유저 및 상담사의 상세 프로필 관리.

### 2. Tarot Reading
- **랜덤 카드 알고리즘:** 78장의 카드 중 무작위 리딩 기능.
- **히스토리 관리:** 본인이 뽑았던 카드와 리딩 내용을 영구 저장 및 조회.

### 3. Reservation System
- **실시간 예약:** 상담사별 가능 시간대에 맞춘 예약 신청.
- **동시성 처리:** `@Version`을 활용한 낙관적 락(Optimistic Lock) 적용으로 중복 예약 방지.
- **목록 조회:** 유저별 예약 내역 및 상담사별 상담 스케줄 관리.

### 4. Real-time Chatting (WebSocket)
- **STOMP 기반 채팅:** 예약된 시간에 맞춘 실시간 양방향 상담.
- **메시지 영속화:** 상담 도중 오가는 메시지를 DB에 저장하여 상담 종료 후에도 복기 가능.
- **SockJS 지원:** 웹소켓 미지원 브라우저 환경에서도 안정적인 연결 보장.

### 5. Review & Rating
- **상담 종료 프로세스:** 리뷰 작성 시 예약 상태가 자동으로 `COMPLETED`로 전환.
- **평점 자동 집계:** 리뷰 등록 시 상담사의 평균 평점이 실시간으로 업데이트 및 프로필 반영.

---

## 🔌 API & System Architecture

### WebSocket Endpoint
- **Connection:** `/ws-tarot`
- **Pub (메시지 전송):** `/pub/chat/message`
- **Sub (메시지 수신):** `/sub/chat/room/{reservationId}`

### Core API (Major)
- `GET /api/reservations/my` : 내 예약 목록 확인
- `POST /api/reviews` : 상담 리뷰 등록 및 평점 반영
- `GET /api/readers` : 활성화된 상담사 목록 및 실시간 평점 조회

---

## 📈 Future Improvements (향후 과제)
- **Redis Pub/Sub:** 다중 서버 환경을 위한 채팅 브로커 확장.
- **Notification System:** 예약 시간 10분 전 알림 기능 (SSE 또는 FCM).
- **S3 Image Upload:** 상담사 프로필 및 타로 카드 고화질 이미지 스토리지 연동.
- **Statistic Dashboard:** 월간 상담 횟수 및 인기 상담사 랭킹 시스템.