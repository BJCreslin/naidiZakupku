# Настройка системы мониторинга NaidiZakupku

## Обзор

Данное руководство описывает процесс настройки комплексной системы мониторинга для приложения NaidiZakupku, включающей Prometheus, Grafana и кастомные метрики.

## Компоненты системы мониторинга

### 1. Spring Boot Actuator
- Предоставляет endpoints для мониторинга здоровья приложения
- Экспортирует метрики в формате Prometheus
- Включает кастомные метрики производительности

### 2. Prometheus
- Сбор и хранение метрик
- Правила алертов
- PromQL запросы

### 3. Grafana
- Визуализация метрик
- Дашборды мониторинга
- Алерты

## Установка и настройка

### 1. Запуск приложения

Убедитесь, что приложение запущено с правильными настройками мониторинга:

```bash
# Проверка доступности endpoints
curl http://localhost:9000/actuator/health
curl http://localhost:9000/actuator/prometheus
```

### 2. Установка Prometheus

#### Docker Compose (рекомендуется)

Создайте файл `docker-compose.monitoring.yml`:

```yaml
version: '3.8'

services:
  prometheus:
    image: prom/prometheus:latest
    container_name: prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - ./prometheus-alerts.yml:/etc/prometheus/alerts.yml
      - prometheus_data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'
      - '--storage.tsdb.retention.time=200h'
      - '--web.enable-lifecycle'

  grafana:
    image: grafana/grafana:latest
    container_name: grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    volumes:
      - grafana_data:/var/lib/grafana
      - ./grafana-dashboard.json:/etc/grafana/provisioning/dashboards/dashboard.json

volumes:
  prometheus_data:
  grafana_data:
```

#### Конфигурация Prometheus

Создайте файл `prometheus.yml`:

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  - "alerts.yml"

scrape_configs:
  - job_name: 'naidizakupku'
    static_configs:
      - targets: ['host.docker.internal:9000']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 15s
    scrape_timeout: 10s

  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']
```

### 3. Установка Grafana

#### Настройка дашборда

1. Откройте Grafana: http://localhost:3000
2. Логин: admin / admin
3. Добавьте Prometheus как источник данных:
   - URL: http://prometheus:9090
   - Access: Server (default)

#### Импорт дашборда

1. Создайте файл `grafana-dashboard.json` с конфигурацией из `docs/grafana-dashboard.json`
2. В Grafana: Dashboards → Import → Upload JSON file
3. Выберите источник данных Prometheus

### 4. Настройка алертов

#### Prometheus алерты

1. Скопируйте `docs/prometheus-alerts.yml` в контейнер Prometheus
2. Перезапустите Prometheus для применения правил

#### Grafana алерты

1. В дашборде создайте алерты для критических метрик
2. Настройте уведомления (email, Slack, Telegram)

## Проверка работоспособности

### 1. Проверка метрик

```bash
# Проверка доступности метрик
curl http://localhost:9000/actuator/metrics

# Проверка конкретных метрик
curl http://localhost:9000/actuator/metrics/jvm.memory.used
curl http://localhost:9000/actuator/metrics/http.server.requests
```

### 2. Проверка Prometheus

1. Откройте http://localhost:9090
2. Перейдите в Status → Targets
3. Убедитесь, что target `naidizakupku` в состоянии UP

### 3. Проверка Grafana

1. Откройте http://localhost:3000
2. Проверьте, что дашборд отображает данные
3. Убедитесь, что все панели работают

## Основные метрики для мониторинга

### Системные метрики

- **JVM Memory**: `jvm_memory_used_bytes / jvm_memory_max_bytes`
- **CPU Usage**: `process_cpu_usage`
- **Thread Count**: `jvm_threads_live_threads`

### Прикладные метрики

- **HTTP Requests**: `http_server_requests_seconds_count`
- **Response Time**: `http_server_requests_seconds`
- **Telegram Processing**: `telegram_update_processing_time_seconds`
- **GigaChat Requests**: `gigachat_request_time_seconds`

### Кэш метрики

- **Hit Ratio**: `cache_gets_total{result="hit"} / cache_gets_total`
- **Cache Size**: `cache_size`
- **Evictions**: `cache_evictions_total`

## Алерты и уведомления

### Критические алерты

- Высокое использование памяти (>85%)
- Высокое время ответа API (>2s)
- Много ошибок GigaChat (>0.1/s)
- Превышения rate limit (>5/s)

### Предупреждающие алерты

- Низкий hit ratio кэша (<70%)
- Медленные Telegram обновления (>1s)
- Высокое время валидации JWT (>100ms)

## Оптимизация производительности

### На основе метрик

1. **Кэширование**: Увеличивайте размер кэша при низком hit ratio
2. **Telegram**: Оптимизируйте обработку при высоком времени
3. **GigaChat**: Кэшируйте частые запросы
4. **API**: Оптимизируйте медленные endpoints

### Мониторинг трендов

- Ежедневные отчеты по производительности
- Анализ пиковых нагрузок
- Планирование ресурсов

## Troubleshooting

### Проблема: Метрики не отображаются

**Решение:**
1. Проверьте endpoint: `GET /actuator/health`
2. Убедитесь, что все endpoints включены
3. Проверьте логи на ошибки Micrometer

### Проблема: Prometheus не собирает метрики

**Решение:**
1. Проверьте доступность target в Prometheus UI
2. Убедитесь, что приложение доступно по адресу
3. Проверьте конфигурацию scrape_configs

### Проблема: Grafana не отображает данные

**Решение:**
1. Проверьте подключение к Prometheus
2. Убедитесь, что метрики существуют в Prometheus
3. Проверьте синтаксис запросов

## Расширение мониторинга

### Добавление новых метрик

1. Создайте новый метод в `CustomMetricsService`
2. Добавьте вызов в соответствующий сервис
3. Обновите дашборд Grafana

### Добавление новых алертов

1. Добавьте правило в `prometheus-alerts.yml`
2. Перезапустите Prometheus
3. Настройте уведомления в Grafana

## Заключение

Настроенная система мониторинга обеспечивает полную видимость в производительность приложения. Регулярно анализируйте метрики и настраивайте алерты для обеспечения стабильной работы системы.

Для получения дополнительной информации обратитесь к документации:
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer](https://micrometer.io/docs)
- [Prometheus](https://prometheus.io/docs/)
- [Grafana](https://grafana.com/docs/)
