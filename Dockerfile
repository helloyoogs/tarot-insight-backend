# 1. Java 17 환경 베이스 이미지
FROM eclipse-temurin:17-jdk-alpine

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 빌드된 JAR 파일을 컨테이너 안으로 복사
COPY build/libs/*-SNAPSHOT.jar app.jar

# 컨테이너가 사용할 포트 명시
EXPOSE 8080

# 4. 앱 실행 (옵션 순서 최적화)
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]