# Statistics API

Модуль Statistics отвечает за сбор, хранение, обработку и предоставление статистики и аналитики по результатам проверок доступности ресурсов.

## 1. Эндпоинты

### 1. Результаты проверок (Check Results)

#### Получить последние результаты проверки

```text
GET /api/v1/statistics/checks/{checkId}/latest
```

**Ответ:**

```json
{
  "checkId": "string",
  "resourceUrl": "string",
  "timestamp": "ISO8601 datetime",
  "status": "UP|DOWN|UNKNOWN",
  "responseTime": "number (ms)",
  "httpStatusCode": "number (опционально)",
  "errorMessage": "string (опционально)",
  "metrics": {
    "connectionTime": "number (ms)",
    "timeToFirstByte": "number (ms)",
    "sslValid": "boolean (опционально)",
    "sslExpirationDate": "ISO8601 datetime (опционально)"
  }
}
```

#### Получить историю результатов проверки

```text
GET /api/v1/statistics/checks/{checkId}/history
```

**Параметры запроса:**

- `from` (опционально) - начальная дата (в формате ISO8601)
- `to` (опционально) - конечная дата (в формате ISO8601)
- `page` (опционально) - номер страницы (по умолчанию 1)
- `size` (опционально) - количество элементов на странице (по умолчанию 20)

**Ответ:**

```json
{
  "results": [
    {
      "checkId": "string",
      "resourceUrl": "string",
      "timestamp": "ISO8601 datetime",
      "status": "UP|DOWN|UNKNOWN",
      "responseTime": "number (ms)",
      "httpStatusCode": "number (опционально)",
      "errorMessage": "string (опционально)",
      "metrics": {
        "connectionTime": "number (ms)",
        "timeToFirstByte": "number (ms)",
        "sslValid": "boolean (опционально)",
        "sslExpirationDate": "ISO8601 datetime (опционально)"
      }
    }
  ],
  "page": "number",
  "size": "number",
  "total": "number"
}
```

### 1.2. Агрегированные данные (Aggregated Data)

#### Получить агрегированные данные по проверке

```text
GET /api/v1/statistics/checks/{checkId}/aggregated
```

**Параметры запроса:**

- `from` (опционально) - начальная дата (в формате ISO8601)
- `to` (опционально) - конечная дата (в формате ISO8601)
- `interval` (опционально) - временной интервал для агрегированных данных (MINUTE, HOUR, DAY, WEEK, MONTH) (по умолчанию HOUR)

**Ответ:**

```json
{
  "checkId": "string",
  "interval": "MINUTE|HOUR|DAY|WEEK|MONTH",
 "from": "ISO8601 datetime",
  "to": "ISO8601 datetime",
  "upCount": "number",
  "downCount": "number",
  "unknownCount": "number",
  "avgResponseTime": "number (ms)",
  "minResponseTime": "number (ms)",
  "maxResponseTime": "number (ms)",
  "uptimePercentage": "number (0-100)"
}
```

### 1.3. Дашборд (Dashboard)

#### Получить данные для дашборда

```text
GET /api/v1/statistics/dashboard
```

**Параметры запроса:**

- `from` (опционально) - начальная дата (в формате ISO8601)
- `to` (опционально) - конечная дата (в формате ISO8601)

**Ответ:**

```json
{
  "totalChecks": "number",
  "upChecks": "number",
  "downChecks": "number",
  "unknownChecks": "number",
  "overallUptime": "number (0-100)",
  "recentAlerts": [
    {
      "checkId": "string",
      "resourceUrl": "string",
      "timestamp": "ISO8601 datetime",
      "status": "DOWN",
      "downtimeDuration": "number (ms)",
      "errorMessage": "string"
    }
  ]
}
```

### 1.4. Алерты (Alerts)

#### Получить информацию о недавних алертах

```text
GET /api/v1/statistics/alerts
```

**Параметры запроса:**

- `from` (опционально) - начальная дата (в формате ISO8601)
- `to` (опционально) - конечная дата (в формате ISO8601)
- `page` (опционально) - номер страницы (по умолчанию 1)
- `size` (опционально) - количество элементов на странице (по умолчанию 20)

**Ответ:**

```json
{
  "alerts": [
    {
      "checkId": "string",
      "resourceUrl": "string",
      "timestamp": "ISO8601 datetime",
      "status": "DOWN",
      "downtimeDuration": "number (ms)",
      "errorMessage": "string"
    }
  ],
  "page": "number",
  "size": "number",
  "total": "number"
}
```

## 2. Коды ошибок

- `200 OK` - Запрос успешно выполнен
- `201 Created` - Ресурс успешно создан
- `204 No Content` - Запрос успешно выполнен, но ответ пустой
- `400 Bad Request` - Некорректный запрос
- `401 Unauthorized` - Необходима аутентификация
- `403 Forbidden` - Недостаточно прав для доступа к ресурсу
- `404 Not Found` - Ресурс не найден
- `409 Conflict` - Конфликт при создании/обновлении ресурса
- `500 Internal Server Error` - Внутренняя ошибка сервера
- `503 Service Unavailable` - Сервис временно недоступен

## 3. Безопасность

Все эндпоинты Statistics API требуют аутентификации через OAuth2 токен. Поддерживаются следующие роли:

- `PINGTOWER_USER` - базовый доступ к API
- `PINGTOWER_EDITOR` - возможность изменения настроек
- `PINGTOWER_ADMIN` - полный доступ ко всем функциям системы

## 4. Ограничения

- Максимальный период для запроса истории результатов: 30 дней
- Максимальное количество записей в ответе: 10000
- Максимальный период для запроса агрегированных данных: 1 год
