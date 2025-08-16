# Документация проекта NaidiZakupku

## Обзор

Проект представляет собой систему для работы с закупками, включающую:
- Telegram бота для управления закупками
- REST API для фронтенда
- Административный интерфейс
- Chrome расширение для добавления закупок

## API Документация

### Основные API

1. **[API закупок для фронта](./front-api-procurements.md)** - новый API для получения списка закупок с фильтрацией, сортировкой и пагинацией
2. **[Telegram Bot API](./telegram-bot-architecture.md)** - архитектура Telegram бота
3. **[Inline Buttons](./telegram-bot-inline-buttons.md)** - работа с inline кнопками в Telegram
4. **[Caching](./telegram-caching.md)** - система кэширования

### Мониторинг

- **[Настройка мониторинга](./monitoring-setup.md)** - инструкции по настройке Prometheus, Grafana, AlertManager
- **[Quick Start](./quick-start.md)** - быстрый старт проекта

## Новый Front API

### Эндпоинт закупок

```
GET /api/front/procurements
```

**Возможности:**
- ✅ Фильтрация по тексту, заказчику, цене
- ✅ Сортировка по всем полям
- ✅ Пагинация
- ✅ Кэширование результатов
- ✅ JWT аутентификация

**Пример использования:**
```bash
curl -H "Authorization: Bearer <token>" \
  "http://localhost:8080/api/front/procurements?searchText=строительство&sortBy=price&sortDirection=DESC&page=0&size=20"
```

### Компоненты

1. **ProcurementFrontController** - контроллер для обработки запросов
2. **ProcurementFrontService** - бизнес-логика фильтрации и сортировки
3. **ProcurementFrontDto** - DTO для фронта с дополнительными полями

## Разработка

### Структура проекта

```
src/main/kotlin/ru/bjcreslin/naidizakupku/
├── front_api/                    # API для фронта
│   ├── controller/              # Контроллеры
│   ├── dto/                    # DTO объекты
│   └── service/                # Сервисы
├── procurement/                 # Модуль закупок
├── telegram/                   # Telegram бот
├── security/                   # Безопасность
└── user/                       # Пользователи
```

### Технологии

- **Backend:** Kotlin, Spring Boot, JPA, PostgreSQL
- **Security:** JWT, Spring Security
- **Caching:** Spring Cache
- **Monitoring:** Prometheus, Grafana, AlertManager
- **Frontend:** React TypeScript (планируется)

## Быстрый старт

1. Запустите базу данных и мониторинг:
```bash
docker-compose -f docker-compose.monitoring.yml up -d
```

2. Запустите приложение:
```bash
./gradlew bootRun
```

3. API будет доступен по адресу: `http://localhost:8080`

## Промт для React разработки

Для создания React TypeScript таблицы закупок используйте промт: **[React Table Prompt](./react-table-prompt.md)**
