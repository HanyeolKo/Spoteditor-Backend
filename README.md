# SpotEditor Backend
Google Map의 평점 시스템과 유사한 지역 기반 핫플레이스 추천 서비스 플랫폼의 백엔드 API 서버입니다.

## 주요 기능
- Oauth2.0 기반 회원가입/로그인 (JWT 인증)
- 사용자 팔로우 및 게시글 좋아요 알림
- 인기 게시물 상위노출
- 지역 기반 검색

## 기술 스택
- Java 17, Spring Boot 3.x, Spring Security, JPA, QueryDSL, Gradle, Spring Scheduler
- MySQL, Redis, Redisson, flyway
- Docker, Docker Compose
- Swagger, Spring Rest Docs
- AWS S3, code deploy, GitHub, AWS CLI(On-Premise Server)

## 프로젝트 패키지 구조
- config : 프로젝트 전역 설정 관련 패키지
- global : 프로젝트 전역 공통 모듈 관련 패키지
- infra : 프로젝트 전역 인프라 관련 패키지
- modules : 도매인 패키지

## 인프라 아키텍처 구성

### 전체 개요

SpotEditor 백엔드는 **온프레미스 환경의 Docker 기반 서버**에서 구동되며, 프론트엔드는 **Vercel**에서 별도로 배포됩니다.  
이 시스템은 리버스 프록시, 로드 밸런싱, 캐싱 클러스터, 자동화된 CI/CD 파이프라인 등을 포함합니다.

---

### 요청 흐름

1. **클라이언트 요청**
   - 사용자의 HTTP/HTTPS 요청은 **Nginx 리버스 프록시** 서버를 거칩니다.
   - **Rate Limit** 설정으로 과도한 요청을 방지합니다.

2. **프론트엔드**
   - 프론트는 **Vercel**을 통해 정적으로 배포되어 사용자에게 직접 제공됩니다.

3. **백엔드 서버**
   - **Spring Boot 기반 모놀리식 백엔드 서버 2개**를 Docker로 컨테이너화하여 운용
   - **Nginx를 통한 Round Robin 방식의 로드 밸런싱**으로 트래픽을 분산 처리
   - 전체 백엔드 환경은 **Docker Compose**로 구성 및 관리

4. **Redis 클러스터**
   - 고가용성을 위한 **Redis Cluster (3 Master + 3 Slave)** 구조 운영
   - 세션 관리, 캐시 처리 및 작업 큐 등에 활용

5. ##TLS 인증서**
   - SSL/TLS 인증서는 Let's Encrypt를 통해 1개월에 한번씩 자동갱신

---

### CI/CD 파이프라인

- **CI :**
  - GitHub Actions를 활용해 커밋 시 테스트 및 빌드 자동 수행

- **CD :**
  - 빌드 결과물을 **AWS S3에 업로드**
  - 이후 **CodeDeploy 트리거**가 활성화되어
  - **온프레미스 환경의 CodeDeploy Agent**가 자동으로 배포 수행
  - 최종 배포는 **Docker Compose** 환경을 통해 컨테이너 기반으로 적용

---

### 요약

- **수평 확장 가능성 확보**: 2개 백엔드 서버 + 라운드 로빈 로드밸런싱  
- **과도한 요청 제어**: Nginx + Rate Limit  
- **배포 자동화**: GitHub → S3 → CodeDeploy → 온프레미스  
- **고가용성 캐시 시스템**: Redis 클러스터  
- **모듈화된 관리**: Docker Compose로 전체 환경 통합 관리
