# Мониторинг кэшей приложения

## Обзор

Приложение настроено для мониторинга кэшей через Spring Boot Actuator с использованием Micrometer и Prometheus. Все кэши (Caffeine) автоматически инструментированы и предоставляют детальную статистику.

## Настройка мониторинга

### application.properties

```properties
# Включение endpoints для мониторинга
management.endpoints.web.exposure.include=health,info,metrics,prometheus,caches
management.prometheus.metrics.export.enabled=true
management.prometheus.metrics.export.step=1m

# Настройки мониторинга кэшей
management.endpoint.caches.enabled=true
management.metrics.cache.instrument=true
logging.level.org.springframework.cache=INFO
```

### CacheConfiguration.kt

```kotlin
private fun caffeine() =
    Caffeine.newBuilder()
        .expireAfterWrite(10, java.util.concurrent.TimeUnit.MINUTES)
        .maximumSize(200)
        .recordStats() // Включение сбора статистики
```

### CacheMonitoringConfiguration.kt

Автоматически регистрирует метрики всех Caffeine кэшей в Micrometer после запуска приложения.

## Доступные endpoints

### 1. Общая информация о кэшах

```bash
GET http://localhost:9000/actuator/caches
```

**Ответ:**
```json
{
  "cacheManagers": {
    "cacheManager": {
      "caches": {
        "ProjectInfoCache": {
          "target": "com.github.benmanes.caffeine.cache.BoundedLocalCache"
        },
        "gigachatSessionCache": {
          "target": "com.github.benmanes.caffeine.cache.BoundedLocalCache"
        },
        "telegramUpdateCache": {
          "target": "com.github.benmanes.caffeine.cache.BoundedLocalCache"
        },
        "telegramUserCache": {
          "target": "com.github.benmanes.caffeine.cache.BoundedLocalCache"
        },
        "telegramStateCache": {
          "target": "com.github.benmanes.caffeine.cache.BoundedLocalCache"
        },
        "procurementsListCache": {
          "target": "com.github.benmanes.caffeine.cache.BoundedLocalCache"
        },
        "statsCache": {
          "target": "com.github.benmanes.caffeine.cache.BoundedLocalCache"
        },
        "helpMessageCache": {
          "target": "com.github.benmanes.caffeine.cache.BoundedLocalCache"
        }
      }
    }
  }
}
```

### 2. Детальная информация о конкретном кэше

```bash
GET http://localhost:9000/actuator/caches/telegramUpdateCache
```

**Ответ:**
```json
{
  "target": "com.github.benmanes.caffeine.cache.BoundedLocalCache",
  "name": "telegramUpdateCache",
  "cacheManager": "cacheManager"
}
```

### 3. Метрики кэшей

```bash
GET http://localhost:9000/actuator/metrics/cache.size
GET http://localhost:9000/actuator/metrics/cache.gets
GET http://localhost:9000/actuator/metrics/cache.puts
GET http://localhost:9000/actuator/metrics/cache.evictions
```

**Пример ответа для cache.size:**
```json
{
  "name": "cache.size",
  "measurements": [
    {
      "statistic": "VALUE",
      "value": 15.0
    }
  ],
  "availableTags": [
    {
      "tag": "cache",
      "values": ["ProjectInfoCache", "gigachatSessionCache", "telegramUpdateCache", "telegramUserCache", "telegramStateCache", "procurementsListCache", "statsCache", "helpMessageCache"]
    },
    {
      "tag": "cache.manager",
      "values": ["cacheManager"]
    }
  ]
}
```

### 4. Метрики конкретного кэша

```bash
GET http://localhost:9000/actuator/metrics/cache.gets?tag=cache:telegramUpdateCache
```

## Prometheus метрики

### Endpoint для Prometheus

```bash
GET http://localhost:9000/actuator/prometheus
```

### Основные метрики кэшей

| Метрика | Описание |
|---------|----------|
| `cache_size` | Текущий размер кэша (количество записей) |
| `cache_gets_total` | Общее количество запросов к кэшу |
| `cache_puts_total` | Общее количество записей в кэш |
| `cache_evictions_total` | Количество вытесненных записей |
| `cache_hit_ratio` | Процент попаданий в кэш |
| `cache_miss_ratio` | Процент промахов кэша |

### Пример Prometheus запросов

```promql
# Размер всех кэшей
cache_size

# Hit rate для telegram кэша
cache_gets_total{cache="telegramUpdateCache", result="hit"} / 
cache_gets_total{cache="telegramUpdateCache"}

# Количество evictions по кэшам
sum by (cache) (cache_evictions_total)
```

## Мониторинг кэшей

### 1. Telegram Update Cache

**Назначение:** Дедупликация обновлений Telegram

**Ключевые метрики:**
- `cache_size{cache="telegramUpdateCache"}` - количество обработанных updateId
- `cache_puts_total{cache="telegramUpdateCache"}` - количество новых обновлений
- `cache_gets_total{cache="telegramUpdateCache"}` - общее количество проверок

**Нормальное поведение:**
- Size: 0-200 (ограничение по конфигурации)
- Hit ratio: зависит от количества дубликатов (обычно низкий для новых обновлений)
- Evictions: происходят при превышении размера или TTL (10 минут)

### 2. GigaChat Session Cache

**Назначение:** Кэширование сессий GigaChat

**Ключевые метрики:**
- `cache_size{cache="gigachatSessionCache"}` - количество активных сессий
- `cache_hit_ratio{cache="gigachatSessionCache"}` - эффективность кэширования

### 3. Telegram User Cache

**Назначение:** Кэширование пользователей Telegram

**Ключевые метрики:**
- `cache_size{cache="telegramUserCache"}` - количество активных пользователей
- `cache_hit_ratio{cache="telegramUserCache"}` - эффективность кэширования пользователей

### 4. Telegram State Cache

**Назначение:** Кэширование состояний пользователей

**Ключевые метрики:**
- `cache_size{cache="telegramStateCache"}` - количество пользователей с состояниями
- `cache_hit_ratio{cache="telegramStateCache"}` - эффективность кэширования состояний

### 5. Stats Cache

**Назначение:** Кэширование статистики пользователей

**Ключевые метрики:**
- `cache_size{cache="statsCache"}` - количество кэшированных статистик
- `cache_hit_ratio{cache="statsCache"}` - эффективность кэширования статистики

### 6. Help Message Cache

**Назначение:** Кэширование справочных сообщений

**Ключевые метрики:**
- `cache_size{cache="helpMessageCache"}` - обычно 1 запись
- `cache_hit_ratio{cache="helpMessageCache"}` - должен быть высокий

### 7. Project Info Cache

**Назначение:** Кэширование информации о проекте

**Ключевые метрики:**
- `cache_size{cache="ProjectInfoCache"}` - обычно 1 запись
- `cache_hit_ratio{cache="ProjectInfoCache"}` - должен быть высокий

## Алерты и мониторинг

### Рекомендуемые алерты

1. **Высокий miss rate**
```promql
cache_gets_total{result="miss"} / cache_gets_total > 0.8
```

2. **Превышение размера кэша**
```promql
cache_size > 180  # 90% от максимума (200)
```

3. **Частые evictions**
```promql
rate(cache_evictions_total[5m]) > 10
```

### Grafana Dashboard

Пример панелей для Grafana:

1. **Cache Hit Ratio**
```promql
cache_gets_total{result="hit"} / cache_gets_total
```

2. **Cache Size Over Time**
```promql
cache_size
```

3. **Operations Rate**
```promql
rate(cache_gets_total[1m])
rate(cache_puts_total[1m])
```

## Логирование

### Включение DEBUG логов для кэша

```properties
logging.level.org.springframework.cache=DEBUG
logging.level.com.github.benmanes.caffeine=DEBUG
```

### Примеры логов

```
2024-01-15 10:30:15 [http-nio-9000-exec-1] INFO  o.s.cache.interceptor.CacheInterceptor - Cache hit for key '123' in cache 'telegramUpdateCache'
2024-01-15 10:30:16 [http-nio-9000-exec-2] INFO  o.s.cache.interceptor.CacheInterceptor - Cache miss for key '124' in cache 'telegramUpdateCache'
```

## Troubleshooting

### Проблема: Метрики не отображаются

**Решение:**
1. Проверить endpoint: `GET /actuator/health`
2. Убедиться что `management.metrics.cache.instrument=true`
3. Проверить что кэши используются (есть операции get/put)

### Проблема: Низкий hit rate

**Возможные причины:**
1. TTL слишком короткий (10 минут)
2. Размер кэша слишком мал (200 записей)
3. Ключи кэша генерируются неправильно

### Проблема: Частые evictions

**Решения:**
1. Увеличить `maximumSize` в CacheConfiguration
2. Увеличить `expireAfterWrite` время
3. Проверить паттерны использования кэша

## Производительность

### Оптимизация кэшей

1. **Размер кэша:** Настроить на основе реального использования
2. **TTL:** Баланс между свежестью данных и hit rate
3. **Eviction policy:** Caffeine использует W-TinyLFU (оптимально)

### Мониторинг производительности

```bash
# JVM метрики
GET /actuator/metrics/jvm.memory.used
GET /actuator/metrics/jvm.gc.pause

# Application метрики
GET /actuator/metrics/http.server.requests
```

## Заключение

Настроенный мониторинг кэшей обеспечивает полную видимость в работу системы кэширования. Используйте метрики для оптимизации производительности и настройки алертов для проактивного мониторинга.
