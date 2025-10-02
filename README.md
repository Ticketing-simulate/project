# 🎟 Ticketing Project
프로젝트 진행 기간: 25.09.24 ~ 25.10.02

## 📌 프로젝트 개요
**공연/콘서트 티켓 예매 시스템**을 구현한다.

- 인기 콘서트 예매 시스템 구현
- 1인 1매, 고정 좌석 구조
- 순간적으로 많은 결제 요청 상황을 상정하여 **동시성 이슈** 에대해 탐색.

---

## ⚙️ 기술 스택
- **Language** : Java  
- **Database** : Supabase (PostgreSQL 기반)  
- **ORM / Persistence** : JPA / Hibernate  
- **Build Tool** : Gradle or Maven  
- **Framework** : Spring Boot  

---

## 🎯 주요 기능
- ✅ **회원 관리** : 회원 가입, 로그인, JWT 인증
- ✅ **콘서트 관리** : 공연(Concert) 등록 / 조회 / 삭제  
- ✅ **좌석 관리** : 공연별 **고정 좌석** 생성, 좌석 상태(AVAILABLE/PENDING/SOLD) 관리  
- ✅ **예매(결제) 기능** :  
  - 사용자 1인당 1매 제한  
  - 좌석 선점 시 동시성 제어 (REDIS 분산락, 비관적 락 활용)  
- ✅ **인기순위** : @Cacheable 이용하여 콘서트 검색 및 Top 5 랭크 인기 콘서트 출력

---

## 🗂 ERD 구조
https://www.erdcloud.com/d/riGmpkkkMc4KNNRKC

---

## 🗂 API 명세서
https://www.notion.so/teamsparta/2692dc3ef5148197a2f3c6466a507186?v=2692dc3ef51481909486000cbeddefe5&source=copy_link

---


