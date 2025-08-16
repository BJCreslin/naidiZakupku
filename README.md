# NaidiZakupku - Telegram Bot

Telegram бот для управления закупками с интуитивным интерфейсом на inline кнопках.

## 🚀 Возможности

- **📋 Список закупок** - просмотр всех сохраненных закупок с пагинацией
- **🔍 Фильтрация** - фильтры по цене, дате и заказчику
- **📊 Статистика** - аналитика по закупкам
- **🤖 AI интеграция** - анализ закупок через GigaChat
- **🔗 Браузерное расширение** - добавление закупок через Chrome Extension
- **📈 Мониторинг** - комплексная система мониторинга производительности

## 🏗️ Архитектура

Проект построен на Spring Boot с использованием:
- **Kotlin** - основной язык разработки
- **Spring Boot** - фреймворк
- **Telegram Bot API** - интеграция с Telegram
- **JPA/Hibernate** - работа с базой данных
- **Gradle** - система сборки
- **Micrometer + Prometheus** - мониторинг метрик

### Основные компоненты

```
src/main/kotlin/ru/bjcreslin/naidizakupku/
├── telegram/           # Telegram бот
│   ├── TelegramBot.kt  # Основной класс бота
│   ├── events/         # Обработчики событий
│   └── handlers/       # Обработчики команд
├── procurement/        # Модуль закупок
├── user/              # Модуль пользователей
├── security/          # Безопасность
├── cfg/               # Конфигурация
│   └── MetricsConfiguration.kt  # Конфигурация метрик
└── gigachat/          # GigaChat интеграция
```

## 📚 Документация

- [Документация по inline кнопкам](docs/telegram-bot-inline-buttons.md) - подробное описание системы кнопок
- [Архитектура бота](docs/telegram-bot-architecture.md) - техническая документация
- [API Reference](docs/api-reference.md) - справочник API
- [Мониторинг производительности](monitoring.md) - система мониторинга
- [Настройка мониторинга](docs/monitoring-setup.md) - инструкции по развертыванию

## 🛠️ Установка и запуск

### Требования

- Java 17+
- Gradle 8.0+
- PostgreSQL 12+
- Docker (для мониторинга)

### Настройка

1. **Клонирование репозитория**
   ```bash
   git clone https://github.com/your-repo/naidiZakupku.git
   cd naidiZakupku
   ```

2. **Настройка базы данных**
   ```bash
   # Создайте базу данных PostgreSQL
   createdb naidizakupku
   ```

3. **Конфигурация**
   ```bash
   # Скопируйте и настройте application.properties
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

4. **Запуск приложения**
   ```bash
   ./gradlew bootRun
   ```

5. **Запуск системы мониторинга (опционально)**
   ```bash
   # Windows
   scripts\start-monitoring.bat
   
   # Linux/Mac
   ./scripts/start-monitoring.sh
   ```

### Переменные окружения

```properties
# Telegram Bot
telegram.bot.token=YOUR_BOT_TOKEN
telegram.bot.name=YOUR_BOT_NAME

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/naidizakupku
spring.datasource.username=your_username
spring.datasource.password=your_password

# GigaChat
gigachat.api.url=YOUR_GIGACHAT_URL
gigachat.api.token=YOUR_GIGACHAT_TOKEN

# JWT
NAIDI_ZAKUPKU_JWT_SECRET_CODE=your_jwt_secret
```

## 🎯 Использование

### Команды бота

- `/start` - начало работы с ботом
- `/list` - список закупок с inline кнопками
- `/stats` - статистика по закупкам
- `/help` - справка по командам
- `/gigachat` - анализ закупок через AI

### Inline кнопки

Бот поддерживает интуитивную навигацию через inline кнопки:

- **⬅️ Предыдущая / Следующая ➡️** - пагинация по страницам
- **📋 [Название закупки]** - просмотр деталей закупки
- **🔍 По цене / 📅 По дате** - фильтры
- **🔄 Обновить** - обновление списка
- **⬅️ Назад к списку** - возврат к списку
- **🗑️ Удалить из списка** - удаление закупки

## 📊 Мониторинг и метрики

### Система мониторинга

Приложение включает комплексную систему мониторинга производительности:

- **Spring Boot Actuator** - базовые метрики здоровья приложения
- **Micrometer** - сбор кастомных метрик
- **Prometheus** - хранение и запросы метрик
- **Grafana** - визуализация и дашборды
- **AlertManager** - управление алертами

### Доступные метрики

#### Системные метрики
- Использование памяти JVM
- CPU нагрузка
- Количество потоков
- Время работы приложения

#### Прикладные метрики
- Время обработки Telegram обновлений
- Количество команд бота
- Время запросов к GigaChat API
- Производительность API endpoints
- Валидация JWT токенов

#### Кэш метрики
- Hit ratio всех кэшей
- Размер кэшей
- Количество evictions
- Время доступа к кэшу

#### Бизнес метрики
- Количество поисков закупок
- Время парсинга новостей
- Rate limit превышения

### Запуск мониторинга

```bash
# Запуск системы мониторинга
scripts/start-monitoring.bat  # Windows
./scripts/start-monitoring.sh  # Linux/Mac

# Остановка
scripts/stop-monitoring.bat    # Windows
./scripts/stop-monitoring.sh   # Linux/Mac

# Просмотр логов
scripts/monitoring-logs.bat all    # Windows
./scripts/monitoring-logs.sh all   # Linux/Mac
```

### Доступные сервисы

После запуска мониторинга доступны:

- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)
- **AlertManager**: http://localhost:9093

### Алерты

Система включает предустановленные алерты:

- **Критические**: высокое использование ресурсов, медленные ответы API
- **Предупреждающие**: низкий hit ratio кэша, частые ошибки
- **Информационные**: статус приложения, бизнес-метрики

## 🧪 Тестирование

### Unit тесты
```bash
./gradlew test
```

### Интеграционные тесты
```bash
./gradlew integrationTest
```

### Запуск всех тестов
```bash
./gradlew check
```

## 📦 Сборка

### JAR файл
```bash
./gradlew build
```

### Docker образ
```bash
docker build -t naidizakupku .
docker run -p 8080:8080 naidizakupku
```

## 🔧 Разработка

### Структура проекта

```
naidiZakupku/
├── src/
│   ├── main/
│   │   ├── kotlin/          # Исходный код
│   │   └── resources/       # Ресурсы
│   └── test/                # Тесты
├── docs/                    # Документация
│   ├── monitoring-setup.md  # Настройка мониторинга
│   ├── grafana-dashboard.json # Дашборд Grafana
│   └── prometheus-alerts.yml  # Алерты Prometheus
├── scripts/                 # Скрипты развертывания
├── docker-compose.monitoring.yml # Docker Compose для мониторинга
├── build.gradle.kts         # Конфигурация Gradle
└── README.md               # Этот файл
```

### Добавление новой функции

1. **Создайте обработчик команды**
   ```kotlin
   @Service("root#newcommand")
   class NewCommandHandler : CommandHandler {
       override fun execute(chatId: Long, params: String): String {
           return "Новая команда выполнена!"
       }
   }
   ```

2. **Добавьте inline кнопку**
   ```kotlin
   val newButton = InlineKeyboardButton()
   newButton.text = "🆕 Новая функция"
   newButton.callbackData = "new_feature"
   ```

3. **Обработайте callback**
   ```kotlin
   callbackData == "new_feature" -> {
       handleNewFeatureCallback(chatId)
   }
   ```

4. **Добавьте метрики (опционально)**
   ```kotlin
   customMetricsService.incrementTelegramCommandCounter("new_feature")
   ```

### Стиль кода

Проект использует:
- **Kotlin Coding Conventions**
- **Spring Boot Best Practices**
- **Clean Architecture principles**

## 🤝 Вклад в проект

1. Fork репозитория
2. Создайте feature branch (`git checkout -b feature/amazing-feature`)
3. Commit изменения (`git commit -m 'Add amazing feature'`)
4. Push в branch (`git push origin feature/amazing-feature`)
5. Откройте Pull Request

## 📄 Лицензия

Этот проект лицензирован под MIT License - см. файл [LICENSE](LICENSE) для деталей.

## 📞 Поддержка

- **Issues**: [GitHub Issues](https://github.com/your-repo/naidiZakupku/issues)
- **Discussions**: [GitHub Discussions](https://github.com/your-repo/naidiZakupku/discussions)
- **Email**: support@naidizakupku.com

## 🙏 Благодарности

- [Spring Boot](https://spring.io/projects/spring-boot) - фреймворк
- [Telegram Bot API](https://core.telegram.org/bots/api) - API ботов
- [GigaChat](https://developers.sber.ru/portal/products/gigachat) - AI интеграция
- [Prometheus](https://prometheus.io/) - мониторинг метрик
- [Grafana](https://grafana.com/) - визуализация данных

---

**NaidiZakupku** - находи закупки легко! 🎯
