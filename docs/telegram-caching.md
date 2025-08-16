# Кэширование часто запрашиваемых данных в Telegram боте

## Обзор

В Telegram боте реализована система кэширования для оптимизации производительности и снижения нагрузки на базу данных. Кэширование применяется к часто запрашиваемым данным, которые редко изменяются.

## Архитектура кэширования

### Конфигурация кэша

```kotlin
@Configuration
@EnableCaching
class CacheConfiguration {
    @Bean
    fun cacheManager(): CacheManager = CaffeineCacheManager().apply { 
        setCaffeine(caffeine())
        setCacheNames(setOf(
            "ProjectInfoCache", 
            "gigachatSessionCache", 
            "telegramUpdateCache",
            "telegramUserCache",
            "telegramStateCache",
            "procurementsListCache",
            "statsCache",
            "helpMessageCache"
        ))
    }

    private fun caffeine() =
        Caffeine.newBuilder()
            .expireAfterWrite(10, java.util.concurrent.TimeUnit.MINUTES)
            .maximumSize(200)
            .recordStats()
}
```

### Параметры кэша

- **TTL**: 10 минут - автоматическое удаление старых записей
- **Максимальный размер**: 200 записей - ограничение по памяти
- **Провайдер**: Caffeine - высокопроизводительный in-memory кэш
- **Статистика**: Включена для мониторинга

## Типы кэшируемых данных

### 1. Дедупликация обновлений Telegram

**Кэш**: `telegramUpdateCache`

**Назначение**: Предотвращение повторной обработки дублирующихся обновлений от Telegram API.

```kotlin
@Service
class TelegramUpdateDeduplicationService {
    
    @Cacheable(cacheNames = ["telegramUpdateCache"], key = "#updateId")
    fun isUpdateProcessed(updateId: Int): Boolean = false

    @Cacheable(cacheNames = ["telegramUpdateCache"], key = "#updateId")
    fun markAsProcessed(updateId: Int): Boolean = true
}
```

**Принцип работы**:
1. При получении update проверяется его ID в кэше
2. Если ID уже есть - update игнорируется
3. Если ID новый - обрабатывается и сохраняется в кэше

### 2. Кэширование пользователей Telegram

**Кэш**: `telegramUserCache`

**Назначение**: Кэширование данных пользователей для быстрого доступа.

```kotlin
@Service
class TelegramUserServiceImpl(
    private val repository: TelegramUserRepository
) : TelegramUserService {

    @Cacheable(cacheNames = ["telegramUserCache"], key = "#telegramId")
    override fun getNewOrSavedUserByTelegramId(telegramId: Long): TelegramUser {
        return repository.findByTelegramId(telegramId) 
            ?: repository.save(TelegramUser(telegramId = telegramId))
    }

    @CacheEvict(cacheNames = ["telegramUserCache"], key = "#user.telegramId")
    override fun saveUser(user: TelegramUser): TelegramUser {
        return repository.save(user)
    }
}
```

### 3. Кэширование состояний пользователей

**Кэш**: `telegramStateCache`

**Назначение**: Кэширование текущих состояний пользователей для быстрого определения контекста.

```kotlin
@Service
class TelegramStateServiceImpl(
    private val repository: TelegramSectionUserRepository
) : TelegramStateService {

    @Cacheable(cacheNames = ["telegramStateCache"], key = "#chatID")
    override fun getState(chatID: Long): SectionState {
        val telegramState = repository.findByTelegramId(chatID)
        return telegramState?.sectionState ?: SectionState.ROOT
    }

    @CacheEvict(cacheNames = ["telegramStateCache"], key = "#chatID")
    override fun setState(chatID: Long, sectionState: SectionState) {
        // Логика сохранения состояния
    }
}
```

### 4. Кэширование списков закупок

**Кэш**: `procurementsListCache`

**Назначение**: Кэширование списков закупок пользователей для быстрого отображения.

```kotlin
@Service
class ProcurementsListBotService(
    private val telegramUserService: TelegramUserService,
    private val procurementService: ProcurementService
) : CommandHandler {

    @Cacheable(
        cacheNames = ["procurementsListCache"], 
        key = "#chatId + '_' + #page"
    )
    fun getProcurementsForPage(chatId: Long, page: Int): List<Procurement> {
        val user = telegramUserService.getNewOrSavedUserByTelegramId(chatId)
        val allProcurements = procurementService.getAllProcurementsForTelegram(user)
        return paginateProcurements(allProcurements, page)
    }

    @CacheEvict(cacheNames = ["procurementsListCache"], allEntries = true)
    fun invalidateProcurementsCache() {
        // Вызывается при изменении закупок
    }
}
```

### 5. Кэширование статистики

**Кэш**: `statsCache`

**Назначение**: Кэширование статистических данных для быстрого отображения.

```kotlin
@Service
class StatsBotService(
    private val telegramUserService: TelegramUserService,
    private val procurementService: ProcurementService
) : CommandHandler {

    @Cacheable(cacheNames = ["statsCache"], key = "#chatId")
    fun getStatsForUser(chatId: Long): String {
        val user = telegramUserService.getNewOrSavedUserByTelegramId(chatId)
        val procurements = procurementService.getAllProcurementsForTelegram(user)
        return buildStatsMessage(procurements)
    }

    @CacheEvict(cacheNames = ["statsCache"], key = "#chatId")
    fun invalidateUserStats(chatId: Long) {
        // Вызывается при изменении закупок пользователя
    }
}
```

### 6. Кэширование справочных сообщений

**Кэш**: `helpMessageCache`

**Назначение**: Кэширование статических справочных сообщений.

```kotlin
@Service
class HelpBotService : CommandHandler {

    @Cacheable(cacheNames = ["helpMessageCache"])
    fun getHelpMessage(): String {
        return buildHelpMessage()
    }
}
```

## Стратегии инвалидации кэша

### 1. TTL (Time To Live)
- Автоматическое удаление через 10 минут
- Подходит для данных, которые могут устареть

### 2. Размер кэша
- Максимум 200 записей
- LRU (Least Recently Used) вытеснение

### 3. Ручная инвалидация
```kotlin
@CacheEvict(cacheNames = ["procurementsListCache"], allEntries = true)
fun invalidateAllProcurementsCache()

@CacheEvict(cacheNames = ["statsCache"], key = "#chatId")
fun invalidateUserStats(chatId: Long)
```

### 4. Условная инвалидация
```kotlin
@CacheEvict(
    cacheNames = ["procurementsListCache"], 
    key = "#chatId",
    condition = "#result != null"
)
fun updateProcurement(chatId: Long, procurement: Procurement): Procurement?
```

## Мониторинг кэша

### Метрики Prometheus

```promql
# Hit rate для всех кэшей
cache_gets_total{result="hit"} / cache_gets_total

# Размер кэшей
cache_size

# Количество evictions
cache_evictions_total

# Операции в секунду
rate(cache_gets_total[1m])
rate(cache_puts_total[1m])
```

### Endpoints для мониторинга

```bash
# Общая информация о кэшах
GET /actuator/caches

# Детали конкретного кэша
GET /actuator/caches/telegramUserCache

# Метрики кэша
GET /actuator/metrics/cache.size?tag=cache:telegramUserCache
```

## Оптимизация производительности

### 1. Выбор ключей кэширования

```kotlin
// Хорошо - простой ключ
@Cacheable(cacheNames = ["userCache"], key = "#chatId")

// Плохо - сложный ключ
@Cacheable(cacheNames = ["userCache"], key = "#user.telegramId + '_' + #user.username + '_' + #user.createdAt")
```

### 2. Условное кэширование

```kotlin
@Cacheable(
    cacheNames = ["procurementsCache"], 
    key = "#chatId",
    condition = "#result.size() > 0"
)
fun getProcurements(chatId: Long): List<Procurement>
```

### 3. Асинхронное обновление кэша

```kotlin
@Async
@CacheEvict(cacheNames = ["statsCache"], key = "#chatId")
fun updateStatsAsync(chatId: Long) {
    // Обновление статистики в фоне
}
```

## Troubleshooting

### Проблема: Низкий hit rate

**Причины**:
- Слишком короткий TTL
- Неправильные ключи кэширования
- Частая инвалидация

**Решение**:
```kotlin
// Увеличить TTL для статических данных
@Cacheable(
    cacheNames = ["helpMessageCache"],
    key = "'static_help'"
)
```

### Проблема: Высокое потребление памяти

**Причины**:
- Слишком большой размер кэша
- Неэффективные ключи

**Решение**:
```kotlin
// Ограничить размер кэша
@Cacheable(
    cacheNames = ["largeDataCache"],
    key = "#chatId"
)
```

### Проблема: Устаревшие данные

**Причины**:
- Слишком длинный TTL
- Отсутствие инвалидации

**Решение**:
```kotlin
// Добавить инвалидацию при изменениях
@CacheEvict(cacheNames = ["userCache"], key = "#user.telegramId")
fun updateUser(user: TelegramUser)
```

## Рекомендации по использованию

### 1. Кэшируйте только часто запрашиваемые данные
- Статистика пользователей
- Списки закупок
- Справочная информация

### 2. Не кэшируйте
- Критически важные данные
- Часто изменяемые данные
- Персональные данные

### 3. Мониторьте производительность
- Отслеживайте hit rate
- Контролируйте размер кэша
- Анализируйте evictions

### 4. Настройте алерты
```promql
# Алерт на низкий hit rate
cache_gets_total{result="miss"} / cache_gets_total > 0.8

# Алерт на превышение размера
cache_size > 180
```

## Заключение

Кэширование в Telegram боте значительно улучшает производительность за счет:
- Снижения нагрузки на базу данных
- Ускорения ответов пользователям
- Оптимизации использования ресурсов

Правильная настройка кэширования требует баланса между производительностью и актуальностью данных.
