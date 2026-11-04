[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
[Console]::InputEncoding  = [System.Text.Encoding]::UTF8
chcp 65001 > $null

Write-Host "Компіляція та запуск проєкту через Maven (ЛР 1-10)..." -ForegroundColor Cyan

$mvnCmd = "mvn"
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    if (Test-Path "C:\Users\Admin\apache-maven-3.9.9\bin\mvn.cmd") {
        $mvnCmd = "C:\Users\Admin\apache-maven-3.9.9\bin\mvn.cmd"
    }
}

& $mvnCmd compile exec:java
if ($LASTEXITCODE -ne 0) {
    Write-Host "Помилка виконання!" -ForegroundColor Red
    pause
    exit $LASTEXITCODE
}
