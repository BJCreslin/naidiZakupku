#!/bin/bash

# Скрипт для запуска системы мониторинга NaidiZakupku

set -e

echo "🚀 Запуск системы мониторинга NaidiZakupku..."

# Проверка наличия Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker не установлен. Установите Docker и попробуйте снова."
    exit 1
fi

# Проверка наличия Docker Compose
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose не установлен. Установите Docker Compose и попробуйте снова."
    exit 1
fi

# Проверка доступности приложения
echo "🔍 Проверка доступности приложения..."
if ! curl -s http://localhost:9000/actuator/health > /dev/null; then
    echo "❌ Приложение NaidiZakupku недоступно на порту 9000"
    echo "   Убедитесь, что приложение запущено и доступно"
    exit 1
fi

echo "✅ Приложение доступно"

# Создание необходимых директорий
echo "📁 Создание директорий..."
mkdir -p logs
mkdir -p data/prometheus
mkdir -p data/grafana
mkdir -p data/alertmanager

# Запуск системы мониторинга
echo "🐳 Запуск контейнеров мониторинга..."
docker-compose -f docker-compose.monitoring.yml up -d

# Ожидание запуска сервисов
echo "⏳ Ожидание запуска сервисов..."
sleep 10

# Проверка статуса сервисов
echo "🔍 Проверка статуса сервисов..."

# Проверка Prometheus
if curl -s http://localhost:9090/-/healthy > /dev/null; then
    echo "✅ Prometheus запущен (http://localhost:9090)"
else
    echo "❌ Prometheus не отвечает"
fi

# Проверка Grafana
if curl -s http://localhost:3000/api/health > /dev/null; then
    echo "✅ Grafana запущен (http://localhost:3000)"
    echo "   Логин: admin"
    echo "   Пароль: admin"
else
    echo "❌ Grafana не отвечает"
fi

# Проверка AlertManager
if curl -s http://localhost:9093/-/healthy > /dev/null; then
    echo "✅ AlertManager запущен (http://localhost:9093)"
else
    echo "❌ AlertManager не отвечает"
fi

echo ""
echo "🎉 Система мониторинга запущена!"
echo ""
echo "📊 Доступные сервисы:"
echo "   • Prometheus: http://localhost:9090"
echo "   • Grafana: http://localhost:3000 (admin/admin)"
echo "   • AlertManager: http://localhost:9093"
echo ""
echo "📈 Для импорта дашборда в Grafana:"
echo "   1. Откройте http://localhost:3000"
echo "   2. Добавьте Prometheus как источник данных (http://prometheus:9090)"
echo "   3. Импортируйте дашборд из docs/grafana-dashboard.json"
echo ""
echo "🛑 Для остановки: ./scripts/stop-monitoring.sh"
echo "📋 Для просмотра логов: ./scripts/monitoring-logs.sh"
