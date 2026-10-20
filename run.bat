@echo off
chcp 65001 > nul
echo Компіляція проєкту...
javac -encoding UTF-8 *.java
if %errorlevel% neq 0 (
    echo Помилка компіляції!
    pause
    exit /b %errorlevel%
)
echo Запуск Трекера завдань...
java -Dfile.encoding=UTF-8 Main
pause
