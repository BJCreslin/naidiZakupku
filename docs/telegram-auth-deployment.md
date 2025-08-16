# Развертывание Telegram авторизации

## Быстрый старт

### 1. Подготовка окружения

```bash
# Клонирование репозитория
git clone <repository-url>
cd naidiZakupku

# Установка зависимостей
./gradlew build
```

### 2. Настройка переменных окружения

Создайте файл `.env` в корне проекта:

```bash
# Telegram Bot
NAIDI_ZAKUPKU_TELEGRAM_BOT_TOKEN=your_bot_token_here
NAIDI_ZAKUPKU_TELEGRAM_BOT_NAME=your_bot_name

# Web App
WEBAPP_BASE_URL=https://your-domain.com
WEBAPP_ALLOWED_ORIGINS=https://your-domain.com,http://localhost:3000

# JWT
NAIDI_ZAKUPKU_JWT_SECRET_CODE=your_secure_jwt_secret_here
NAIDI_ZAKUPKU_JWT_TOKEN_EXPIRED=1800000

# Database
NAIDI_ZAKUPKU_USER_DB=your_db_user
NAIDI_ZAKUPKU_PASSWORD_DB=your_db_password
```

### 3. Настройка базы данных

```bash
# Запуск PostgreSQL (если используете Docker)
docker run -d \
  --name postgres-naidi \
  -e POSTGRES_DB=zakupdb \
  -e POSTGRES_USER=bduser \
  -e POSTGRES_PASSWORD=bdPassword \
  -p 5432:5432 \
  postgres:15

# Применение миграций
./gradlew bootRun
```

### 4. Настройка Telegram бота

1. Откройте @BotFather в Telegram
2. Создайте нового бота: `/newbot`
3. Получите токен и username
4. Настройте Web App URL: `/setmenubutton`
5. Укажите URL: `https://your-domain.com/auth`

### 5. Запуск приложения

```bash
# Разработка
./gradlew bootRun

# Продакшн
./gradlew bootJar
java -jar build/libs/myapp.jar
```

## Проверка работоспособности

### 1. Тест API endpoints

```bash
# Проверка здоровья сервиса
curl http://localhost:9000/api/health

# Тест валидации Telegram данных
curl -X POST http://localhost:9000/api/auth/telegram/validate \
  -H "Content-Type: application/json" \
  -d '{
    "id": 123456789,
    "first_name": "Test",
    "auth_date": 1640995200,
    "hash": "test_hash"
  }'
```

### 2. Тест Web App

1. Откройте Telegram
2. Найдите вашего бота
3. Отправьте команду `/start`
4. Нажмите кнопку "Открыть приложение"
5. Проверьте авторизацию

### 3. Проверка логов

```bash
# Просмотр логов приложения
tail -f logs/spring-boot-application.log

# Фильтрация по Telegram
tail -f logs/spring-boot-application.log | grep -i telegram

# Фильтрация по авторизации
tail -f logs/spring-boot-application.log | grep -i auth
```

## Конфигурация для продакшна

### 1. Nginx конфигурация

```nginx
server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;

    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;

    location / {
        proxy_pass http://localhost:9000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /api/auth/telegram/ {
        proxy_pass http://localhost:9000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Rate limiting
        limit_req zone=telegram_auth burst=5 nodelay;
    }
}

# Rate limiting
limit_req_zone $binary_remote_addr zone=telegram_auth:10m rate=10r/m;
```

### 2. Systemd сервис

Создайте файл `/etc/systemd/system/naidizakupku.service`:

```ini
[Unit]
Description=Naidi Zakupku Application
After=network.target

[Service]
Type=simple
User=naidizakupku
WorkingDirectory=/opt/naidizakupku
ExecStart=/usr/bin/java -jar /opt/naidizakupku/myapp.jar
Environment="SPRING_PROFILES_ACTIVE=production"
Environment="NAIDI_ZAKUPKU_TELEGRAM_BOT_TOKEN=your_token"
Environment="NAIDI_ZAKUPKU_JWT_SECRET_CODE=your_secret"
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

### 3. Мониторинг

```bash
# Создание пользователя для приложения
sudo useradd -r -s /bin/false naidizakupku

# Создание директории
sudo mkdir -p /opt/naidizakupku
sudo chown naidizakupku:naidizakupku /opt/naidizakupku

# Копирование JAR файла
sudo cp build/libs/myapp.jar /opt/naidizakupku/

# Запуск сервиса
sudo systemctl daemon-reload
sudo systemctl enable naidizakupku
sudo systemctl start naidizakupku

# Проверка статуса
sudo systemctl status naidizakupku
```

## Troubleshooting

### 1. Проблемы с авторизацией

**Ошибка: "Invalid Telegram data"**

```bash
# Проверьте токен бота
echo $NAIDI_ZAKUPKU_TELEGRAM_BOT_TOKEN

# Проверьте логи
tail -f logs/spring-boot-application.log | grep "TelegramAuthService"

# Проверьте время на сервере
date
```

**Ошибка: "Rate limit exceeded"**

```bash
# Проверьте настройки rate limiting
grep -r "MAX_REQUESTS_PER_MINUTE" src/

# Временно увеличьте лимиты в AuthRateLimitingFilter.kt
```

### 2. Проблемы с базой данных

**Ошибка: "Connection refused"**

```bash
# Проверьте статус PostgreSQL
sudo systemctl status postgresql

# Проверьте подключение
psql -h localhost -U bduser -d zakupdb

# Проверьте миграции
./gradlew liquibaseStatus
```

### 3. Проблемы с CORS

**Ошибка: "CORS policy"**

```bash
# Проверьте настройки CORS в SecurityConfiguration.kt
# Добавьте ваш домен в allowedOrigins

# Проверьте заголовки ответа
curl -I -H "Origin: https://your-domain.com" \
  http://localhost:9000/api/auth/telegram/validate
```

### 4. Проблемы с сессиями

**Ошибка: "Session not found"**

```bash
# Проверьте таблицу сессий
psql -h localhost -U bduser -d zakupdb -c "SELECT * FROM user_sessions;"

# Проверьте время на сервере
date

# Очистите старые сессии
psql -h localhost -U bduser -d zakupdb -c "DELETE FROM user_sessions WHERE expires_at < NOW();"
```

## Безопасность

### 1. Рекомендации по безопасности

- Используйте HTTPS в продакшне
- Регулярно обновляйте JWT секреты
- Настройте firewall
- Используйте сильные пароли для БД
- Включите логирование безопасности

### 2. Мониторинг безопасности

```bash
# Проверка неудачных попыток авторизации
tail -f logs/spring-boot-application.log | grep "Invalid Telegram"

# Проверка rate limiting
tail -f logs/spring-boot-application.log | grep "Rate limit exceeded"

# Мониторинг активных сессий
psql -h localhost -U bduser -d zakupdb -c "
SELECT COUNT(*) as active_sessions 
FROM user_sessions 
WHERE is_active = true AND expires_at > NOW();"
```

### 3. Резервное копирование

```bash
# Создание бэкапа БД
pg_dump -h localhost -U bduser zakupdb > backup_$(date +%Y%m%d_%H%M%S).sql

# Восстановление из бэкапа
psql -h localhost -U bduser zakupdb < backup_file.sql
```

## Обновление

### 1. Обновление приложения

```bash
# Остановка сервиса
sudo systemctl stop naidizakupku

# Создание бэкапа
pg_dump -h localhost -U bduser zakupdb > backup_before_update.sql

# Обновление кода
git pull origin main

# Сборка
./gradlew bootJar

# Копирование нового JAR
sudo cp build/libs/myapp.jar /opt/naidizakupku/

# Запуск сервиса
sudo systemctl start naidizakupku

# Проверка статуса
sudo systemctl status naidizakupku
```

### 2. Откат изменений

```bash
# Остановка сервиса
sudo systemctl stop naidizakupku

# Восстановление старой версии
sudo cp backup/myapp.jar /opt/naidizakupku/

# Восстановление БД (если необходимо)
psql -h localhost -U bduser zakupdb < backup_before_update.sql

# Запуск сервиса
sudo systemctl start naidizakupku
```
