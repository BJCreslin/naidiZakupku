@echo off
REM Скрипт для запуска системы мониторинга NaidiZakupku

echo 🚀 Запуск системы мониторинга NaidiZakupku...

REM Проверка наличия Docker
docker --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker не установлен. Установите Docker и попробуйте снова.
    pause
    exit /b 1
)

REM Проверка наличия Docker Compose
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker Compose не установлен. Установите Docker Compose и попробуйте снова.
    pause
    exit /b 1
)

REM Проверка доступности приложения
echo 🔍 Проверка доступности приложения...
curl -s http://localhost:9000/actuator/health >nul 2>&1
if errorlevel 1 (
    echo ❌ Приложение NaidiZakupku недоступно на порту 9000
    echo    Убедитесь, что приложение запущено и доступно
    pause
    exit /b 1
)

echo ✅ Приложение доступно

REM Создание необходимых директорий
echo 📁 Создание директорий...
if not exist "logs" mkdir logs
if not exist "data\prometheus" mkdir data\prometheus
if not exist "data\grafana" mkdir data\grafana
if not exist "data\alertmanager" mkdir data\alertmanager

REM Запуск системы мониторинга
echo 🐳 Запуск контейнеров мониторинга...
docker-compose -f docker-compose.monitoring.yml up -d

REM Ожидание запуска сервисов
echo ⏳ Ожидание запуска сервисов...
timeout /t 10 /nobreak >nul

REM Проверка статуса сервисов
echo 🔍 Проверка статуса сервисов...

REM Проверка Prometheus
curl -s http://localhost:9090/-/healthy >nul 2>&1
if errorlevel 1 (
    echo ❌ Prometheus не отвечает
) else (
    echo ✅ Prometheus запущен (http://localhost:9090)
)

REM Проверка Grafana
curl -s http://localhost:3000/api/health >nul 2>&1
if errorlevel 1 (
    echo ❌ Grafana не отвечает
) else (
    echo ✅ Grafana запущен (http://localhost:3000)
    echo    Логин: admin
    echo    Пароль: admin
)

REM Проверка AlertManager
curl -s http://localhost:9093/-/healthy >nul 2>&1
if errorlevel 1 (
    echo ❌ AlertManager не отвечает
) else (
    echo ✅ AlertManager запущен (http://localhost:9093)
)

echo.
echo 🎉 Система мониторинга запущена!
echo.
echo 📊 Доступные сервисы:
echo    • Prometheus: http://localhost:9090
echo    • Grafana: http://localhost:3000 (admin/admin)
echo    • AlertManager: http://localhost:9093
echo.
echo 📈 Для импорта дашборда в Grafana:
echo    1. Откройте http://localhost:3000
echo    2. Добавьте Prometheus как источник данных (http://prometheus:9090)
echo    3. Импортируйте дашборд из docs/grafana-dashboard.json
echo.
echo 🛑 Для остановки: scripts\stop-monitoring.bat
echo 📋 Для просмотра логов: scripts\monitoring-logs.bat

pause
