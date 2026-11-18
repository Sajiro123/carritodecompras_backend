@echo off
set "JAVA_HOME=C:\laragon\bin\java\jdk-25.0.5.0.2+1"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo Starting Carrito de Compras Backend (Spring Boot)...
call .\mvnw.cmd spring-boot:run
