@echo off
chcp 65001 > nul
echo Компіляція та запуск проєкту через Maven (ЛР 1-10)...
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    call mvn compile exec:java
) else (
    call "C:\Users\Admin\apache-maven-3.9.9\bin\mvn.cmd" compile exec:java
)
if %ERRORLEVEL% NEQ 0 (
    echo Помилка виконання!
    pause
)
