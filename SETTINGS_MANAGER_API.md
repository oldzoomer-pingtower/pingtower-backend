# Settings Manager API

Модуль Settings Manager является центральным компонентом системы мониторинга PingTower, отвечающим за управление настройками всех модулей системы. Он обеспечивает централизованное хранение, управление и распространение конфигураций между различными компонентами системы через Apache Kafka.

## 1. Эндпоинты

### 1.1. Настройки (Settings)

#### Получить список настроек

```text
GET /api/v1/settings
```

**Параметры запроса:**

- `page` (опционально) - номер страницы (по умолчанию 1)
- `size` (опционально) - количество элементов на странице (по умолчанию 20)
- `module` (опционально) - фильтр по модулю (pinger, notificator, statistics)

**Ответ:**

```json
{
  "settings": [
    {
      "id": "uuid",
      "module": "string",
      "key": "string",
      "value": "string|object",
      "description": "string",
      "createdAt": "ISO8601 datetime",
      "updatedAt": "ISO8601 datetime",
      "version": "integer"
    }
  ],
  "page": "number",
  "size": "number",
  "total": "number"
}
```

#### Получить настройки модуля

```text
GET /api/v1/settings/{module}
```

**Ответ:**

```json
{
  "settings": [
    {
      "id": "uuid",
      "module": "string",
      "key": "string",
      "value": "string|object",
      "description": "string",
      "createdAt": "ISO8601 datetime",
      "updatedAt": "ISO8601 datetime",
      "version": "integer"
    }
  ]
}
```

#### Получить конкретную настройку

```text
GET /api/v1/settings/{module}/{key}
```

**Ответ:**

```json
{
  "id": "uuid",
  "module": "string",
  "key": "string",
  "value": "string|object",
  "description": "string",
  "createdAt": "ISO8601 datetime",
  "updatedAt": "ISO8601 datetime",
  "version": "integer"
}
```

#### Создать новую настройку

```text
POST /api/v1/settings
```

**Тело запроса:**

```json
{
  "module": "string",
  "key": "string",
  "value": "string|object",
  "description": "string"
}
```

**Ответ:**

```json
{
  "id": "uuid",
  "module": "string",
  "key": "string",
  "value": "string|object",
  "description": "string",
  "createdAt": "ISO8601 datetime",
  "updatedAt": "ISO8601 datetime",
  "version": "integer"
}
```

#### Обновить настройку

```text
PUT /api/v1/settings/{module}/{key}
```

**Тело запроса:**

```json
{
  "value": "string|object",
  "description": "string"
}
```

**Ответ:**

```json
{
  "id": "uuid",
  "module": "string",
  "key": "string",
  "value": "string|object",
  "description": "string",
  "createdAt": "ISO8601 datetime",
  "updatedAt": "ISO8601 datetime",
  "version": "integer"
}
```

#### Частично обновить настройку

```text
PATCH /api/v1/settings/{module}/{key}
```

**Тело запроса:**

```json
{
  "value": "string|object (опционально)",
  "description": "string (опционально)"
}
```

**Ответ:**

```json
{
  "id": "uuid",
  "module": "string",
  "key": "string",
  "value": "string|object",
  "description": "string",
  "createdAt": "ISO8601 datetime",
  "updatedAt": "ISO8601 datetime",
  "version": "integer"
}
```

#### Удалить настройку

```text
DELETE /api/v1/settings/{module}/{key}
```

**Ответ:**

```json
{
  "message": "Setting deleted successfully"
}
```

### 1.2. Пользователи (Users)

#### Получить список пользователей

```text
GET /api/v1/users
```

**Параметры запроса:**

- `page` (опционально) - номер страницы (по умолчанию 1)
- `size` (опционально) - количество элементов на странице (по умолчанию 20)

**Ответ:**

```json
{
  "users": [
    {
      "id": "uuid",
      "username": "string",
      "email": "string",
      "firstName": "string",
      "lastName": "string",
      "createdAt": "ISO8601 datetime",
      "updatedAt": "ISO8601 datetime"
    }
  ],
  "page": "number",
  "size": "number",
  "total": "number"
}
```

#### Создать нового пользователя

```text
POST /api/v1/users
```

**Тело запроса:**

```json
{
  "username": "string",
  "email": "string",
  "firstName": "string",
  "lastName": "string"
}
```

**Ответ:**

```json
{
  "id": "uuid",
  "username": "string",
  "email": "string",
  "firstName": "string",
  "lastName": "string",
  "createdAt": "ISO8601 datetime",
  "updatedAt": "ISO8601 datetime"
}
```

#### Обновить пользователя

```text
PUT /api/v1/users/{id}
```

**Тело запроса:**

```json
{
  "username": "string",
  "email": "string",
  "firstName": "string",
  "lastName": "string"
}
```

**Ответ:**

```json
{
  "id": "uuid",
  "username": "string",
  "email": "string",
  "firstName": "string",
  "lastName": "string",
  "createdAt": "ISO8601 datetime",
  "updatedAt": "ISO8601 datetime"
}
```

#### Удалить пользователя

```text
DELETE /api/v1/users/{id}
```

**Ответ:**

```json
{
  "message": "User deleted successfully"
}
```

### 1.3. Роли (Roles)

#### Получить список ролей

```text
GET /api/v1/roles
```

**Параметры запроса:**

- `page` (опционально) - номер страницы (по умолчанию 1)
- `size` (опционально) - количество элементов на странице (по умолчанию 20)

**Ответ:**

```json
{
  "roles": [
    {
      "id": "uuid",
      "name": "string",
      "description": "string",
      "permissions": ["string"],
      "createdAt": "ISO8601 datetime",
      "updatedAt": "ISO8601 datetime"
    }
  ],
  "page": "number",
  "size": "number",
  "total": "number"
}
```

#### Создать новую роль

```text
POST /api/v1/roles
```

**Тело запроса:**

```json
{
  "name": "string",
  "description": "string",
  "permissions": ["string"]
}
```

**Ответ:**

```json
{
  "id": "uuid",
  "name": "string",
  "description": "string",
  "permissions": ["string"],
  "createdAt": "ISO8601 datetime",
  "updatedAt": "ISO8601 datetime"
}
```

#### Обновить роль

```text
PUT /api/v1/roles/{id}
```

**Тело запроса:**

```json
{
  "name": "string",
  "description": "string",
  "permissions": ["string"]
}
```

**Ответ:**

```json
{
  "id": "uuid",
  "name": "string",
  "description": "string",
  "permissions": ["string"],
  "createdAt": "ISO8601 datetime",
  "updatedAt": "ISO8601 datetime"
}
```

#### Удалить роль

```text
DELETE /api/v1/roles/{id}
```

**Ответ:**

```json
{
  "message": "Role deleted successfully"
}
```

### 1.4. Настройки проверок Pinger (Pinger Checks Configuration)

#### Получить список настроек проверок Pinger

```text
GET /api/v1/pinger/checks
```

**Параметры запроса:**

- `page` (опционально) - номер страницы (по умолчанию 1)
- `size` (опционально) - количество элементов на странице (по умолчанию 20)

**Ответ:**

```json
{
  "checks": [
    {
      "id": "string",
      "type": "HTTP|TCP|DNS|API",
      "resourceUrl": "string",
      "frequency": "number (ms)",
      "timeout": "number (ms)",
      "expectedStatusCode": "number (опционально)",
      "expectedResponseTime": "number (ms) (опционально)",
      "validateSsl": "boolean (опционально)",
      "createdAt": "ISO8601 datetime",
      "updatedAt": "ISO8601 datetime"
    }
  ],
  "page": "number",
  "size": "number",
  "total": "number"
}
```

#### Получить информацию о конкретной настройке проверки Pinger

```text
GET /api/v1/pinger/checks/{checkId}
```

**Ответ:**

```json
{
  "id": "string",
  "type": "HTTP|TCP|DNS|API",
  "resourceUrl": "string",
  "frequency": "number (ms)",
  "timeout": "number (ms)",
  "expectedStatusCode": "number (опционально)",
  "expectedResponseTime": "number (ms) (опционально)",
  "validateSsl": "boolean (опционально)",
  "createdAt": "ISO8601 datetime",
  "updatedAt": "ISO8601 datetime"
}
```

#### Создать новую настройку проверки Pinger

```text
POST /api/v1/pinger/checks
```

**Тело запроса:**

```json
{
  "type": "HTTP|TCP|DNS|API",
  "resourceUrl": "string",
  "frequency": "number (ms)",
  "timeout": "number (ms)",
  "expectedStatusCode": "number (опционально)",
  "expectedResponseTime": "number (ms) (опционально)",
  "validateSsl": "boolean (опционально)"
}
```

**Ответ:**

```json
{
  "id": "string",
  "type": "HTTP|TCP|DNS|API",
  "resourceUrl": "string",
  "frequency": "number (ms)",
  "timeout": "number (ms)",
  "expectedStatusCode": "number (опционально)",
  "expectedResponseTime": "number (ms) (опционально)",
  "validateSsl": "boolean (опционально)",
  "createdAt": "ISO8601 datetime",
  "updatedAt": "ISO8601 datetime"
}
```

#### Обновить настройку проверки Pinger

```text
PUT /api/v1/pinger/checks/{checkId}
```

**Тело запроса:**

```json
{
  "type": "HTTP|TCP|DNS|API",
  "resourceUrl": "string",
  "frequency": "number (ms)",
  "timeout": "number (ms)",
  "expectedStatusCode": "number (опционально)",
  "expectedResponseTime": "number (ms) (опционально)",
  "validateSsl": "boolean (опционально)"
}
```

**Ответ:**

```json
{
  "id": "string",
  "type": "HTTP|TCP|DNS|API",
  "resourceUrl": "string",
  "frequency": "number (ms)",
  "timeout": "number (ms)",
  "expectedStatusCode": "number (опционально)",
  "expectedResponseTime": "number (ms) (опционально)",
  "validateSsl": "boolean (опционально)",
  "createdAt": "ISO8601 datetime",
  "updatedAt": "ISO8601 datetime"
}
```

#### Удалить настройку проверки Pinger

```text
DELETE /api/v1/pinger/checks/{checkId}
```

**Ответ:**

```json
{
  "message": "Check deleted successfully"
}
```

### 1.5. Настройки уведомлений Notificator (Notification Configuration)

#### Получить список каналов уведомлений

```text
GET /api/v1/notificator/channels
```

**Параметры запроса:**

- `page` (опционально) - номер страницы (по умолчанию 1)
- `size` (опционально) - количество элементов на странице (по умолчанию 20)
- `type` (опционально) - фильтр по типу канала (EMAIL, TELEGRAM, WEBHOOK)

**Ответ:**

```json
{
  "channels": [
    {
      "id": "string",
      "type": "EMAIL|TELEGRAM|WEBHOOK",
      "name": "string",
      "configuration": {
        // Конфигурация зависит от типа канала
      },
      "enabled": "boolean",
      "createdAt": "ISO8601 datetime",
      "updatedAt": "ISO8601 datetime"
    }
  ],
  "page": "number",
  "size": "number",
  "total": "number"
}
```

#### Получить информацию о конкретном канале уведомлений

```text
GET /api/v1/notificator/channels/{channelId}
```

**Ответ:**

```json
{
  "id": "string",
  "type": "EMAIL|TELEGRAM|WEBHOOK",
 "name": "string",
  "configuration": {
    // Конфигурация зависит от типа канала
  },
  "enabled": "boolean",
  "createdAt": "ISO8601 datetime",
  "updatedAt": "ISO8601 datetime"
}
```

#### Создать новый канал уведомлений

```text
POST /api/v1/notificator/channels
```

**Тело запроса (для Email):**

```json
{
  "type": "EMAIL",
  "name": "string",
  "configuration": {
    "smtpServer": "string",
    "smtpPort": "number",
    "username": "string",
    "password": "string",
    "fromAddress": "string",
    "toAddresses": ["string"]
  },
  "enabled": "boolean"
}
```

**Тело запроса (для Telegram):**

```json
{
  "type": "TELEGRAM",
 "name": "string",
  "configuration": {
    "botToken": "string",
    "chatId": "string"
  },
  "enabled": "boolean"
}
```

**Тело запроса (для Webhook):**

```json
{
  "type": "WEBHOOK",
  "name": "string",
  "configuration": {
    "url": "string",
    "method": "GET|POST|PUT|DELETE",
    "headers": {
      "key": "string"
    },
    "bodyTemplate": "string"
  },
  "enabled": "boolean"
}
```

**Ответ:**

```json
{
  "id": "string",
  "type": "EMAIL|TELEGRAM|WEBHOOK",
  "name": "string",
  "configuration": {
    // Конфигурация зависит от типа канала
  },
  "enabled": "boolean",
  "createdAt": "ISO8601 datetime",
  "updatedAt": "ISO8601 datetime"
}
```

#### Обновить канал уведомлений

```text
PUT /api/v1/notificator/channels/{channelId}
```

**Тело запроса:**
(Аналогично созданию, но без обязательного указания типа)

**Ответ:**

```json
{
  "id": "string",
  "type": "EMAIL|TELEGRAM|WEBHOOK",
  "name": "string",
  "configuration": {
    // Конфигурация зависит от типа канала
  },
  "enabled": "boolean",
  "createdAt": "ISO8601 datetime",
  "updatedAt": "ISO8601 datetime"
}
```

#### Удалить канал уведомлений

```text
DELETE /api/v1/notificator/channels/{channelId}
```

**Ответ:**

```json
{
  "message": "Channel deleted successfully"
}
```

#### Получить список правил уведомлений

```text
GET /api/v1/notificator/rules
```

**Параметры запроса:**

- `page` (опционально) - номер страницы (по умолчанию 1)
- `size` (опционально) - количество элементов на странице (по умолчанию 20)

**Ответ:**

```json
{
  "rules": [
    {
      "id": "string",
      "name": "string",
      "conditions": [
        {
          "field": "string",
          "operator": "EQUALS|NOT_EQUALS|CONTAINS|NOT_CONTAINS|GREATER_THAN|LESS_THAN",
          "value": "string"
        }
      ],
      "actions": [
        {
          "type": "SEND_NOTIFICATION",
          "channelId": "string"
        }
      ],
      "enabled": "boolean",
      "createdAt": "ISO8601 datetime",
      "updatedAt": "ISO8601 datetime"
    }
  ],
  "page": "number",
  "size": "number",
  "total": "number"
}
```

#### Получить информацию о конкретном правиле уведомлений

```text
GET /api/v1/notificator/rules/{ruleId}
```

**Ответ:**

```json
{
  "id": "string",
  "name": "string",
  "conditions": [
    {
      "field": "string",
      "operator": "EQUALS|NOT_EQUALS|CONTAINS|NOT_CONTAINS|GREATER_THAN|LESS_THAN",
      "value": "string"
    }
  ],
  "actions": [
    {
      "type": "SEND_NOTIFICATION",
      "channelId": "string"
    }
  ],
  "enabled": "boolean",
  "createdAt": "ISO8601 datetime",
  "updatedAt": "ISO8601 datetime"
}
```

#### Создать новое правило уведомлений

```text
POST /api/v1/notificator/rules
```

**Тело запроса:**

```json
{
  "name": "string",
  "conditions": [
    {
      "field": "string",
      "operator": "EQUALS|NOT_EQUALS|CONTAINS|NOT_CONTAINS|GREATER_THAN|LESS_THAN",
      "value": "string"
    }
  ],
  "actions": [
    {
      "type": "SEND_NOTIFICATION",
      "channelId": "string"
    }
  ],
  "enabled": "boolean"
}
```

**Ответ:**

```json
{
  "id": "string",
  "name": "string",
  "conditions": [
    {
      "field": "string",
      "operator": "EQUALS|NOT_EQUALS|CONTAINS|NOT_CONTAINS|GREATER_THAN|LESS_THAN",
      "value": "string"
    }
  ],
  "actions": [
    {
      "type": "SEND_NOTIFICATION",
      "channelId": "string"
    }
  ],
  "enabled": "boolean",
  "createdAt": "ISO8601 datetime",
  "updatedAt": "ISO8601 datetime"
}
```

#### Обновить правило уведомлений

```text
PUT /api/v1/notificator/rules/{ruleId}
```

**Тело запроса:**
(Аналогично созданию)

**Ответ:**

```json
{
  "id": "string",
  "name": "string",
  "conditions": [
    {
      "field": "string",
      "operator": "EQUALS|NOT_EQUALS|CONTAINS|NOT_CONTAINS|GREATER_THAN|LESS_THAN",
      "value": "string"
    }
  ],
  "actions": [
    {
      "type": "SEND_NOTIFICATION",
      "channelId": "string"
    }
  ],
  "enabled": "boolean",
  "createdAt": "ISO8601 datetime",
  "updatedAt": "ISO8601 datetime"
}
```

#### Удалить правило уведомлений

```text
DELETE /api/v1/notificator/rules/{ruleId}
```

**Ответ:**

```json
{
  "message": "Rule deleted successfully"
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

Все эндпоинты Settings Manager API требуют аутентификации через OAuth2 токен. Поддерживаются следующие роли:

- `PINGTOWER_USER` - базовый доступ к API
- `PINGTOWER_EDITOR` - возможность изменения настроек
- `PINGTOWER_ADMIN` - полный доступ ко всем функциям системы

Чувствительные данные (например, пароли, токены) шифруются перед сохранением в базу данных.

## 4. Ограничения

- Максимальное количество настроек на одного пользователя: 10000
- Максимальная длина значения настройки: 100000 символов
- Максимальное количество пользователей: 1000
- Максимальное количество ролей: 100
