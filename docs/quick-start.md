# Quick Start Guide - Быстрый старт

## 🚀 Быстрая настройка за 5 минут

### 1. Предварительные требования

```bash
# Проверьте версии
java -version    # Должна быть 17+
./gradlew --version  # Должна быть 8.0+
```

### 2. Клонирование и настройка

```bash
# Клонируйте репозиторий
git clone https://github.com/your-repo/naidiZakupku.git
cd naidiZakupku

# Создайте базу данных (PostgreSQL)
createdb naidizakupku

# Настройте переменные окружения
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

### 3. Настройка Telegram бота

1. Создайте бота через [@BotFather](https://t.me/botfather)
2. Получите токен бота
3. Добавьте токен в `application.properties`:

```properties
telegram.bot.token=YOUR_BOT_TOKEN_HERE
telegram.bot.name=YOUR_BOT_NAME
```

### 4. Запуск

```bash
# Запустите приложение
./gradlew bootRun

# Или соберите и запустите JAR
./gradlew build
java -jar build/libs/naidiZakupku-*.jar
```

### 5. Тестирование

1. Найдите вашего бота в Telegram
2. Отправьте команду `/start`
3. Попробуйте `/list` для просмотра inline кнопок

## 🔧 Разработка

### Структура проекта

```
src/main/kotlin/ru/bjcreslin/naidizakupku/
├── telegram/                    # Telegram бот
│   ├── TelegramBot.kt          # Основной класс
│   ├── events/                 # Обработчики событий
│   │   ├── impl/
│   │   │   ├── MessageEventImpl.kt    # Обработка сообщений
│   │   │   └── CallbackEventImpl.kt   # Обработка кнопок
│   │   └── handlers/           # Обработчики команд
│   │       └── impl/
│   │           ├── ProcurementsListBotService.kt  # Список закупок
│   │           ├── StatsBotService.kt             # Статистика
│   │           └── HelpBotService.kt              # Справка
│   └── state/                  # Управление состояниями
├── procurement/                # Модуль закупок
├── user/                      # Модуль пользователей
└── cfg/                       # Конфигурация
```

### Основные файлы для понимания

1. **`TelegramBot.kt`** - точка входа, обработка обновлений
2. **`MessageEventImpl.kt`** - обработка команд пользователя
3. **`CallbackEventImpl.kt`** - обработка нажатий на кнопки
4. **`ProcurementsListBotService.kt`** - логика списка закупок с кнопками

### Добавление новой команды

```kotlin
// 1. Создайте обработчик
@Service("root#test")
class TestCommandHandler : CommandHandler {
    override fun execute(chatId: Long, params: String): String {
        return "Тестовая команда работает!"
    }
    
    override fun getSupportedState(): SectionState = SectionState.ROOT
}

// 2. Команда автоматически станет доступной как /test
```

### Добавление новой кнопки

```kotlin
// 1. В CallbackEventImpl добавьте обработку
when {
    callbackData == "test_button" -> {
        handleTestButtonCallback(chatId)
    }
}

// 2. Создайте обработчик
private fun handleTestButtonCallback(chatId: Long): SendMessage {
    val message = SendMessage()
    message.chatId = chatId.toString()
    message.text = "Кнопка работает!"
    message.enableMarkdown(true)
    return message
}

// 3. Добавьте кнопку в клавиатуру
val testButton = InlineKeyboardButton()
testButton.text = "🧪 Тест"
testButton.callbackData = "test_button"
keyboard.add(mutableListOf(testButton))
```

## 🧪 Тестирование

### Запуск тестов

```bash
# Все тесты
./gradlew test

# Конкретный тест
./gradlew test --tests ProcurementsListBotServiceTest

# С отчетом
./gradlew test --info
```

### Пример теста

```kotlin
@Test
fun `should create keyboard with pagination`() {
    // Given
    val service = ProcurementsListBotService(mockUserService, mockProcurementService)
    
    // When
    val message = service.executeWithPagination(123L, 0)
    
    // Then
    assertThat(message.replyMarkup).isInstanceOf(InlineKeyboardMarkup::class.java)
    assertThat(message.text).contains("Список закупок")
}
```

## 🐛 Отладка

### Логирование

```kotlin
// В любом классе
private val logger = LoggerFactory.getLogger(YourClass::class.java)

logger.info("Обработка запроса: $data")
logger.warn("Предупреждение: $warning")
logger.error("Ошибка: $error", exception)
```

### Отладка callback'ов

```kotlin
// В CallbackEventImpl
logger.info("Processing callback: $callbackData from user $username")
```

### Проверка состояния

```bash
# Проверьте логи приложения
tail -f logs/application.log

# Проверьте подключение к БД
./gradlew bootRun --debug
```

## 📚 Полезные команды

```bash
# Очистка и пересборка
./gradlew clean build

# Запуск с профилем разработки
./gradlew bootRun --args='--spring.profiles.active=dev'

# Проверка зависимостей
./gradlew dependencies

# Обновление Gradle Wrapper
./gradlew wrapper --gradle-version=8.8
```

## 🔍 Частые проблемы

### 1. Бот не отвечает
- Проверьте токен в `application.properties`
- Убедитесь, что приложение запущено
- Проверьте логи на ошибки

### 2. Кнопки не работают
- Проверьте обработку в `CallbackEventImpl`
- Убедитесь в правильности `callbackData`
- Проверьте `enableMarkdown(true)`

### 3. Ошибки компиляции
- Проверьте версию Java (должна быть 17+)
- Очистите кэш: `./gradlew clean`
- Обновите зависимости: `./gradlew --refresh-dependencies`

### 4. Проблемы с БД
- Проверьте подключение к PostgreSQL
- Убедитесь, что база данных создана
- Проверьте миграции: `./gradlew flywayInfo`

## 📞 Получение помощи

1. **Документация**: [docs/](docs/)
2. **Issues**: [GitHub Issues](https://github.com/your-repo/naidiZakupku/issues)
3. **Discussions**: [GitHub Discussions](https://github.com/your-repo/naidiZakupku/discussions)

## 🎯 Следующие шаги

1. Изучите [документацию по inline кнопкам](telegram-bot-inline-buttons.md)
2. Познакомьтесь с [архитектурой](telegram-bot-architecture.md)
3. Добавьте свою первую функцию
4. Напишите тесты
5. Создайте Pull Request

---

**Удачи в разработке! 🚀**
