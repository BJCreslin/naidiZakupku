# API закупок для фронта

## Обзор

API для получения списка закупок пользователя с возможностью фильтрации, сортировки и пагинации.

## Базовый URL

```
/api/front/procurements
```

## Аутентификация

Все запросы требуют JWT токен в заголовке `Authorization`:
```
Authorization: Bearer <jwt_token>
```

## Эндпоинты

### GET /api/front/procurements

Получает список закупок пользователя с фильтрацией, сортировкой и пагинацией.

#### Параметры запроса

| Параметр | Тип | Обязательный | Описание | Пример |
|----------|-----|--------------|----------|--------|
| `searchText` | string | Нет | Поиск по названию закупки или номеру реестра | `строительство` |
| `customerName` | string | Нет | Фильтр по названию заказчика | `ООО Рога и Копыта` |
| `minPrice` | decimal | Нет | Минимальная цена | `100000` |
| `maxPrice` | decimal | Нет | Максимальная цена | `1000000` |
| `sortBy` | string | Нет | Поле для сортировки | `price`, `name`, `createdAt` |
| `sortDirection` | enum | Нет | Направление сортировки | `ASC`, `DESC` |
| `page` | integer | Нет | Номер страницы (начиная с 0) | `0` |
| `size` | integer | Нет | Размер страницы | `20` |

#### Доступные поля для сортировки

- `name` - название закупки
- `publisher` - заказчик
- `price` - цена
- `registryNumber` - номер реестра
- `federalLawNumber` - номер федерального закона
- `updatedAt` - дата обновления
- `createdAt` - дата создания (по умолчанию)

#### Примеры запросов

```bash
# Получить все закупки (по умолчанию)
GET /api/front/procurements

# Поиск по тексту
GET /api/front/procurements?searchText=строительство

# Фильтр по заказчику
GET /api/front/procurements?customerName=ООО

# Фильтр по диапазону цен
GET /api/front/procurements?minPrice=100000&maxPrice=1000000

# Сортировка по цене (по убыванию)
GET /api/front/procurements?sortBy=price&sortDirection=DESC

# Пагинация
GET /api/front/procurements?page=1&size=10

# Комбинированный запрос
GET /api/front/procurements?searchText=строительство&minPrice=50000&sortBy=createdAt&sortDirection=DESC&page=0&size=20
```

#### Ответ

```json
{
  "procurements": [
    {
      "id": 1,
      "registryNumber": "123456789",
      "name": "Строительство жилого дома",
      "publisher": "ООО СтройКомпания",
      "price": 1500000.00,
      "linkOnPlacement": "https://zakupki.gov.ru/epz/order/quicksearch/search.html",
      "federalLawNumber": "44-ФЗ",
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T10:30:00"
    }
  ],
  "totalCount": 150,
  "page": 0,
  "size": 20,
  "totalPages": 8
}
```

#### Структура ответа

| Поле | Тип | Описание |
|------|-----|----------|
| `procurements` | array | Массив закупок |
| `totalCount` | integer | Общее количество закупок |
| `page` | integer | Текущая страница |
| `size` | integer | Размер страницы |
| `totalPages` | integer | Общее количество страниц |

#### Структура закупки

| Поле | Тип | Описание |
|------|-----|----------|
| `id` | integer | Уникальный идентификатор |
| `registryNumber` | string | Номер реестра |
| `name` | string | Название закупки |
| `publisher` | string | Заказчик |
| `price` | decimal | Цена (может быть null) |
| `linkOnPlacement` | string | Ссылка на размещение |
| `federalLawNumber` | string | Номер федерального закона |
| `createdAt` | datetime | Дата создания |
| `updatedAt` | datetime | Дата обновления |

## Коды ошибок

| Код | Описание |
|-----|----------|
| 200 | Успешный запрос |
| 400 | Неверные параметры запроса |
| 401 | Не авторизован |
| 403 | Доступ запрещен |
| 500 | Внутренняя ошибка сервера |

## Кэширование

Результаты кэшируются на уровне пользователя и параметров запроса. Кэш автоматически сбрасывается при изменении данных закупок.

## Ограничения

- Максимальный размер страницы: 100 элементов
- Поиск по тексту нечувствителен к регистру
- Фильтрация по цене работает только для закупок с указанной ценой
