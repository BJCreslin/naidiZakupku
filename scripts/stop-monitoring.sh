#!/bin/bash

# Скрипт для остановки системы мониторинга NaidiZakupku

echo "🛑 Остановка системы мониторинга NaidiZakupku..."

# Остановка контейнеров
docker-compose -f docker-compose.monitoring.yml down

echo "✅ Система мониторинга остановлена"

# Опционально: удаление данных (раскомментируйте при необходимости)
# echo "🗑️ Удаление данных мониторинга..."
# docker volume rm naidizakupku_prometheus_data naidizakupku_grafana_data naidizakupku_alertmanager_data

echo ""
echo "📊 Сервисы остановлены:"
echo "   • Prometheus"
echo "   • Grafana"
echo "   • AlertManager"
echo ""
echo "🚀 Для повторного запуска: ./scripts/start-monitoring.sh"
