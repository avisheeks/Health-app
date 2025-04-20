@echo off
echo Starting Spring Boot application...
mvn spring-boot:run > app.log 2>&1
echo Application started. See app.log for details. 