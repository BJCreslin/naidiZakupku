# Мониторинг производительности приложения

## Обзор

Приложение настроено для комплексного мониторинга производительности через Spring Boot Actuator с использованием Micrometer и Prometheus. Система включает мониторинг кэшей, API запросов, Telegram бота, GigaChat интеграции и общих метрик JVM.

## Архитектура мониторинга

### Компоненты
- **Spring Boot Actuator** - базовые endpoints для мониторинга
- **Micrometer** - абстракция для метрик
- **Prometheus** - сбор и хранение метрик
- **CustomMetricsService** - кастомные метрики приложения
- **CacheMonitoringConfiguration** - мониторинг кэшей

## Настройка мониторинга

### application.properties

```properties
# Actuator endpoints
management.endpoints.web.exposure.include=health,info,metrics,prometheus,caches,env,configprops
management.endpoint.health.show-details=always
management.endpoint.health.show-components=always

# Prometheus метрики
management.prometheus.metrics.export.enabled=true
management.prometheus.metrics.export.step=1m
management.prometheus.metrics.export.descriptions=true

# HTTP метрики
management.metrics.web.server.request.autotime.enabled=true
management.metrics.web.server.request.autotime.percentiles=0.5,0.95,0.99
management.metrics.web.server.request.autotime.percentiles-histogram=true

# JVM метрики
management.metrics.jvm.enabled=true
management.metrics.process.enabled=true
management.metrics.system.enabled=true

# Database метрики
management.metrics.jdbc.instrument=true
management.metrics.hikaricp.enabled=true

# Cache monitoring
management.metrics.cache.instrument=true
management.metrics.cache.monitoring.enabled=true
```

## Доступные endpoints

### 1. Общее здоровье системы

```bash
GET http://localhost:9000/actuator/health
```

**Ответ:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 419430400000,
        "threshold": 10485760
      }
    }
  }
}
```

### 2. Детальная информация о системе

```bash
GET http://localhost:9000/api/health/detailed
```

**Ответ:**
```json
{
  "status": "UP",
  "timestamp": "2024-01-15T10:30:00",
  "uptime": "2h 15m 30s",
  "memory": {
    "heapUsed": 1073741824,
    "heapMax": 2147483648,
    "nonHeapUsed": 134217728,
    "nonHeapMax": 268435456
  },
  "threads": {
    "total": 45,
    "daemon": 42,
    "peak": 50
  },
  "system": {
    "processors": 8,
    "totalMemory": 17179869184,
    "freeMemory": 8589934592,
    "maxMemory": 2147483648
  },
  "metrics": {
    "jvm_memory_used": 1073741824.0,
    "jvm_memory_max": 2147483648.0,
    "jvm_threads_live": 45.0,
    "process_cpu_usage": 0.15,
    "http_server_requests_total": 1250.0,
    "cache_size_total": 156.0
  }
}
```

### 3. Prometheus метрики

```bash
GET http://localhost:9000/actuator/prometheus
```

## Кастомные метрики приложения

### Telegram метрики

| Метрика | Описание | Тип |
|---------|----------|-----|
| `telegram_update_processing_time` | Время обработки Telegram обновлений | Timer |
| `telegram_commands` | Количество выполненных команд | Counter |
| `telegram_duplicate_updates` | Количество дубликатов обновлений | Counter |

### GigaChat метрики

| Метрика | Описание | Тип |
|---------|----------|-----|
| `gigachat_request_time` | Время запросов к GigaChat API | Timer |
| `gigachat_errors` | Количество ошибок GigaChat | Counter |

### API метрики

| Метрика | Описание | Тип |
|---------|----------|-----|
| `api_requests` | Количество API запросов по статусам | Counter |
| `jwt_validation_time` | Время валидации JWT токенов | Timer |
| `rate_limit_exceeded` | Превышения rate limit | Counter |

### Кэш метрики

| Метрика | Описание | Тип |
|---------|----------|-----|
| `cache_hits` | Количество попаданий в кэш | Counter |
| `cache_misses` | Количество промахов кэша | Counter |
| `cache_size` | Размер кэша | Gauge |

### Новостные метрики

| Метрика | Описание | Тип |
|---------|----------|-----|
| `news_parsing_time` | Время парсинга новостей | Timer |
| `procurement_searches` | Количество поисков закупок | Counter |

## Prometheus запросы

### Telegram метрики

```promql
# Среднее время обработки Telegram обновлений
rate(telegram_update_processing_time_seconds_sum[5m]) / rate(telegram_update_processing_time_seconds_count[5m])

# Количество команд по типам
telegram_commands_total

# Процент дубликатов
rate(telegram_duplicate_updates_total[5m]) / rate(telegram_commands_total[5m])
```

### GigaChat метрики

```promql
# Среднее время ответа GigaChat
rate(gigachat_request_time_seconds_sum[5m]) / rate(gigachat_request_time_seconds_count[5m])

# Ошибки GigaChat
rate(gigachat_errors_total[5m])
```

### API метрики

```promql
# HTTP запросы по статусам
rate(http_server_requests_seconds_count[5m])

# Время валидации JWT
histogram_quantile(0.95, rate(jwt_validation_time_seconds_bucket[5m]))

# Rate limit превышения
rate(rate_limit_exceeded_total[5m])
```

### Кэш метрики

```promql
# Hit ratio для всех кэшей
cache_gets_total{result="hit"} / cache_gets_total

# Размер кэшей
cache_size

# Evictions
rate(cache_evictions_total[5m])
```

### Системные метрики

```promql
# Использование памяти
jvm_memory_used_bytes / jvm_memory_max_bytes

# CPU использование
process_cpu_usage

# Количество потоков
jvm_threads_live_threads
```

## Алерты и мониторинг

### Критические алерты

1. **Высокое время ответа API**
```promql
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m])) > 2
```

2. **Высокое использование памяти**
```promql
jvm_memory_used_bytes / jvm_memory_max_bytes > 0.85
```

3. **Много ошибок GigaChat**
```promql
rate(gigachat_errors_total[5m]) > 0.1
```

4. **Превышения rate limit**
```promql
rate(rate_limit_exceeded_total[5m]) > 5
```

### Предупреждающие алерты

1. **Низкий hit ratio кэша**
```promql
cache_gets_total{result="hit"} / cache_gets_total < 0.7
```

2. **Медленные Telegram обновления**
```promql
histogram_quantile(0.95, rate(telegram_update_processing_time_seconds_bucket[5m])) > 1
```

3. **Много дубликатов Telegram**
```promql
rate(telegram_duplicate_updates_total[5m]) / rate(telegram_commands_total[5m]) > 0.3
```

## Grafana Dashboard

### Основные панели

1. **Обзор системы**
   - CPU и Memory использование
   - Количество потоков
   - Uptime приложения

2. **API производительность**
   - HTTP запросы по статусам
   - Время ответа (p50, p95, p99)
   - Rate limit превышения

3. **Telegram бот**
   - Время обработки обновлений
   - Количество команд
   - Дубликаты обновлений

4. **GigaChat интеграция**
   - Время ответов
   - Количество ошибок
   - Использование по моделям

5. **Кэши**
   - Hit ratio по кэшам
   - Размер кэшей
   - Evictions

6. **База данных**
   - Время запросов
   - Количество соединений
   - Pool статистика

### Примеры запросов для панелей

**Telegram Processing Time:**
```promql
histogram_quantile(0.95, rate(telegram_update_processing_time_seconds_bucket[5m]))
```

**Cache Hit Ratio:**
```promql
sum(rate(cache_gets_total{result="hit"}[5m])) by (cache) / 
sum(rate(cache_gets_total[5m])) by (cache)
```

**API Response Time:**
```promql
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))
```

## Логирование

### Настройка логов

```properties
# Логирование метрик
logging.level.io.micrometer=INFO
logging.level.org.springframework.boot.actuator=INFO

# Логирование кэшей
logging.level.org.springframework.cache=INFO

# Логирование производительности
logging.level.ru.bjcreslin.naidizakupku.telegram=DEBUG
logging.level.ru.bjcreslin.naidizakupku.gigachat=DEBUG
```

### Примеры логов

```
2024-01-15 10:30:15 [http-nio-9000-exec-1] INFO  o.s.cache.interceptor.CacheInterceptor - Cache hit for key '123' in cache 'telegramUpdateCache'
2024-01-15 10:30:16 [http-nio-9000-exec-2] INFO  r.b.n.telegram.TelegramBot - Update 124 processed in 150ms
2024-01-15 10:30:17 [http-nio-9000-exec-3] INFO  r.b.n.gigachat.GigachatService - GigaChat request completed in 2500ms using model: GigaChat:latest
```

## Troubleshooting

### Проблема: Метрики не отображаются

**Решение:**
1. Проверить endpoint: `GET /actuator/health`
2. Убедиться что все endpoints включены в `management.endpoints.web.exposure.include`
3. Проверить логи на ошибки Micrometer

### Проблема: Высокое время ответа

**Диагностика:**
1. Проверить `http_server_requests_seconds`
2. Анализировать `telegram_update_processing_time`
3. Проверить `gigachat_request_time`

### Проблема: Низкий hit ratio кэша

**Решения:**
1. Увеличить размер кэша
2. Увеличить TTL
3. Оптимизировать ключи кэша

### Проблема: Частые rate limit превышения

**Решения:**
1. Увеличить лимиты в RateLimitingFilter
2. Оптимизировать клиентские запросы
3. Добавить кэширование на клиенте

## Производительность

### Оптимизация на основе метрик

1. **Кэширование:** Мониторить hit ratio и увеличивать размер/время жизни при необходимости
2. **Telegram:** Оптимизировать обработку обновлений при высоком времени
3. **GigaChat:** Кэшировать частые запросы, использовать пул соединений
4. **API:** Оптимизировать медленные endpoints

### Мониторинг трендов

- Ежедневные отчеты по производительности
- Алерты на аномалии
- Автоматическое масштабирование на основе метрик

## Заключение

Настроенная система мониторинга обеспечивает полную видимость в производительность приложения. Используйте метрики для:

- Проактивного выявления проблем
- Оптимизации производительности
- Планирования ресурсов
- Анализа трендов использования

Регулярно анализируйте метрики и настраивайте алерты для обеспечения стабильной работы системы.
