import React, { useState, useEffect } from 'react';

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

interface BotInfoResponse {
  success: boolean;
  botInfo?: BotInfo;
  error?: string;
}

interface QrCodeResponse {
  success: boolean;
  qrCodeUrl?: string;
  error?: string;
}

class TelegramAuthService {
  private baseUrl = '/api/auth/telegram-bot';

  async getBotInfo(): Promise<BotInfo> {
    const response = await fetch(`${this.baseUrl}/info`);
    const data: BotInfoResponse = await response.json();
    
    if (!data.success) {
      throw new Error(data.error || 'Failed to get bot info');
    }
    
    return data.botInfo!;
  }

  async generateQrCode(botUrl: string): Promise<string> {
    const response = await fetch(`${this.baseUrl}/qr-code`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ botUrl }),
    });
    
    const data: QrCodeResponse = await response.json();
    
    if (!data.success) {
      throw new Error(data.error || 'Failed to generate QR code');
    }
    
    return data.qrCodeUrl!;
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

const TelegramAuthComponent: React.FC = () => {
  const [botInfo, setBotInfo] = useState<BotInfo | null>(null);
  const [qrCodeUrl, setQrCodeUrl] = useState<string | null>(null);
  const [code, setCode] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [session, setSession] = useState<AuthSession | null>(null);

  const authService = new TelegramAuthService();

  useEffect(() => {
    loadBotInfo();
  }, []);

  const loadBotInfo = async () => {
    try {
      setLoading(true);
      setError(null);
      const info = await authService.getBotInfo();
      setBotInfo(info);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load bot info');
    } finally {
      setLoading(false);
    }
  };

  const generateQrCode = async () => {
    if (!botInfo) return;

    try {
      setLoading(true);
      setError(null);
      const qrCode = await authService.generateQrCode(botInfo.botUrl);
      setQrCodeUrl(qrCode);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to generate QR code');
    } finally {
      setLoading(false);
    }
  };

  const handleLogin = async () => {
    if (!code.trim()) {
      setError('Please enter a code');
      return;
    }

    const codeNumber = parseInt(code);
    if (isNaN(codeNumber) || codeNumber < 100000 || codeNumber > 999999) {
      setError('Code must be a 6-digit number');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      setSuccess(null);

      const result = await authService.loginWithCode(codeNumber);
      
      if (result.success) {
        setSession(result.session!);
        setSuccess('Successfully logged in!');
        setCode('');
        
        // Сохраняем токен в localStorage
        if (result.token) {
          localStorage.setItem('authToken', result.token);
        }
      } else {
        setError(result.error || 'Login failed');
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    setSession(null);
    setSuccess(null);
    localStorage.removeItem('authToken');
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  if (session) {
    return (
      <div className="max-w-md mx-auto mt-10 p-6 bg-white rounded-lg shadow-lg">
        <h2 className="text-2xl font-bold text-center mb-6 text-green-600">
          Successfully Logged In!
        </h2>
        
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Username:</label>
            <p className="mt-1 text-lg">{session.username || 'N/A'}</p>
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700">First Name:</label>
            <p className="mt-1 text-lg">{session.firstName}</p>
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700">Telegram ID:</label>
            <p className="mt-1 text-lg">{session.telegramId}</p>
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700">Session ID:</label>
            <p className="mt-1 text-sm text-gray-500 break-all">{session.sessionId}</p>
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700">Created At:</label>
            <p className="mt-1 text-sm text-gray-500">
              {new Date(session.createdAt).toLocaleString()}
            </p>
          </div>
        </div>
        
        <button
          onClick={handleLogout}
          className="w-full mt-6 bg-red-500 text-white py-2 px-4 rounded-md hover:bg-red-600 transition-colors"
        >
          Logout
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-md mx-auto mt-10 p-6 bg-white rounded-lg shadow-lg">
      <h1 className="text-3xl font-bold text-center mb-8 text-gray-800">
        Telegram Bot Authentication
      </h1>

      {error && (
        <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
          {error}
        </div>
      )}

      {success && (
        <div className="mb-4 p-3 bg-green-100 border border-green-400 text-green-700 rounded">
          {success}
        </div>
      )}

      {botInfo && (
        <div className="mb-6 p-4 bg-blue-50 rounded-lg">
          <h3 className="text-lg font-semibold mb-2">Bot Information</h3>
          <p className="text-sm text-gray-600 mb-2">
            <strong>Username:</strong> {botInfo.botUsername}
          </p>
          <p className="text-sm text-gray-600 mb-3">
            <strong>URL:</strong> {botInfo.botUrl}
          </p>
          
          <div className="flex space-x-2">
            <a
              href={botInfo.botUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="flex-1 bg-blue-500 text-white py-2 px-4 rounded-md text-center hover:bg-blue-600 transition-colors"
            >
              Open Bot
            </a>
            
            <button
              onClick={generateQrCode}
              className="flex-1 bg-green-500 text-white py-2 px-4 rounded-md hover:bg-green-600 transition-colors"
            >
              QR Code
            </button>
          </div>
        </div>
      )}

      {qrCodeUrl && (
        <div className="mb-6 text-center">
          <h3 className="text-lg font-semibold mb-3">QR Code</h3>
          <img
            src={qrCodeUrl}
            alt="Telegram Bot QR Code"
            className="mx-auto border rounded-lg"
            style={{ maxWidth: '200px' }}
          />
          <p className="text-sm text-gray-600 mt-2">
            Scan this QR code to open the bot
          </p>
        </div>
      )}

      <div className="space-y-4">
        <div>
          <label htmlFor="code" className="block text-sm font-medium text-gray-700 mb-2">
            Enter Code from Bot
          </label>
          <input
            type="text"
            id="code"
            value={code}
            onChange={(e) => setCode(e.target.value)}
            placeholder="Enter 6-digit code"
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            maxLength={6}
          />
          <p className="text-xs text-gray-500 mt-1">
            Send /code to the bot to get your authentication code
          </p>
        </div>

        <button
          onClick={handleLogin}
          disabled={loading || !code.trim()}
          className="w-full bg-blue-500 text-white py-2 px-4 rounded-md hover:bg-blue-600 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
        >
          {loading ? 'Logging in...' : 'Login with Code'}
        </button>
      </div>

      <div className="mt-6 text-center">
        <p className="text-sm text-gray-600">
          Don't have a code?{' '}
          <a
            href={botInfo?.botUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="text-blue-500 hover:underline"
          >
            Open the bot and send /code
          </a>
        </p>
      </div>
    </div>
  );
};

export default TelegramAuthComponent;
