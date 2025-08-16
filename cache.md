# Cache Configuration and Telegram Update Deduplication

## Обзор

В проекте используется Spring Boot Starter Cache с провайдером Caffeine для кэширования данных. Кэш настроен для различных компонентов системы, включая дедупликацию обновлений Telegram.

## Конфигурация кэша

### CacheConfiguration.kt

```kotlin
@Configuration
@EnableCaching
class CacheConfiguration {
    @Bean
    fun cacheManager(): CacheManager = CaffeineCacheManager().apply { 
        setCaffeine(caffeine())
        setCacheNames(setOf("ProjectInfoCache", "gigachatSessionCache", "telegramUpdateCache"))
    }

    private fun caffeine() =
        Caffeine.newBuilder()
            .expireAfterWrite(10, java.util.concurrent.TimeUnit.MINUTES)
            .maximumSize(200)
}
```

### Параметры кэша

- **TTL (Time To Live)**: 10 минут - данные автоматически удаляются через 10 минут после записи
- **Максимальный размер**: 200 записей - при превышении старые записи вытесняются
- **Провайдер**: Caffeine - высокопроизводительный кэш для Java

## Настроенные кэши

1. **ProjectInfoCache** - кэширование информации о проекте
2. **gigachatSessionCache** - кэширование сессий GigaChat
3. **telegramUpdateCache** - дедупликация обновлений Telegram
4. **telegramUserCache** - кэширование пользователей Telegram
5. **telegramStateCache** - кэширование состояний пользователей
6. **procurementsListCache** - кэширование списков закупок
7. **statsCache** - кэширование статистики пользователей
8. **helpMessageCache** - кэширование справочных сообщений

## Дедупликация обновлений Telegram

### Проблема

Telegram может отправлять дублирующиеся обновления с одинаковым `updateId`, что приводит к повторной обработке сообщений.

### Решение

Создан сервис `TelegramUpdateDeduplicationService` для предотвращения дублирования:

```kotlin
@Service
class TelegramUpdateDeduplicationService {
    
    @Cacheable(cacheNames = ["telegramUpdateCache"], key = "#updateId")
    fun isUpdateProcessed(updateId: Int): Boolean {
        return false // При первом вызове возвращает false и кэширует
    }

    @Cacheable(cacheNames = ["telegramUpdateCache"], key = "#updateId")
    fun markAsProcessed(updateId: Int): Boolean {
        return true // Кэширует true для обработанных updateId
    }
}
```

### Принцип работы

1. **Первая проверка**: `isUpdateProcessed(123)` → возвращает `false`, кэширует результат
2. **Отметка как обработанный**: `markAsProcessed(123)` → кэширует `true`
3. **Повторная проверка**: `isUpdateProcessed(123)` → возвращает `true` из кэша

### Интеграция в TelegramBot

```kotlin
override fun onUpdateReceived(update: Update) {
    val updateId = update.updateId
    
    // Проверяем через Spring Cache
    if (deduplicationService.isUpdateProcessed(updateId)) {
        logger.warn("Duplicate update received with id: $updateId, skipping")
        return
    }
    
    // Отмечаем как обработанный
    deduplicationService.markAsProcessed(updateId)
    
    // Обрабатываем update...
}
```

## Преимущества миграции на Spring Cache

### Было (ручное управление кэшем)

```kotlin
// Ручное управление ConcurrentHashMap
private val processedUpdates = ConcurrentHashMap<Int, Long>()
private val scheduler = Executors.newScheduledThreadPool(1)

@PostConstruct
fun initCleanupTask() {
    scheduler.scheduleAtFixedRate({
        cleanupOldUpdates() // Ручная очистка каждые 30 минут
    }, 30, 30, TimeUnit.MINUTES)
}
```

### Стало (Spring Cache)

```kotlin
// Простое использование аннотаций
@Cacheable(cacheNames = ["telegramUpdateCache"], key = "#updateId")
fun isUpdateProcessed(updateId: Int): Boolean = false
```

### Выгоды

1. **Автоматическое управление памятью** - Caffeine автоматически вытесняет старые записи
2. **Настраиваемый TTL** - данные автоматически удаляются через 10 минут
3. **Меньше кода** - убрали ~40 строк ручного управления кэшем
4. **Единообразие** - все кэши в проекте используют одну конфигурацию
5. **Производительность** - Caffeine оптимизирован для высокой нагрузки
6. **Мониторинг** - Spring Actuator может предоставить метрики кэша

## Мониторинг кэша

При включенном Spring Actuator можно получить статистику кэша:

```bash
GET /actuator/caches
GET /actuator/caches/telegramUpdateCache
```

## Настройка в application.properties

```properties
# Логирование кэша (опционально)
logging.level.org.springframework.cache=DEBUG
```

## Новые возможности кэширования

### Кэширование пользователей Telegram
```kotlin
@Cacheable(cacheNames = ["telegramUserCache"], key = "#telegramId")
fun getNewOrSavedUserByTelegramId(telegramId: Long): User
```

### Кэширование состояний пользователей
```kotlin
@Cacheable(cacheNames = ["telegramStateCache"], key = "#chatID")
fun getState(chatID: Long): SectionState
```

### Кэширование статистики
```kotlin
@Cacheable(cacheNames = ["statsCache"], key = "#chatId")
fun execute(chatId: Long, params: String): String
```

### Кэширование справочных сообщений
```kotlin
@Cacheable(cacheNames = ["helpMessageCache"])
private fun buildHelpMessage(): String
```

## Заключение

Система кэширования в Telegram боте значительно улучшена:
- Добавлено кэширование часто запрашиваемых данных
- Оптимизирована производительность обработки команд
- Снижена нагрузка на базу данных
- Обеспечено единообразное управление кэшированием

Все кэши используют единую конфигурацию с автоматическим TTL и ограничением по размеру.
