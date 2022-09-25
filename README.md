# daoutech-api-master

스케줄러 및 api 서버 구현
> 특정 경로의 파일을 스케줄링하여 read 및 DB에 저장하고, 해당 데이터를 api로 CRUD할 수 있는 Backend 서버 구현



### 사용 기술
``` bash
  - Spring boot 2.7.3
  - Spring security 5.7.3
  - Spring data Jpa 2.7.2
  - Json Web Token 0.9.1
  - h2 database 2.1.214
  - resilience4j-spring-boot2 1.7.1
  - junit 5.8.2
  - lombok 1.18.24
```



### 실행
``` bash
# Change to application root directory
cd /foldername  (foldername은 git을 통해 다운로드한 루트 폴더명)

# gradle build (-Pprofile: resources-{env} 디렉토리의 properties파일을 포함)
gradlew -Pprofile=dev bootJar

# Change to build directory
cd build
cd libs

# run jar with localhost:8080 (IPv4 사용설정, active profile 설정)
java -jar daoutech-api-master-0.0.1.jar --spring.profiles.active=dev -Djava.net.preferIPv4Stack=true
```



### API 문서
``` bash
  - https://documenter.getpostman.com/view/1356289/2s7ZLdLYpU#16aa772f-d87e-46a5-8310-537666feba43
```




### 테스트
``` bash
#### 초기 사용자 데이터(for 인증)
  - 관리자 (통계정보 조회/등록/수정/삭제 가능)
    - admin / 1234
  - 일반사용자 (통계정보 조회만 가능)
    - user / 1234
  
#### 기타
  - 추가 사용자가 필요할 경우, 사용자 등록 API로 등록
  - 사용자 인증 API를 사용하여 인증 토큰 발급 및 CRUD API 호출시 발급된 토큰기반으로 호출 (API 문서 참고)
  
#### 경로
  - 파일경로: 어플리케이션 실행경로의 sample/sample.dat
  - 로그경로: 어플리케이션 실행경로의 log/dev/daoutech-api-master_yyyyMMddHH.log
```




### 구현 내용
``` bash
#### 스케줄링을 통해 특정경로 파일 데이터 read / DB에 저장
  - 자정에 특정경로(application-dev.properties 참고) 파일데이터를 읽어 DB 저장 및 파일 삭제처리
  - 매 시간 랜덤데이터를 특정경로 파일에 append하여 저장 (구분자 '|')
  - 파일형태가 변경될 수 있음을 고려하여 탭/쉼표 구분자 정규식 선언(FileParser 클래스). 구분자만 변경 적용하여 정상 파싱할 수 있도록 구현.

#### 스케줄링을 통해 저장된 정보를 CRUD 하는 api 구현
  - API 문서 참고
  - Spring security 설정을 통해 설정파일에 등록된 아이피(127.0.0.1) 에서만 허용 처리
  - logback 설정(logback-spring.xml)읕 통해 로그 파일명/형식 적용
  - @Validated group을 이용하여 요청에 대한 유효성 적용 및 오류응답 적용
  
#### API 요청 인증 및 rate limit 적용
  - 사용자 인증 API를 통해 jwt 토큰을 발급하여 해당 토큰으로 인증 처리, Spring security 적용으로 Uri별 접근 권한 적용
  - resilience4j ratelimit 를 사용하여 초당 5회로 요청제한 및 오류응답 처리
  
#### 단위테스트 적용
  - junit5를 이용하여 단위테스트 적용
```
