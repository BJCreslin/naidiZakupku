# Централизованная система обработки ошибок

## Обзор

В проекте NaidiZakupku реализована централизованная система обработки ошибок, которая обеспечивает единообразное форматирование и обработку всех типов ошибок в приложении.

## Архитектура

### Основные компоненты

1. **ErrorCode** - перечисление всех кодов ошибок
2. **ErrorFactory** - фабрика для создания стандартизированных ответов об ошибках
3. **ErrorResponse/ValidationErrorResponse** - DTO для ответов об ошибках
4. **GlobalExceptionHandler** - глобальный обработчик исключений
5. **ErrorCodeException** - специальное исключение с кодом ошибки

## Использование

### 1. Коды ошибок (ErrorCode)

Все ошибки в системе имеют уникальные коды и сообщения по умолчанию:

```kotlin
enum class ErrorCode(val code: String, val defaultMessage: String) {
    // Authentication & Authorization
    UNAUTHORIZED("AUTH001", "Доступ запрещен"),
    INVALID_TOKEN("AUTH002", "Недействительный токен"),
    TOKEN_EXPIRED("AUTH003", "Срок действия токена истек"),
    
    // Resources
    RESOURCE_NOT_FOUND("RES001", "Ресурс не найден"),
    USER_NOT_FOUND("RES002", "Пользователь не найден"),
    
    // Validation
    VALIDATION_ERROR("VAL001", "Ошибка валидации данных"),
    
    // Telegram Bot
    TELEGRAM_SEND_ERROR("TG001", "Ошибка отправки сообщения в Telegram"),
    
    // System
    INTERNAL_SERVER_ERROR("SYS001", "Внутренняя ошибка сервера"),
    
    // Business Logic
    BUSINESS_LOGIC_ERROR("BIZ001", "Ошибка бизнес-логики")
}
```

### 2. ErrorFactory - централизованное создание ошибок

#### В сервисах:

```kotlin
@Service
class MyService(
    private val errorFactory: ErrorFactory
) {
    
    fun findUser(id: Long): User {
        return userRepository.findById(id)
            ?: throw errorFactory.createException(
                ErrorCode.USER_NOT_FOUND,
                "Пользователь с ID $id не найден"
            )
    }
}
```

#### В контроллерах (если нужно вернуть ResponseEntity):

```kotlin
@RestController
class MyController(
    private val errorFactory: ErrorFactory
) {
    
    @GetMapping("/users/{id}")
    fun getUser(@PathVariable id: Long): ResponseEntity<*> {
        return try {
            val user = userService.findUser(id)
            ResponseEntity.ok(user)
        } catch (e: ErrorCodeException) {
            errorFactory.createErrorFromException(e, e.errorCode, HttpStatus.NOT_FOUND)
        }
    }
}
```

### 3. Стандартные методы ErrorFactory

```kotlin
// Основной метод создания ошибок
errorFactory.createErrorResponse(
    errorCode = ErrorCode.USER_NOT_FOUND,
    customMessage = "Кастомное сообщение",
    details = "Дополнительные детали",
    httpStatus = HttpStatus.NOT_FOUND
)

// Ошибки валидации
errorFactory.createValidationErrorResponse(
    fieldErrors = mapOf("email" to "Некорректный формат email")
)

// Специализированные методы
errorFactory.createNotFoundError("Пользователь", userId)
errorFactory.createUnauthorizedError("Токен истек")
errorFactory.createForbiddenError("Недостаточно прав")
errorFactory.createBadRequestError("Некорректные параметры")

// Создание исключений для сервисов
throw errorFactory.createException(ErrorCode.USER_NOT_FOUND, "Детали ошибки")
```

## Формат ответов

### Стандартный ответ об ошибке:

```json
{
    "code": "RES002",
    "message": "Пользователь не найден",
    "details": "Пользователь с ID 123 не найден",
    "path": "/api/users/123",
    "timestamp": "2024-01-15 14:30:25",
    "traceId": "a1b2c3d4"
}
```

### Ответ об ошибке валидации:

```json
{
    "code": "VAL001",
    "message": "Ошибка валидации данных",
    "fieldErrors": {
        "email": "Некорректный формат email",
        "age": "Возраст должен быть больше 0"
    },
    "path": "/api/users",
    "timestamp": "2024-01-15 14:30:25",
    "traceId": "a1b2c3d4"
}
```

## Категории кодов ошибок

| Префикс | Категория | Описание |
|---------|-----------|----------|
| AUTH | Authentication & Authorization | Ошибки аутентификации и авторизации |
| RES | Resources | Ошибки поиска ресурсов |
| VAL | Validation | Ошибки валидации данных |
| TG | Telegram Bot | Ошибки Telegram бота |
| EXT | External Services | Ошибки внешних сервисов |
| SYS | System | Системные ошибки |
| BIZ | Business Logic | Ошибки бизнес-логики |

## Логирование

ErrorFactory автоматически логирует ошибки с соответствующим уровнем:

- **ERROR** - для серверных ошибок (5xx)
- **WARN** - для клиентских ошибок (4xx)
- **INFO** - для остальных случаев

Каждая ошибка содержит:
- Код ошибки
- Сообщение
- TraceId для трассировки
- Path запроса (если доступен)

## Трассировка ошибок

Система автоматически генерирует traceId для каждой ошибки:
1. Использует MDC.get("traceId") если доступен
2. Генерирует случайный 8-символьный ID

## Миграция существующих ошибок

### Было:
```kotlin
throw BadRequestException("User not found")
throw RuntimeException("Something went wrong")
```

### Стало:
```kotlin
throw errorFactory.createException(ErrorCode.USER_NOT_FOUND, "User not found")
throw errorFactory.createException(ErrorCode.INTERNAL_SERVER_ERROR, "Something went wrong")
```

## Добавление новых типов ошибок

1. Добавить код в `ErrorCode` enum:
```kotlin
NEW_ERROR("CAT001", "Описание ошибки")
```

2. При необходимости добавить обработчик в `GlobalExceptionHandler`:
```kotlin
@ExceptionHandler(CustomException::class)
fun handleCustomException(ex: CustomException): ResponseEntity<ErrorResponse> {
    return errorFactory.createErrorResponse(
        errorCode = ErrorCode.NEW_ERROR,
        details = ex.message,
        httpStatus = HttpStatus.BAD_REQUEST
    )
}
```

## Лучшие практики

1. **Используйте ErrorFactory** во всех сервисах для создания исключений
2. **Выбирайте подходящие коды ошибок** из существующих или добавляйте новые
3. **Предоставляйте детальные сообщения** для упрощения отладки
4. **Не дублируйте логику обработки** - используйте GlobalExceptionHandler
5. **Логируйте контекст** - используйте traceId для связывания логов
6. **Тестируйте обработку ошибок** в unit и integration тестах

## Примеры использования

### В сервисе аутентификации:
```kotlin
fun validateToken(token: String): Boolean {
    if (token.isBlank()) {
        throw errorFactory.createException(ErrorCode.INVALID_TOKEN, "Токен не может быть пустым")
    }
    
    if (isExpired(token)) {
        throw errorFactory.createException(ErrorCode.TOKEN_EXPIRED, "Срок действия токена истек")
    }
    
    return true
}
```

### В Telegram боте:
```kotlin
fun sendMessage(chatId: String, text: String) {
    try {
        telegramApi.sendMessage(chatId, text)
    } catch (e: TelegramApiException) {
        throw errorFactory.createException(
            ErrorCode.TELEGRAM_SEND_ERROR,
            "Не удалось отправить сообщение пользователю $chatId"
        )
    }
}
```

### В бизнес-логике:
```kotlin
fun processProcurement(procurementId: Long) {
    val procurement = findProcurement(procurementId)
    
    if (procurement.status == ProcurementStatus.CLOSED) {
        throw errorFactory.createException(
            ErrorCode.OPERATION_NOT_ALLOWED,
            "Нельзя обработать закрытую закупку"
        )
    }
    
    // ... обработка
}
```

## Заключение

Централизованная система обработки ошибок обеспечивает:
- Единообразие ответов API
- Упрощенное логирование и отладку
- Лучший пользовательский опыт
- Простоту сопровождения кода
- Возможность трассировки ошибок

Используйте ErrorFactory и стандартные коды ошибок во всех новых разработках и постепенно мигрируйте существующий код.
