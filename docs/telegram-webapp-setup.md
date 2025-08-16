# Настройка Telegram Web App для авторизации

## Обзор

Система поддерживает гибридную авторизацию через Telegram бота с использованием Web App и Login Widget.

## Компоненты

### 1. Сущности базы данных

- **UserSession** - хранение сессий пользователей
- **User** - пользователи системы (расширена связь с Telegram)
- **TelegramUser** - связь пользователей с Telegram ID

### 2. API Endpoints

- `POST /api/auth/telegram/validate` - валидация данных от Telegram
- `POST /api/auth/telegram/webapp` - обработка Web App данных
- `GET /api/auth/telegram/session/{sessionId}` - проверка сессии
- `DELETE /api/auth/telegram/logout` - выход из системы
- `DELETE /api/auth/telegram/logout/all` - выход из всех сессий

### 3. Безопасность

- Валидация хеша от Telegram (HMAC-SHA-256)
- JWT токены с expiration
- Rate limiting (10 запросов в минуту)
- Интеграция с существующими ролями

## Настройка BotFather

### 1. Создание бота

```bash
# Отправьте команду @BotFather
/newbot

# Укажите имя и username бота
# Получите токен бота
```

### 2. Настройка Web App

```bash
# Отправьте команду @BotFather
/setmenubutton

# Выберите вашего бота
# Укажите URL Web App
https://naidizakupku.ru/auth

# Или используйте команду
/setcommands

# Добавьте команды:
start - Открыть веб-приложение
help - Помощь
code - Получить код для расширения
```

### 3. Настройка команд

```bash
/setcommands

# Добавьте команды:
start - Открыть веб-приложение
help - Помощь и инструкции
code - Получить код для браузерного расширения
procurements - Список закупок
stats - Статистика
gigachat - AI-ассистент
```

## Переменные окружения

```bash
# Telegram Bot
NAIDI_ZAKUPKU_TELEGRAM_BOT_TOKEN=your_bot_token
NAIDI_ZAKUPKU_TELEGRAM_BOT_NAME=your_bot_name

# Web App
WEBAPP_BASE_URL=https://naidizakupku.ru
WEBAPP_ALLOWED_ORIGINS=https://naidizakupku.ru,http://localhost:3000

# JWT
NAIDI_ZAKUPKU_JWT_SECRET_CODE=your_jwt_secret
NAIDI_ZAKUPKU_JWT_TOKEN_EXPIRED=1800000
```

## Использование

### 1. В Telegram боте

Пользователь отправляет `/start` и получает кнопку "Открыть приложение".

### 2. В Web App

```javascript
// Получение данных от Telegram
const initData = window.Telegram.WebApp.initData;

// Отправка на сервер для валидации
fetch('/api/auth/telegram/validate', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify(initData)
})
.then(response => response.json())
.then(data => {
    if (data.success) {
        // Сохраняем токен и sessionId
        localStorage.setItem('jwt_token', data.token);
        localStorage.setItem('session_id', data.sessionId);
    }
});
```

### 3. Проверка сессии

```javascript
// Проверка активной сессии
fetch(`/api/auth/telegram/session/${sessionId}`)
.then(response => response.json())
.then(data => {
    if (data.valid) {
        // Пользователь авторизован
        console.log('User:', data.user);
    }
});
```

## Безопасность

### 1. Валидация данных Telegram

Система проверяет:
- Подлинность данных через HMAC-SHA-256
- Актуальность данных (не старше 24 часов)
- Корректность хеша

### 2. Rate Limiting

- 10 запросов в минуту на auth endpoints
- Отслеживание по IP адресу
- Автоматическая очистка старых записей

### 3. Сессии

- Уникальный sessionId для каждой сессии
- Автоматическое истечение через 24 часа
- Возможность принудительного выхода

## Мониторинг

### 1. Логи

```bash
# Просмотр логов авторизации
tail -f logs/spring-boot-application.log | grep "telegram"
```

### 2. Метрики

- Количество успешных авторизаций
- Количество неудачных попыток
- Время обработки запросов
- Количество активных сессий

### 3. Health Check

```bash
# Проверка состояния сервиса
curl http://localhost:9000/api/health
```

## Troubleshooting

### 1. Ошибка валидации

- Проверьте токен бота
- Убедитесь в корректности URL Web App
- Проверьте настройки CORS

### 2. Rate Limiting

- Увеличьте лимиты в конфигурации
- Проверьте IP адреса клиентов
- Настройте прокси если необходимо

### 3. Проблемы с сессиями

- Проверьте настройки базы данных
- Убедитесь в корректности JWT секрета
- Проверьте время на сервере

## Разработка

### 1. Локальная разработка

```bash
# Запуск с локальными настройками
export WEBAPP_BASE_URL=http://localhost:3000
export WEBAPP_ALLOWED_ORIGINS=http://localhost:3000
./gradlew bootRun
```

### 2. Тестирование

```bash
# Запуск тестов
./gradlew test

# Тестирование API
curl -X POST http://localhost:9000/api/auth/telegram/validate \
  -H "Content-Type: application/json" \
  -d '{"id":123,"first_name":"Test","auth_date":1640995200,"hash":"test_hash"}'
```

### 3. Отладка

```bash
# Включение debug логов
export LOGGING_LEVEL_ROOT=DEBUG
export LOGGING_LEVEL_RU_BJCRESLIN=DEBUG
```
