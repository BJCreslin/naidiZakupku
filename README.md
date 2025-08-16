# NaidiZakupku - Telegram Bot

Telegram бот для управления закупками с интуитивным интерфейсом на inline кнопках.

## 🚀 Возможности

- **📋 Список закупок** - просмотр всех сохраненных закупок с пагинацией
- **🔍 Фильтрация** - фильтры по цене, дате и заказчику
- **📊 Статистика** - аналитика по закупкам
- **🤖 AI интеграция** - анализ закупок через GigaChat
- **🔗 Браузерное расширение** - добавление закупок через Chrome Extension

## 🏗️ Архитектура

Проект построен на Spring Boot с использованием:
- **Kotlin** - основной язык разработки
- **Spring Boot** - фреймворк
- **Telegram Bot API** - интеграция с Telegram
- **JPA/Hibernate** - работа с базой данных
- **Gradle** - система сборки

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
└── cfg/               # Конфигурация
```

## 📚 Документация

- [Документация по inline кнопкам](docs/telegram-bot-inline-buttons.md) - подробное описание системы кнопок
- [Архитектура бота](docs/telegram-bot-architecture.md) - техническая документация
- [API Reference](docs/api-reference.md) - справочник API

## 🛠️ Установка и запуск

### Требования

- Java 17+
- Gradle 8.0+
- PostgreSQL 12+

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

4. **Запуск**
   ```bash
   ./gradlew bootRun
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

### Стиль кода

Проект использует:
- **Kotlin Coding Conventions**
- **Spring Boot Best Practices**
- **Clean Architecture principles**

## 📊 Мониторинг

### Логирование

Бот использует структурированное логирование:
- `INFO` - обычные операции
- `WARN` - предупреждения
- `ERROR` - ошибки

### Метрики

- Время обработки запросов
- Количество активных пользователей
- Статистика использования команд

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

---

**NaidiZakupku** - находи закупки легко! 🎯
