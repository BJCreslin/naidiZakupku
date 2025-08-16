# Telegram Bot Inline Buttons - Документация для разработчиков

## Обзор

Система inline кнопок для навигации по закупкам в Telegram боте предоставляет интуитивный интерфейс для пользователей с возможностью пагинации, фильтрации и детального просмотра закупок.

## Архитектура

### Основные компоненты

1. **ProcurementsListBotService** - основной сервис для отображения списка закупок с кнопками
2. **CallbackEventImpl** - обработчик всех типов callback'ов от inline кнопок
3. **MessageEventImpl** - обработчик команды `/list` с интеграцией кнопок
4. **TelegramBot** - основной класс бота с поддержкой Markdown

### Диаграмма взаимодействия

```
User → /list → MessageEventImpl → ProcurementsListBotService → SendMessage with InlineKeyboard
User → Click Button → CallbackEventImpl → Handle Callback → Send Response
```

## Типы Callback'ов

### Структура callback данных

Все callback данные имеют префиксную структуру для легкой идентификации:

| Префикс | Описание | Пример |
|---------|----------|---------|
| `page_` | Навигация по страницам | `page_0`, `page_1` |
| `procurement_` | Детальный просмотр закупки | `procurement_123` |
| `filter_` | Применение фильтров | `filter_price`, `filter_date` |
| `refresh` | Обновление списка | `refresh` |
| `back_to_list` | Возврат к списку | `back_to_list` |
| `delete_procurement_` | Удаление закупки | `delete_procurement_123` |

### Обработка callback'ов

```kotlin
when {
    callbackData.startsWith("procurement_") -> handleProcurementCallback(callbackData, chatId)
    callbackData.startsWith("filter_") -> handleFilterCallback(callbackData, chatId)
    callbackData.startsWith("page_") -> handlePaginationCallback(callbackData, chatId)
    callbackData == "refresh" -> handleRefreshCallback(chatId)
    callbackData == "back_to_list" -> handleBackToListCallback(chatId)
    callbackData.startsWith("delete_procurement_") -> handleDeleteProcurementCallback(callbackData, chatId)
}
```

## Конфигурация

### Параметры пагинации

```kotlin
companion object {
    private const val ITEMS_PER_PAGE = 5  // Количество закупок на страницу
}
```

### Форматирование сообщений

Все сообщения используют Markdown для красивого отображения:
- `enableMarkdown(true)` - включение Markdown
- Эмодзи для визуального разделения
- Ссылки в формате `[текст](url)`

## API Reference

### ProcurementsListBotService

#### Основные методы

```kotlin
// Отображение списка с пагинацией и кнопками
fun executeWithPagination(chatId: Long, page: Int = 0): SendMessage

// Форматирование списка закупок с пагинацией
private fun formatProcurementsWithPagination(procurements: List<Procurement>, page: Int): String

// Создание комбинированной клавиатуры
private fun createCombinedKeyboard(totalItems: Int, currentPage: Int, pageProcurements: List<Procurement>, startIndex: Int): InlineKeyboardMarkup
```

#### Структура клавиатуры

```kotlin
val keyboard = mutableListOf<MutableList<InlineKeyboardButton>>()

// 1. Навигационные кнопки (если есть пагинация)
if (totalItems > ITEMS_PER_PAGE) {
    // Кнопки "Предыдущая" и "Следующая"
}

// 2. Кнопки закупок (по одной на строку)
pageProcurements.forEachIndexed { index, procurement ->
    // Кнопка для каждой закупки
}

// 3. Кнопки фильтров (в одной строке)
// "🔍 По цене" | "📅 По дате"

// 4. Кнопка обновления
// "🔄 Обновить"
```

### CallbackEventImpl

#### Методы обработки

```kotlin
// Обработка просмотра закупки
private fun handleProcurementCallback(callbackData: String, chatId: Long): SendMessage

// Обработка фильтров
private fun handleFilterCallback(callbackData: String, chatId: Long): SendMessage

// Обработка пагинации
private fun handlePaginationCallback(callbackData: String, chatId: Long): SendMessage

// Обработка обновления
private fun handleRefreshCallback(chatId: Long): SendMessage

// Обработка возврата к списку
private fun handleBackToListCallback(chatId: Long): SendMessage

// Обработка удаления закупки
private fun handleDeleteProcurementCallback(callbackData: String, chatId: Long): SendMessage
```

## Примеры использования

### Добавление новой кнопки

1. **Создайте callback обработчик:**

```kotlin
private fun handleNewFeatureCallback(callbackData: String, chatId: Long): SendMessage {
    val message = SendMessage()
    message.chatId = chatId.toString()
    message.text = "Новая функция"
    message.enableMarkdown(true)
    return message
}
```

2. **Добавьте обработку в основной метод:**

```kotlin
when {
    // ... существующие обработчики
    callbackData.startsWith("new_feature_") -> {
        handleNewFeatureCallback(callbackData, chatId)
    }
}
```

3. **Создайте кнопку в клавиатуре:**

```kotlin
val newButton = InlineKeyboardButton()
newButton.text = "🆕 Новая функция"
newButton.callbackData = "new_feature_action"
keyboard.add(mutableListOf(newButton))
```

### Расширение функциональности фильтров

```kotlin
private fun handleFilterCallback(callbackData: String, chatId: Long): SendMessage {
    val filterType = callbackData.removePrefix("filter_")
    val filterText = when (filterType) {
        "price" -> "по цене"
        "date" -> "по дате"
        "publisher" -> "по заказчику"  // Новый фильтр
        else -> filterType
    }
    
    // Применение фильтра
    val filteredProcurements = applyFilter(procurements, filterType)
    
    return procurementsListService.executeWithPagination(chatId, 0, filteredProcurements)
}
```

## Обработка ошибок

### Типичные ошибки и их решения

1. **Callback data слишком длинный**
   - Telegram ограничивает callback data 64 байтами
   - Используйте короткие префиксы и ID

2. **Клавиатура не отображается**
   - Проверьте `enableMarkdown(true)`
   - Убедитесь, что `replyMarkup` установлен

3. **Кнопки не работают**
   - Проверьте обработку в `CallbackEventImpl`
   - Убедитесь в правильности `callbackData`

### Логирование

```kotlin
logger.info("Processing callback from user $username (chatId: $chatId): $callbackData")
logger.warn("Unknown callback data: $callbackData from user $username")
logger.error("Error processing callback from user $username: ${e.message}", e)
```

## Тестирование

### Unit тесты

```kotlin
@Test
fun `should create navigation keyboard with pagination`() {
    val service = ProcurementsListBotService(mockUserService, mockProcurementService)
    val keyboard = service.createCombinedKeyboard(10, 0, procurements, 0)
    
    assertThat(keyboard.keyboard).hasSize(4) // nav + items + filters + refresh
    assertThat(keyboard.keyboard[0]).hasSize(1) // next button only
}

@Test
fun `should handle procurement callback correctly`() {
    val callbackEvent = CallbackEventImpl(mockProcurementService)
    val message = callbackEvent.handleProcurementCallback("procurement_123", 456L)
    
    assertThat(message.text).contains("Детали закупки #123")
    assertThat(message.replyMarkup).isNotNull()
}
```

### Интеграционные тесты

```kotlin
@Test
fun `should process list command with buttons`() {
    // Given
    val update = createUpdateWithMessage("/list")
    
    // When
    val response = messageEvent.action(update)
    
    // Then
    assertThat(response.replyMarkup).isInstanceOf(InlineKeyboardMarkup::class.java)
    assertThat(response.text).contains("Список закупок")
}
```

## Производительность

### Оптимизации

1. **Кэширование пользователей**
   - `telegramUserService.getNewOrSavedUserByTelegramId()` кэшируется
   - Избегайте повторных запросов к БД

2. **Ограничение количества элементов**
   - `ITEMS_PER_PAGE = 5` для оптимального отображения
   - Пагинация для больших списков

3. **Ленивая загрузка**
   - Закупки загружаются только при необходимости
   - Используйте `FetchType.LAZY` в JPA

### Мониторинг

```kotlin
// Логирование времени обработки
val startTime = LocalDateTime.now()
// ... обработка
middleware.logResponse(update, response.text, startTime)
```

## Безопасность

### Валидация данных

```kotlin
// Проверка ID закупки
val procurementId = callbackData.removePrefix("procurement_").toLongOrNull()
if (procurementId == null) {
    return SendMessage().apply {
        chatId = chatId.toString()
        text = "❌ Ошибка: неверный ID закупки"
        enableMarkdown(true)
    }
}
```

### Проверка прав доступа

```kotlin
// Проверка принадлежности закупки пользователю
val user = telegramUserService.getNewOrSavedUserByTelegramId(chatId)
val procurement = procurementService.getById(procurementId)
if (procurement?.users?.contains(user) != true) {
    return SendMessage().apply {
        chatId = chatId.toString()
        text = "❌ Доступ запрещен"
        enableMarkdown(true)
    }
}
```

## Будущие улучшения

### Планируемые функции

1. **Расширенные фильтры**
   - Фильтр по диапазону цен
   - Фильтр по дате размещения
   - Фильтр по заказчику

2. **Сортировка**
   - По цене (возрастание/убывание)
   - По дате
   - По названию

3. **Экспорт данных**
   - Экспорт в Excel
   - Экспорт в PDF
   - Отправка по email

4. **Уведомления**
   - Уведомления о новых закупках
   - Уведомления об изменениях
   - Напоминания о дедлайнах

### Архитектурные улучшения

1. **Кэширование результатов**
   - Redis для кэширования списков
   - Инвалидация кэша при изменениях

2. **Асинхронная обработка**
   - Обработка больших списков в фоне
   - Прогресс-бары для длительных операций

3. **Микросервисная архитектура**
   - Выделение сервиса закупок
   - API Gateway для внешних интеграций

## Заключение

Система inline кнопок предоставляет мощный и удобный интерфейс для навигации по закупкам. Архитектура позволяет легко расширять функциональность и добавлять новые возможности.

Для получения дополнительной информации обращайтесь к исходному коду или создавайте issues в репозитории проекта.
