#자바 버전
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 경로 지정
ARG JAR_FILE=server-0.0.1-SNAPSHOT.jar

# JAR 복사
COPY ${JAR_FILE} /app/app.jar

# 포트 개방
EXPOSE 8080

# 실행 명령
ENTRYPOINT ["java", "-jar", "/app/app.jar"]