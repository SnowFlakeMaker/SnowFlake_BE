# Java 21을 사용하는 Spring Boot 애플리케이션 빌드
FROM openjdk:21-jdk-slim

# 필요한 패키지 업데이트 및 wget 설치
RUN apt-get update && apt-get install -y wget

# wait-for-it.sh 스크립트 다운로드 및 실행 권한 부여
RUN wget -O /usr/local/bin/wait-for-it.sh https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh && \
    chmod +x /usr/local/bin/wait-for-it.sh

# JAR 파일을 컨테이너로 복사
ARG JAR_FILE=build/libs/noonsongmaker-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 포트 오픈
EXPOSE 8080

# wait-for-it 스크립트를 사용하여 Redis(redis:6379)가 준비될 때까지 기다린 후 애플리케이션 실행
ENTRYPOINT ["/usr/local/bin/wait-for-it.sh", "redis:6379", "--", "java", "-jar", "/app.jar"]