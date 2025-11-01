FROM openjdk:17

#작업 디렉토리 설정
WORKDIR /app

#JAR 파일 경로 지정
ARG JAR_FILE=server-0.0.1-SNAPSHOT.jar

#JAR 복사
COPY ${JAR_FILE} /app/app.jar

#.env 파일도 컨테이너 내부로 복사 (같은 폴더에 존재해야 함)
COPY .env /app/.env

#Spring Boot가 .env를 자동으로 불러오도록 설정
ENV SPRING_CONFIG_IMPORT=optional:file:.env[.properties]

#포트 개방
EXPOSE 8080

# 실행 명령
ENTRYPOINT ["java", "-jar", "/app/app.jar"]