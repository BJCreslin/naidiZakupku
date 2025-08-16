# Промт для ИИ: React TypeScript таблица закупок

## Задача

Создай React TypeScript компонент для отображения таблицы закупок с возможностью фильтрации, сортировки и пагинации.

## API

### Эндпоинт
```
GET /api/front/procurements
```

### Параметры запроса
- `searchText` (string) - поиск по названию или номеру реестра
- `customerName` (string) - фильтр по заказчику
- `minPrice` (number) - минимальная цена
- `maxPrice` (number) - максимальная цена
- `sortBy` (string) - поле для сортировки
- `sortDirection` (ASC/DESC) - направление сортировки
- `page` (number) - номер страницы (начиная с 0)
- `size` (number) - размер страницы

### Ответ
```typescript
interface ProcurementListResponse {
  procurements: Procurement[];
  totalCount: number;
  page: number;
  size: number;
  totalPages: number;
}

interface Procurement {
  id: number;
  registryNumber: string;
  name: string;
  publisher: string;
  price: number | null;
  linkOnPlacement: string;
  federalLawNumber: string;
  createdAt: string;
  updatedAt: string;
}
```

## Требования к компоненту

### Функциональность
1. **Таблица с колонками:**
   - Номер реестра (кликабельный, открывает ссылку)
   - Название закупки
   - Заказчик
   - Цена (форматированная с разделителями)
   - Федеральный закон
   - Дата создания

2. **Фильтрация:**
   - Поиск по тексту (название + номер реестра)
   - Фильтр по заказчику
   - Диапазон цен (мин/макс)
   - Кнопка сброса фильтров

3. **Сортировка:**
   - Клик по заголовку колонки для сортировки
   - Индикатор направления сортировки (↑↓)
   - Поддержка всех полей

4. **Пагинация:**
   - Номера страниц
   - Кнопки "Предыдущая"/"Следующая"
   - Выбор размера страницы (10, 20, 50, 100)
   - Отображение общего количества

5. **Дополнительно:**
   - Загрузка (спиннер)
   - Обработка ошибок
   - Пустое состояние
   - Адаптивность для мобильных устройств

### Технические требования
- React 18+ с TypeScript
- Хуки (useState, useEffect, useCallback, useMemo)
- Асинхронные запросы (fetch или axios)
- Современный UI (можно использовать Material-UI, Ant Design, или создать свой)
- Responsive дизайн
- TypeScript типы для всех данных

### Структура компонента
```typescript
interface ProcurementTableProps {
  apiUrl: string;
  token: string;
}

interface TableState {
  procurements: Procurement[];
  loading: boolean;
  error: string | null;
  filters: Filters;
  sorting: Sorting;
  pagination: Pagination;
}
```

### Пример использования
```tsx
<ProcurementTable 
  apiUrl="http://localhost:8080/api/front/procurements"
  token={userToken}
/>
```

### Дополнительные требования
1. **Производительность:**
   - Дебаунс для поиска (300ms)
   - Мемоизация компонентов
   - Виртуализация для больших списков (опционально)

2. **UX:**
   - Плавные анимации
   - Сохранение состояния фильтров в URL
   - Клавиатурная навигация
   - Экспорт в CSV (опционально)

3. **Безопасность:**
   - Валидация входных данных
   - Санитизация HTML
   - Защита от XSS

### Стилизация
- Современный минималистичный дизайн
- Цветовая схема: синий/серый
- Иконки (можно использовать react-icons)
- Hover эффекты
- Тени и скругления

### Тестирование
- Unit тесты для логики фильтрации/сортировки
- Integration тесты для API вызовов
- E2E тесты для основных сценариев

## Пример структуры файлов
```
src/
  components/
    ProcurementTable/
      index.tsx
      types.ts
      hooks/
        useProcurements.ts
        useFilters.ts
        useSorting.ts
        usePagination.ts
      components/
        TableHeader.tsx
        TableRow.tsx
        Filters.tsx
        Pagination.tsx
        LoadingSpinner.tsx
        ErrorMessage.tsx
      utils/
        formatters.ts
        validators.ts
      styles/
        ProcurementTable.module.css
```

## Критерии качества
- Чистый, читаемый код
- Правильная типизация TypeScript
- Компонентная архитектура
- Переиспользуемость
- Производительность
- Доступность (a11y)
- Адаптивность
