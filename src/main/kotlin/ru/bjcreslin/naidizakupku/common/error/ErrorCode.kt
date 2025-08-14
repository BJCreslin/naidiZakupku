package ru.bjcreslin.naidizakupku.common.error

enum class ErrorCode(val code: String, val defaultMessage: String) {
    // Authentication & Authorization
    UNAUTHORIZED("AUTH001", "Доступ запрещен"),
    INVALID_TOKEN("AUTH002", "Недействительный токен"),
    TOKEN_EXPIRED("AUTH003", "Срок действия токена истек"),
    FORBIDDEN("AUTH004", "Недостаточно прав доступа"),
    
    // Resources
    RESOURCE_NOT_FOUND("RES001", "Ресурс не найден"),
    USER_NOT_FOUND("RES002", "Пользователь не найден"),
    PROCUREMENT_NOT_FOUND("RES003", "Закупка не найдена"),
    
    // Validation
    VALIDATION_ERROR("VAL001", "Ошибка валидации данных"),
    INVALID_INPUT("VAL002", "Некорректные входные данные"),
    REQUIRED_FIELD_MISSING("VAL003", "Отсутствует обязательное поле"),
    
    // Telegram Bot
    TELEGRAM_SEND_ERROR("TG001", "Ошибка отправки сообщения в Telegram"),
    TELEGRAM_USER_REQUIRED("TG002", "Требуется Telegram ID пользователя"),
    
    // External Services
    EXTERNAL_SERVICE_ERROR("EXT001", "Ошибка внешнего сервиса"),
    GIGACHAT_ERROR("EXT002", "Ошибка сервиса GigaChat"),
    
    // System
    INTERNAL_SERVER_ERROR("SYS001", "Внутренняя ошибка сервера"),
    DATABASE_ERROR("SYS002", "Ошибка базы данных"),
    CONFIGURATION_ERROR("SYS003", "Ошибка конфигурации"),
    
    // Business Logic
    BUSINESS_LOGIC_ERROR("BIZ001", "Ошибка бизнес-логики"),
    OPERATION_NOT_ALLOWED("BIZ002", "Операция не разрешена")
}
