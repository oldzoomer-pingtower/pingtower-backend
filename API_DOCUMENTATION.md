# Документация API PingTower Backend

## Обзор

PingTower - это комплексная система мониторинга, состоящая из нескольких микросервисов, которые совместно работают для мониторинга доступности ресурсов, управления уведомлениями и предоставления статистического анализа. Система включает следующие сервисы:

- **Pinger**: Обрабатывает проверки доступности ресурсов (HTTP, TCP, DNS и т.д.)
- **Notificator**: Управляет каналами уведомлений и правилами для оповещений
- **Settings Manager**: Предоставляет управление пользователями, ролями и системными настройками
- **Statistics**: Собирает и анализирует данные мониторинга

Эта документация API охватывает все REST endpoints всех этих сервисов.

## Аутентификация

Все endpoints API требуют аутентификации с использованием JWT токенов Keycloak. Система использует конфигурацию OAuth2 Resource Server с JWT authentication converter, который извлекает роли из JWT токена.

### Заголовок аутентификации

```text
Authorization: Bearer <jwt_token>
```

### Роли и разрешения

- `ADMIN`: Полный доступ ко всем endpoints
- Контроль доступа на основе ролей применяется для каждого endpoint

### Структура JWT токена

JWT токены выдаются Keycloak и содержат информацию о пользователе и ролях. Authentication converter добавляет префикс `ROLE_` к ролям (например, `ROLE_ADMIN`).

## Общие форматы ответов

### Успешные ответы

Все успешные ответы возвращают JSON данные с соответствующими HTTP статус кодами.

### Ответы с ошибками

Стандартный формат ответа с ошибкой:

```json
{
  "error": "Тип ошибки",
  "message": "Подробное сообщение об ошибке"
}
```

Общие HTTP статус коды:

- `200`: Успех
- `201`: Создано
- `204`: Нет содержимого
- `400`: Некорректный запрос (ошибки валидации)
- `401`: Не авторизован
- `403`: Запрещено
- `404`: Не найдено
- `500`: Внутренняя ошибка сервера

### Ошибки валидации

Ошибки валидации возвращают специфичные для полей сообщения:

```json
{
  "fieldName": "Сообщение об ошибке для этого поля"
}
```

---

## Сервис Pinger

Базовый URL: `/api/v1/pinger`

### Endpoints конфигураций проверок

#### Получить все конфигурации проверок

- **Метод**: `GET`
- **Endpoint**: `/api/v1/pinger/checks`
- **Авторизация**: Требуется `ROLE_ADMIN`
- **Параметры запроса**:
  - `page` (integer, по умолчанию 1): Номер страницы
  - `size` (integer, по умолчанию 20): Элементов на странице
- **Ответ**: `200 OK`

  ```json
  {
    "data": [
      {
        "id": "check-12345",
        "type": "HTTP",
        "resourceUrl": "https://example.com",
        "frequency": 60000,
        "timeout": 5000,
        "expectedStatusCode": 200,
        "expectedResponseTime": 1000,
        "validateSsl": true,
        "createdAt": "2024-01-15T10:30:00",
        "updatedAt": "2024-01-15T10:30:00"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "currentPage": 1,
    "size": 20
  }
  ```

#### Получить конфигурацию проверки по ID

- **Метод**: `GET`
- **Endpoint**: `/api/v1/pinger/checks/{checkId}`
- **Авторизация**: Требуется `ROLE_ADMIN`
- **Параметры пути**:
  - `checkId` (string): ID конфигурации проверки
- **Ответ**: `200 OK`

  ```json
  {
    "id": "check-12345",
    "type": "HTTP",
    "resourceUrl": "https://example.com",
    "frequency": 60000,
    "timeout": 5000,
    "expectedStatusCode": 200,
    "expectedResponseTime": 1000,
    "validateSsl": true,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
  ```

- **Ответы с ошибками**:
  - `404 Not Found`: Проверка не найдена

#### Создать конфигурацию проверки

- **Метод**: `POST`
- **Endpoint**: `/api/v1/pinger/checks`
- **Авторизация**: Требуется `ROLE_ADMIN`
- **Тело запроса**:

  ```json
  {
    "type": "HTTP",
    "resourceUrl": "https://example.com",
    "frequency": 60000,
    "timeout": 5000,
    "expectedStatusCode": 200,
    "expectedResponseTime": 1000,
    "validateSsl": true
  }
  ```

- **Ответ**: `200 OK` - Возвращает созданную конфигурацию

#### Обновить конфигурацию проверки

- **Метод**: `PUT`
- **Endpoint**: `/api/v1/pinger/checks/{checkId}`
- **Авторизация**: Требуется `ROLE_ADMIN`
- **Параметры пути**:
  - `checkId` (string): ID конфигурации проверки
- **Тело запроса**: То же, что и при создании
- **Ответ**: `200 OK` - Возвращает обновленную конфигурацию
- **Ответы с ошибками**:
  - `404 Not Found`: Проверка не найдена

#### Удалить конфигурацию проверки

- **Метод**: `DELETE`
- **Endpoint**: `/api/v1/pinger/checks/{checkId}`
- **Авторизация**: Требуется `ROLE_ADMIN`
- **Параметры пути**:
  - `checkId` (string): ID конфигурации проверки
- **Ответ**: `200 OK`

  ```json
  {
    "message": "Check deleted successfully"
  }
  ```

- **Ответы с ошибками**:
  - `404 Not Found`: Проверка не найдена

### Схема конфигурации проверки

```json
{
  "id": "string",
  "type": "HTTP|HTTPS|TCP|PING",
  "resourceUrl": "string",
  "frequency": "integer",
  "timeout": "integer",
  "expectedStatusCode": "integer",
  "expectedResponseTime": "integer",
  "validateSsl": "boolean",
  "createdAt": "string",
  "updatedAt": "string"
}
```

---

## Сервис Notificator

Базовый URL: `/api/v1/notificator`

### Endpoints каналов уведомлений

#### Получить все каналы уведомлений

- **Метод**: `GET`
- **Endpoint**: `/api/v1/notificator/channels`
- **Авторизация**: Требуется `ROLE_ADMIN`
- **Параметры запроса**:
  - `page` (integer, по умолчанию 1)
  - `size` (integer, по умолчанию 20)
  - `type` (string): Фильтр по типу канала (EMAIL, TELEGRAM, WEBHOOK)
- **Ответ**: `200 OK` - Пагинированный список каналов

#### Получить канал уведомлений по ID

- **Метод**: `GET`
- **Endpoint**: `/api/v1/notificator/channels/{channelId}`
- **Авторизация**: Требуется `ROLE_ADMIN`
- **Параметры пути**:
  - `channelId` (string): ID канала
- **Ответ**: `200 OK` - Детали канала
- **Ответы с ошибками**:
  - `404 Not Found`

#### Создать канал уведомлений

- **Метод**: `POST`
- **Endpoint**: `/api/v1/notificator/channels`
- **Авторизация**: Требуется `ROLE_ADMIN`
- **Тело запроса**:

  ```json
  {
    "type": "EMAIL",
    "name": "Email администратора",
    "configuration": {
      "email": "admin@example.com"
    },
    "enabled": true
  }
  ```

- **Ответ**: `200 OK` - Созданный канал

#### Обновить канал уведомлений

- **Метод**: `PUT`
- **Endpoint**: `/api/v1/notificator/channels/{channelId}`
- **Авторизация**: Требуется `ROLE_ADMIN`
- **Параметры пути**:
  - `channelId` (string): ID канала
- **Тело запроса**: То же, что и при создании
- **Ответ**: `200 OK` - Обновленный канал
- **Ответы с ошибками**:
  - `404 Not Found`

#### Удалить канал уведомлений

- **Метод**: `DELETE`
- **Endpoint**: `/api/v1/notificator/channels/{channelId}`
- **Авторизация**: Требуется `ROLE_ADMIN`
- **Параметры пути**:
  - `channelId` (string): ID канала
- **Ответ**: `200 OK`

  ```json
  {
    "message": "Channel deleted successfully"
  }
  ```

- **Ответы с ошибками**:
  - `404 Not Found`

### Endpoints правил уведомлений

#### Получить все правила уведомлений

- **Метод**: `GET`
- **Endpoint**: `/api/v1/notificator/rules`
- **Авторизация**: Требуется `ROLE_ADMIN`
- **Параметры запроса**:
  - `page` (integer, по умолчанию 1)
  - `size` (integer, по умолчанию 20)
- **Ответ**: `200 OK` - Пагинированный список правил

#### Получить правило уведомлений по ID

- **Метод**: `GET`
- **Endpoint**: `/api/v1/notificator/rules/{ruleId}`
- **Авторизация**: Требуется `ROLE_ADMIN`
- **Параметры пути**:
  - `ruleId` (string): ID правила
- **Ответ**: `200 OK` - Детали правила
- **Ответы с ошибками**:
  - `404 Not Found`

#### Создать правило уведомлений

- **Метод**: `POST`
- **Endpoint**: `/api/v1/notificator/rules`
- **Авторизация**: Требуется `ROLE_ADMIN`
- **Тело запроса**:

  ```json
  {
    "name": "Уведомление о недоступности",
    "conditions": [
      {
        "field": "status",
        "operator": "EQUALS",
        "value": "FAILURE"
      }
    ],
    "actions": [
      {
        "type": "SEND_NOTIFICATION",
        "channelId": "channel-12345"
      }
    ],
    "enabled": true
  }
  ```

- **Ответ**: `200 OK` - Созданное правило

#### Обновить правило уведомлений

- **Метод**: `PUT`
- **Endpoint**: `/api/v1/notificator/rules/{ruleId}`
- **Авторизация**: Требуется `ROLE_ADMIN`
- **Параметры пути**:
  - `ruleId` (string): ID правила
- **Тело запроса**: То же, что и при создании
- **Ответ**: `200 OK` - Обновленное правило
- **Ответы с ошибками**:
  - `404 Not Found`

#### Удалить правило уведомлений

- **Метод**: `DELETE`
- **Endpoint**: `/api/v1/notificator/rules/{ruleId}`
- **Авторизация**: Требуется `ROLE_ADMIN`
- **Параметры пути**:
  - `ruleId` (string): ID правила
- **Ответ**: `200 OK`

  ```json
  {
    "message": "Rule deleted successfully"
  }
  ```

- **Ответы с ошибками**:
  - `404 Not Found`

### Endpoint проверки здоровья

- **Метод**: `GET`
- **Endpoint**: `/api/v1/notificator/health`
- **Авторизация**: Не требуется
- **Ответ**: `200 OK`

  ```json
  {
    "status": "UP",
    "timestamp": "2024-01-15T10:30:00",
    "service": "Notificator",
    "version": "1.0.0"
  }
  ```

### Endpoint статистики

- **Метод**: `GET`
- **Endpoint**: `/api/v1/notificator/stats`
- **Авторизация**: Не требуется
- **Ответ**: `200 OK`

  ```json
  {
    "activeChannels": 0,
    "activeRules": 0,
    "notificationsSent": 0,
    "notificationsFailed": 0,
    "timestamp": "2024-01-15T10:30:00"
  }
  ```

### Схема канала уведомлений

```json
{
  "id": "string",
  "type": "EMAIL|TELEGRAM|WEBHOOK",
  "name": "string",
  "configuration": "object",
  "enabled": "boolean",
  "createdAt": "string",
  "updatedAt": "string"
}
```

### Схема правила уведомлений

```json
{
  "id": "string",
  "name": "string",
  "conditions": [
    {
      "field": "string",
      "operator": "EQUALS|NOT_EQUALS|GREATER_THAN|LESS_THAN|CONTAINS",
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
  "createdAt": "string",
  "updatedAt": "string"
}
```

---

## Сервис Settings Manager

Базовый URL: `/api/v1`

### Endpoints настроек

#### Получить все настройки

- **Метод**: `GET`
- **Endpoint**: `/api/v1/settings`
- **Авторизация**: Требуется соответствующая роль
- **Ответ**: `200 OK` - Массив настроек

#### Получить настройки по модулю

- **Метод**: `GET`
- **Endpoint**: `/api/v1/settings/{module}`
- **Авторизация**: Требуется соответствующая роль
- **Параметры пути**:
  - `module` (string): Название модуля
- **Ответ**: `200 OK` - Массив настроек модуля

#### Получить настройку по модулю и ключу

- **Метод**: `GET`
- **Endpoint**: `/api/v1/settings/{module}/{key}`
- **Авторизация**: Требуется соответствующая роль
- **Параметры пути**:
  - `module` (string): Название модуля
  - `key` (string): Ключ настройки
- **Ответ**: `200 OK` - Объект настройки
- **Ответы с ошибками**:
  - `404 Not Found`

#### Создать настройку

- **Метод**: `POST`
- **Endpoint**: `/api/v1/settings`
- **Авторизация**: Требуется соответствующая роль
- **Тело запроса**:

  ```json
  {
    "module": "pinger",
    "key": "timeout",
    "value": "5000",
    "description": "Таймаут проверки в миллисекундах"
  }
  ```

- **Ответ**: `200 OK` - Созданная настройка

#### Обновить настройку

- **Метод**: `PUT`
- **Endpoint**: `/api/v1/settings/{module}/{key}`
- **Авторизация**: Требуется соответствующая роль
- **Параметры пути**:
  - `module` (string): Название модуля
  - `key` (string): Ключ настройки
- **Тело запроса**: Объект настройки (то же, что и при создании)
- **Ответ**: `200 OK` - Обновленная настройка
- **Ответы с ошибками**:
  - `404 Not Found`

#### Удалить настройку

- **Метод**: `DELETE`
- **Endpoint**: `/api/v1/settings/{module}/{key}`
- **Авторизация**: Требуется соответствующая роль
- **Параметры пути**:
  - `module` (string): Название модуля
  - `key` (string): Ключ настройки
- **Ответ**: `204 No Content`
- **Ответы с ошибками**:
  - `404 Not Found`

### Endpoints пользователей

#### Получить всех пользователей

- **Метод**: `GET`
- **Endpoint**: `/api/v1/users`
- **Авторизация**: Требуется соответствующая роль
- **Ответ**: `200 OK` - Массив пользователей

#### Получить пользователя по ID

- **Метод**: `GET`
- **Endpoint**: `/api/v1/users/{id}`
- **Авторизация**: Требуется соответствующая роль
- **Параметры пути**:
  - `id` (UUID): ID пользователя
- **Ответ**: `200 OK` - Объект пользователя
- **Ответы с ошибками**:
  - `404 Not Found`

#### Создать пользователя

- **Метод**: `POST`
- **Endpoint**: `/api/v1/users`
- **Авторизация**: Требуется соответствующая роль
- **Тело запроса**:

  ```json
  {
    "username": "john_doe",
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }
  ```

- **Ответ**: `200 OK` - Созданный пользователь

#### Обновить пользователя

- **Метод**: `PUT`
- **Endpoint**: `/api/v1/users/{id}`
- **Авторизация**: Требуется соответствующая роль
- **Параметры пути**:
  - `id` (UUID): ID пользователя
- **Тело запроса**: Объект пользователя
- **Ответ**: `200 OK` - Обновленный пользователь
- **Ответы с ошибками**:
  - `404 Not Found`

#### Удалить пользователя

- **Метод**: `DELETE`
- **Endpoint**: `/api/v1/users/{id}`
- **Авторизация**: Требуется соответствующая роль
- **Параметры пути**:
  - `id` (UUID): ID пользователя
- **Ответ**: `204 No Content`
- **Ответы с ошибками**:
  - `404 Not Found`

### Endpoints ролей

#### Получить все роли

- **Метод**: `GET`
- **Endpoint**: `/api/v1/roles`
- **Авторизация**: Требуется соответствующая роль
- **Ответ**: `200 OK` - Массив ролей

#### Получить роль по ID

- **Метод**: `GET`
- **Endpoint**: `/api/v1/roles/{id}`
- **Авторизация**: Требуется соответствующая роль
- **Параметры пути**:
  - `id` (UUID): ID роли
- **Ответ**: `200 OK` - Объект роли
- **Ответы с ошибками**:
  - `404 Not Found`

#### Создать роль

- **Метод**: `POST`
- **Endpoint**: `/api/v1/roles`
- **Авторизация**: Требуется соответствующая роль
- **Тело запроса**:

  ```json
  {
    "name": "ADMIN",
    "description": "Администратор системы",
    "permissions": ["users.read", "users.write"]
  }
  ```

- **Ответ**: `200 OK` - Созданная роль

#### Обновить роль

- **Метод**: `PUT`
- **Endpoint**: `/api/v1/roles/{id}`
- **Авторизация**: Требуется соответствующая роль
- **Параметры пути**:
  - `id` (UUID): ID роли
- **Тело запроса**: Объект роли
- **Ответ**: `200 OK` - Обновленная роль
- **Ответы с ошибками**:
  - `404 Not Found`

#### Удалить роль

- **Метод**: `DELETE`
- **Endpoint**: `/api/v1/roles/{id}`
- **Авторизация**: Требуется соответствующая роль
- **Параметры пути**:
  - `id` (UUID): ID роли
- **Ответ**: `204 No Content`
- **Ответы с ошибками**:
  - `404 Not Found`

### Endpoints управления связями пользователей и ролей

#### Получить роли пользователя

- **Метод**: `GET`
- **Endpoint**: `/api/v1/user-roles/user/{userId}`
- **Авторизация**: Требуется соответствующая роль
- **Параметры пути**:
  - `userId` (UUID): ID пользователя
- **Ответ**: `200 OK` - Массив UUID ролей

#### Получить пользователей с ролью

- **Метод**: `GET`
- **Endpoint**: `/api/v1/user-roles/role/{roleId}`
- **Авторизация**: Требуется соответствующая роль
- **Параметры пути**:
  - `roleId` (UUID): ID роли
- **Ответ**: `200 OK` - Массив UUID пользователей

#### Назначить роль пользователю

- **Метод**: `POST`
- **Endpoint**: `/api/v1/user-roles/{userId}/{roleId}`
- **Авторизация**: Требуется соответствующая роль
- **Параметры пути**:
  - `userId` (UUID): ID пользователя
  - `roleId` (UUID): ID роли
- **Ответ**: `200 OK`

#### Удалить роль у пользователя

- **Метод**: `DELETE`
- **Endpoint**: `/api/v1/user-roles/{userId}/{roleId}`
- **Авторизация**: Требуется соответствующая роль
- **Параметры пути**:
  - `userId` (UUID): ID пользователя
  - `roleId` (UUID): ID роли
- **Ответ**: `204 No Content`

#### Удалить все роли у пользователя

- **Метод**: `DELETE`
- **Endpoint**: `/api/v1/user-roles/user/{userId}`
- **Авторизация**: Требуется соответствующая роль
- **Параметры пути**:
  - `userId` (UUID): ID пользователя
- **Ответ**: `204 No Content`

### Схема настройки

```json
{
  "id": "UUID",
  "module": "string",
  "key": "string",
  "value": "string",
  "description": "string",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime",
  "version": "integer"
}
```

### Схема пользователя

```json
{
  "id": "UUID",
  "username": "string",
  "email": "string",
  "firstName": "string",
  "lastName": "string",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

### Схема роли

```json
{
  "id": "UUID",
  "name": "string",
  "description": "string",
  "permissions": ["string"],
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

---

## Сервис Statistics

Базовый URL: `/api/v1/statistics`

### Endpoints результатов проверок

#### Получить последний результат проверки

- **Метод**: `GET`
- **Endpoint**: `/api/v1/statistics/checks/{checkId}/latest`
- **Авторизация**: Требуется соответствующая роль
- **Параметры пути**:
  - `checkId` (string): ID проверки
- **Ответ**: `200 OK` - Последний результат

  ```json
  {
    "checkId": "check-12345",
    "resourceUrl": "https://example.com",
    "timestamp": "2024-01-15T10:30:00",
    "status": "SUCCESS",
    "responseTime": 150,
    "httpStatusCode": 200,
    "errorMessage": null,
    "metrics": {
      "connectionTime": 50,
      "timeToFirstByte": 100,
      "sslValid": true,
      "sslExpirationDate": "2024-12-31T23:59:59"
    }
  }
  ```

- **Ответы с ошибками**:
  - `404 Not Found`

#### Получить историю результатов проверки

- **Метод**: `GET`
- **Endpoint**: `/api/v1/statistics/checks/{checkId}/history`
- **Авторизация**: Требуется соответствующая роль
- **Параметры пути**:
  - `checkId` (string): ID проверки
- **Параметры запроса**:
  - `from` (ISO date time): Начальная дата
  - `to` (ISO date time): Конечная дата
  - `limit` (integer, по умолчанию 100): Максимальное количество записей
  - `offset` (integer, по умолчанию 0): Смещение для пагинации
- **Ответ**: `200 OK` - Массив результатов

#### Получить агрегированные данные

- **Метод**: `GET`
- **Endpoint**: `/api/v1/statistics/checks/{checkId}/aggregated`
- **Авторизация**: Требуется соответствующая роль
- **Параметры пути**:
  - `checkId` (string): ID проверки
- **Параметры запроса**:
  - `interval` (string): Интервал агрегации (например, "hourly")
  - `from` (ISO date time): Начальная дата
  - `to` (ISO date time): Конечная дата
- **Ответ**: `200 OK` - Агрегированные данные
- **Ответы с ошибками**:
  - `404 Not Found`

#### Получить данные для дашборда

- **Метод**: `GET`
- **Endpoint**: `/api/v1/statistics/dashboard`
- **Авторизация**: Требуется соответствующая роль
- **Ответ**: `200 OK` - Данные для дашборда

### Схема результата проверки

```json
{
  "checkId": "string",
  "resourceUrl": "string",
  "timestamp": "LocalDateTime",
  "status": "SUCCESS|FAILURE|TIMEOUT",
  "responseTime": "long",
  "httpStatusCode": "integer",
  "errorMessage": "string",
  "metrics": {
    "connectionTime": "long",
    "timeToFirstByte": "long",
    "sslValid": "boolean",
    "sslExpirationDate": "LocalDateTime"
  }
}
```

---

## Обработка ошибок

Все сервисы реализуют последовательную обработку ошибок через GlobalExceptionHandler:

### Ошибки валидации (400 Bad Request)

```json
{
  "fieldName": "Сообщение об ошибке для этого поля"
}
```

### Общие ошибки (500 Internal Server Error)

```json
{
  "error": "Внутренняя ошибка сервера",
  "message": "Подробное сообщение об ошибке"
}
```

### Ошибки базы данных (500 Internal Server Error)

```json
{
  "error": "Ошибка базы данных",
  "message": "Произошла ошибка при доступе к базе данных"
}
```

### Ошибки "Не найдено" (404 Not Found)

```json
{
  "error": "Не найдено",
  "message": "Запрошенный ресурс не найден"
}
```

---

## Примеры использования API

### Полный пример рабочего процесса

1. **Создать конфигурацию проверки:**

   ```bash
   POST /api/v1/pinger/checks
   Authorization: Bearer <jwt_token>
   Content-Type: application/json

   {
     "type": "HTTP",
     "resourceUrl": "https://example.com",
     "frequency": 60000,
     "timeout": 5000,
     "expectedStatusCode": 200
   }
   ```

2. **Создать канал уведомлений:**

   ```bash
   POST /api/v1/notificator/channels
   Authorization: Bearer <jwt_token>
   Content-Type: application/json

   {
     "type": "EMAIL",
     "name": "Email администратора",
     "configuration": {
       "email": "admin@example.com"
     },
     "enabled": true
   }
   ```

3. **Создать правило уведомлений:**

   ```bash
   POST /api/v1/notificator/rules
   Authorization: Bearer <jwt_token>
   Content-Type: application/json

   {
     "name": "Уведомление о недоступности",
     "conditions": [
       {
         "field": "status",
         "operator": "EQUALS",
         "value": "FAILURE"
       }
     ],
     "actions": [
       {
         "type": "SEND_NOTIFICATION",
         "channelId": "channel-uuid"
       }
     ],
     "enabled": true
   }
   ```

4. **Проверить статистику:**

   ```bash
   GET /api/v1/statistics/checks/check-id/latest
   Authorization: Bearer <jwt_token>
   ```

Эта документация предоставляет полный обзор API PingTower. Все endpoints требуют правильной аутентификации и следуют RESTful соглашениям.
