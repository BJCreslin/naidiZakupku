#!/bin/bash

# Скрипт для просмотра логов системы мониторинга NaidiZakupku

echo "📋 Логи системы мониторинга NaidiZakupku"
echo ""

# Проверка аргументов
if [ $# -eq 0 ]; then
    echo "Использование: $0 [prometheus|grafana|alertmanager|all]"
    echo ""
    echo "Примеры:"
    echo "  $0 prometheus    - логи Prometheus"
    echo "  $0 grafana       - логи Grafana"
    echo "  $0 alertmanager  - логи AlertManager"
    echo "  $0 all           - логи всех сервисов"
    exit 1
fi

SERVICE=$1

case $SERVICE in
    "prometheus")
        echo "📊 Логи Prometheus:"
        docker-compose -f docker-compose.monitoring.yml logs -f prometheus
        ;;
    "grafana")
        echo "📈 Логи Grafana:"
        docker-compose -f docker-compose.monitoring.yml logs -f grafana
        ;;
    "alertmanager")
        echo "🚨 Логи AlertManager:"
        docker-compose -f docker-compose.monitoring.yml logs -f alertmanager
        ;;
    "all")
        echo "📋 Логи всех сервисов:"
        docker-compose -f docker-compose.monitoring.yml logs -f
        ;;
    *)
        echo "❌ Неизвестный сервис: $SERVICE"
        echo "Доступные сервисы: prometheus, grafana, alertmanager, all"
        exit 1
        ;;
esac
