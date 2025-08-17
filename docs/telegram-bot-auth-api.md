# Telegram Bot Authentication API

## Описание

Система авторизации через Telegram бота с использованием временных кодов. Пользователь получает код командой `/code` в боте, затем вводит его в веб-приложении для авторизации.

## Эндпоинты

### 1. Получение информации о боте

**GET** `/api/auth/telegram-bot/info`

Возвращает информацию о Telegram боте для авторизации.

**Response:**
```json
{
  "success": true,
  "botInfo": {
    "botUsername": "your_bot_username",
    "botUrl": "https://t.me/your_bot_username"
  }
}
```

### 2. Генерация QR кода

**POST** `/api/auth/telegram-bot/qr-code`

Генерирует QR код для быстрого перехода к боту.

**Request:**
```json
{
  "botUrl": "https://t.me/your_bot_username"
}
```

**Response:**
```json
{
  "success": true,
  "qrCodeUrl": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..."
}
```

### 3. Авторизация по коду

**POST** `/api/auth/telegram-bot/login`

Проверяет код от бота и возвращает JWT токен при успешной авторизации.

**Request:**
```json
{
  "code": 123456
}
```

**Response:**
```json
{
  "success": true,
  "session": {
    "sessionId": "session_123",
    "telegramId": 123456789,
    "username": "user123",
    "firstName": "Иван",
    "lastName": "Иванов",
    "photoUrl": "https://t.me/i/userpic/320/user123.jpg",
    "isActive": true,
    "createdAt": "2024-01-01T12:00:00Z",
    "lastActivityAt": "2024-01-01T12:00:00Z"
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## Логика работы

1. **Получение информации о боте**: Фронтенд запрашивает информацию о боте для отображения ссылки и QR кода.

2. **Генерация QR кода**: Фронтенд может запросить QR код для быстрого перехода к боту.

3. **Получение кода в боте**: Пользователь переходит в бота и отправляет команду `/code`, получая временный код.

4. **Авторизация по коду**: Пользователь вводит код в веб-приложении, который отправляется на бэкенд для проверки.

5. **Проверка кода**: Бэкенд проверяет код в базе данных, его валидность и время жизни.

6. **Создание сессии**: При успешной проверке создается сессия и генерируется JWT токен.

7. **Возврат токена**: Фронтенд получает JWT токен для последующих запросов.

## Безопасность

- Rate limiting: 10 запросов в минуту на IP
- Валидация кодов: проверка существования и времени жизни
- JWT токены с expiration
- Автоматическое удаление использованных кодов

## Обработка ошибок

### Неверный код
```json
{
  "success": false,
  "error": "Неверный код или время истекло"
}
```

### Внутренняя ошибка сервера
```json
{
  "success": false,
  "error": "Внутренняя ошибка сервера"
}
```

## Пример использования на фронтенде (React TypeScript)

```typescript
interface BotInfo {
  botUsername: string;
  botUrl: string;
}

interface AuthSession {
  sessionId: string;
  telegramId: number;
  username: string | null;
  firstName: string;
  lastName: string | null;
  photoUrl: string | null;
  isActive: boolean;
  createdAt: string;
  lastActivityAt: string;
}

interface LoginResponse {
  success: boolean;
  session?: AuthSession;
  token?: string;
  error?: string;
}

class TelegramAuthService {
  private baseUrl = '/api/auth/telegram-bot';

  async getBotInfo(): Promise<BotInfo> {
    const response = await fetch(`${this.baseUrl}/info`);
    const data = await response.json();
    
    if (!data.success) {
      throw new Error(data.error || 'Failed to get bot info');
    }
    
    return data.botInfo;
  }

  async generateQrCode(botUrl: string): Promise<string> {
    const response = await fetch(`${this.baseUrl}/qr-code`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ botUrl }),
    });
    
    const data = await response.json();
    
    if (!data.success) {
      throw new Error(data.error || 'Failed to generate QR code');
    }
    
    return data.qrCodeUrl;
  }

  async loginWithCode(code: number): Promise<LoginResponse> {
    const response = await fetch(`${this.baseUrl}/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ code }),
    });
    
    return await response.json();
  }
}

// Пример использования
const authService = new TelegramAuthService();

// Получение информации о боте
const botInfo = await authService.getBotInfo();
console.log(`Bot URL: ${botInfo.botUrl}`);

// Генерация QR кода
const qrCodeUrl = await authService.generateQrCode(botInfo.botUrl);

// Авторизация по коду
const loginResult = await authService.loginWithCode(123456);
if (loginResult.success) {
  localStorage.setItem('token', loginResult.token!);
  console.log('Successfully logged in:', loginResult.session);
} else {
  console.error('Login failed:', loginResult.error);
}
```

## Настройка

### Переменные окружения

```properties
# Telegram Bot
NAIDI_ZAKUPKU_TELEGRAM_BOT_NAME=your_bot_username
NAIDI_ZAKUPKU_TELEGRAM_BOT_TOKEN=your_bot_token

# JWT
NAIDI_ZAKUPKU_JWT_SECRET_CODE=your_secure_jwt_secret
NAIDI_ZAKUPKU_JWT_TOKEN_EXPIRED=1800000
```

### Зависимости

Добавлены зависимости для генерации QR-кодов:
- `com.google.zxing:core:3.5.2`
- `com.google.zxing:javase:3.5.2`
