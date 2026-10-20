[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
[Console]::InputEncoding  = [System.Text.Encoding]::UTF8
chcp 65001 > $null

Write-Host "Компіляція проєкту..." -ForegroundColor Cyan
javac -encoding UTF-8 *.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "Помилка компіляції!" -ForegroundColor Red
    pause
    exit $LASTEXITCODE
}

Write-Host "Запуск Трекера завдань..." -ForegroundColor Green
java "-Dfile.encoding=UTF-8" Main
