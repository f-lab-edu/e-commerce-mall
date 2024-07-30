# E-Commerce Mall

![License](https://img.shields.io/github/license/f-lab-edu/e-commerce-mall)
![Contributors](https://img.shields.io/github/contributors/f-lab-edu/e-commerce-mall)
![Last Commit](https://img.shields.io/github/last-commit/f-lab-edu/e-commerce-mall)

## 소개

E-Commerce Mall은 Coupang을 모티브로 한 고성능 전자 상거래 서비스 API입니다.<br>
이 프로젝트는 대용량 데이터를 효율적으로 처리하고 온라인 마켓플레이스를 위한 필수 기능을 제공합니다.

## 프로젝트 목표
- **객체지향 원리 적용**: 객체지향 원리를 고려하여, 유지보수성과 확장성이 높은 코드를 작성합니다.
- **단위 테스트**: 철저한 단위 테스트로 코드의 신뢰성을 확보합니다.
- **성능 최적화**: 성능 테스트를 통해 시스템을 최적화하고 대용량 트래픽을 효과적으로 처리합니다.
- **CI/CD 자동화**: CI/CD를 통해 자동화된 빌드, 테스트, 배포 프로세스를 구현합니다.

## 주요 기능

- **사용자 인증**: 안전한 사용자 등록 및 로그인
- **상품 관리**: 상품 추가, 수정 및 조회
- **검색 기능**: 효율적인 상품 검색
- **주문 처리**: 기본 배송지 설정 및 고객 주문

## 프로젝트 규칙 및 개발 전략
### 코드 컨벤션
- **Java**: [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- **파일 및 클래스 이름**: PascalCase를 사용합니다.
- **메소드 및 변수 이름**: camelCase를 사용합니다.

### 성능최적화
- 검색 속도 개선을 위해 검색 엔진 적극 활용
- 비동기를 활용하여 빠른 시간 내에 데이터 setup
- DB 서버와 통신 최소화

### 테스트
- **단위 테스트**: `JUnit 5`를 사용하여 각 기능의 단위 테스트를 작성
- **통합 테스트**: `Spring Boot Test`를 사용하여 애플리케이션의 통합 동작을 테스트
- **테스트 커버리지**: `Jacoco`를 사용하여 코드 커버리지를 측정
  - **테스트 커버리지 목표**: 80% 이상
  - 전체 코드의 80% 이상 테스트 커버리지를 충족해야 합니다. 80% 미만인 경우, 커버리지 부족으로 인해 테스트가 실패한 것으로 처리됩니다.


### 브랜치 전략
이 프로젝트에서는 **[Git Flow](https://nvie.com/posts/a-successful-git-branching-model/)** 전략을 따릅니다:

- **`main`**: 배포 가능한 상태의 코드가 포함된 브랜치
- **`develop`**: 다음 배포를 위한 기능 통합 브랜치
- **`feature`**: 새로운 기능 개발 브랜치
- **`fix`**: 버그 수정 브랜치
- **`release`**: 배포 준비 브랜치
- **`hotfix`**: 긴급 수정 브랜치

브랜치 전략은 다음과 같이 사용됩니다:

- **기능 개발 시**: `이슈번호-feat-기능명` 브랜치에서 작업 후 `develop` 브랜치에 병합
- **버그 수정 시**: `선택적 이슈번호-fix-버그명` 브랜치에서 작업 후 `develop` 브랜치에 병합
- **배포 준비 시**: `release-버전명` 브랜치에서 작업 후 `main` 및 `develop` 브랜치에 병합
- **긴급 수정 시**: `hotfix-버전명` 브랜치에서 작업 후 `main` 및 `develop` 브랜치에 병합

### 커밋 컨벤션
커밋 메시지는 다음과 같은 형식을 따릅니다:
```bash
<타입>: <설명>

<선택적 본문>
```
- **타입**:

  - **feat**: 새로운 기능 추가
  - **fix**: 버그 수정
  - **docs**: 문서 변경
  - **style**: 코드 포맷팅, 세미콜론 누락 등
  - **refactor**: 코드 리팩토링
  - **test**: 테스트 추가 또는 수정
  - **chore**: 빌드 작업, 패키지 매니저 등
 
## 사용 기술

- **백엔드**: Java, Spring Boot
- **보안**: Spring Security, JWT
- **데이터베이스**: MySQL
- **메시지 브로커**: Kafka
- **검색 엔진**: Elasticsearch

## 사용 방법
API를 사용하여 주요 기능들을 테스트할 수 있습니다.

### 기능 명세
https://github.com/f-lab-edu/e-commerce-mall/wiki/%F0%9F%94%A8-%EA%B8%B0%EB%8A%A5-%EB%AA%85%EC%84%B8-&-API-signature
### 화면 설계
https://github.com/f-lab-edu/e-commerce-mall/wiki/%F0%9F%8E%A8%ED%94%84%EB%A1%9C%ED%86%A0%ED%83%80%EC%9E%85
