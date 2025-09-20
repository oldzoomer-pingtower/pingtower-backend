# PingTower Docker Setup

Полная Docker конфигурация для запуска PingTower - системы мониторинга и уведомлений.

## Архитектура

Система состоит из следующих компонентов:

- **PostgreSQL** - основная база данных
- **Cassandra** - распределенная NoSQL база данных для статистики
- **Redis** - кэширование и сессии
- **RedPanda** - Kafka совместимая очередь сообщений
- **Traefik** - reverse proxy и load balancer
- **Микросервисы**:
  - `pingtower-pinger` - сервис проверки доступности
  - `pingtower-notificator` - сервис управления уведомлениями
  - `pingtower-settings-manager` - сервис управления настройками
  - `pingtower-statistics` - сервис статистики и агрегации

## Требования

- Docker 20.10+
- Docker Compose v2.0+

## Быстрый старт

1. **Клонируйте репозиторий:**

   ```bash
   git clone <repository-url>
   cd pingtower-backend
   ```

2. **Создайте .env файл:**

   ```bash
   cp .env.example .env
   ```

   Отредактируйте `.env` файл, указав необходимые переменные окружения.

3. **Запустите сервисы:**

   ```bash
   docker compose up -d
   ```

4. **Проверьте статус:**

   ```bash
   docker compose ps
   ```

## Структура проекта

```text
├── Dockerfile              # Мульти-стадийная сборка для всех сервисов
├── compose.yml            # Docker Compose конфигурация
├── .env.example           # Пример переменных окружения
├── traefik/
│   └── traefik.yml        # Конфигурация Traefik
├── config/                # Общие конфигурации
├── lib/                   # Общие библиотеки
├── notificator/           # Сервис уведомлений
├── pinger/                # Сервис проверки доступности
├── settings-manager/      # Сервис управления настройками
└── statistics/            # Сервис статистики
```

## Переменные окружения

### Обязательные

- `DOMAIN` - домен приложения (по умолчанию: localhost)
- `POSTGRES_USER` - пользователь PostgreSQL (по умолчанию: pingtower)
- `POSTGRES_PASSWORD` - пароль PostgreSQL (по умолчанию: pingtower)
- `POSTGRES_DB` - база данных PostgreSQL (по умолчанию: pingtower)
- `REDIS_PASSWORD` - пароль Redis (по умолчанию: pingtower)

### Опциональные

- `SPRING_PROFILES_ACTIVE` - профиль Spring (по умолчанию: docker)
- `LOG_LEVEL` - уровень логирования (по умолчанию: INFO)

## Сборка и запуск отдельных сервисов

### Сборка образа

```bash
# Сборка всех сервисов
docker compose build

# Сборка конкретного сервиса
docker compose build pingtower-pinger
```

### Запуск сервисов

```bash
# Запуск всех сервисов
docker compose up -d

# Запуск конкретного сервиса
docker compose up -d pingtower-pinger

# Запуск с логами
docker compose up pingtower-pinger
```

### Остановка сервисов

```bash
# Остановка всех сервисов
docker compose down

# Остановка с удалением volumes
docker compose down -v
```

## Доступ к сервисам

После запуска сервисы будут доступны по следующим адресам:

- **Traefik Dashboard**: <http://localhost:8080>
- **Pinger Service**: <http://localhost/pinger>
- **Notificator Service**: <http://localhost/notificator>
- **Settings Manager**: <http://localhost/settings>
- **Statistics Service**: <http://localhost/statistics>

## Мониторинг

### Логи сервисов

```bash
# Логи всех сервисов
docker compose logs

# Логи конкретного сервиса
docker compose logs pingtower-pinger

# Следить за логами в реальном времени
docker compose logs -f pingtower-pinger
```

### Health checks

```bash
# Проверить здоровье всех сервисов
docker compose ps

# Проверить здоровье конкретного сервиса
docker compose exec pingtower-pinger health
```

## База данных

### Подключение к PostgreSQL

```bash
# Через docker exec
docker compose exec postgres psql -U pingtower -d pingtower

# Через внешний клиент
psql -h localhost -p 5432 -U pingtower -d pingtower
```

### Миграции

Миграции базы данных выполняются автоматически через Flyway при запуске каждого сервиса.

## Cassandra

### Подключение к Cassandra

```bash
# Через docker exec
docker compose exec cassandra cqlsh

# Через внешний клиент
cqlsh localhost 9042
```

### Создание keyspace для statistics сервиса

```bash
# Подключиться к Cassandra
docker compose exec cassandra cqlsh

# Создать keyspace (если не создан автоматически)
CREATE KEYSPACE IF NOT EXISTS pingtower_statistics
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};

USE pingtower_statistics;
```

### Логи Cassandra

```bash
docker compose logs cassandra
```

## Redis

### Подключение к Redis

```bash
# Через docker exec
docker compose exec redis redis-cli -a pingtower

# Через внешний клиент
redis-cli -h localhost -p 6379 -a pingtower
```

## Kafka (RedPanda)

### Управление топиками

```bash
# Подключение к RedPanda
docker compose exec redpanda rpk topic create test-topic

# Список топиков
docker compose exec redpanda rpk topic list

# Произвести сообщение
docker compose exec redpanda rpk topic produce test-topic
```

## Traefik

### Доступ к dashboard

- URL: <http://localhost:8080>
- API: <http://localhost:8080/api>

### Конфигурация

Traefik автоматически обнаруживает и маршрутизирует трафик к сервисам через Docker labels.

## Разработка

### Локальная разработка с Docker

```bash
# Сборка и запуск в режиме разработки
docker compose -f compose.yml up --build pingtower-pinger

# Монтирование кода для hot-reload
# (требует настройки volumes в compose.yml)
```

### Отладка

```bash
# Вход в контейнер
docker compose exec pingtower-pinger sh

# Просмотр процессов
docker compose exec pingtower-pinger ps aux

# Проверка переменных окружения
docker compose exec pingtower-pinger env
```

## Производственная среда

Для production окружения рекомендуется:

1. **SSL/TLS**: Настроить HTTPS через Traefik или внешний load balancer
2. **Бэкапы**: Регулярное резервное копирование volumes PostgreSQL, Cassandra и RedPanda
3. **Мониторинг**: Настроить сбор метрик и алертинг
4. **Логи**: Настроить централизованный сбор логов
5. **Масштабирование**: Использовать Docker Swarm или Kubernetes

## Устранение неисправностей

### Сервис не запускается

```bash
# Проверить логи
docker compose logs [service-name]

# Перезапустить сервис
docker compose restart [service-name]

# Пересобрать сервис
docker compose build --no-cache [service-name]
```

### Проблемы с базой данных

```bash
# Проверить статус PostgreSQL
docker compose logs postgres

# Подключиться к PostgreSQL
docker compose exec postgres psql -U pingtower

# Сбросить данные PostgreSQL (удалит все данные!)
docker compose down -v postgres
docker compose up -d postgres
```

### Проблемы с Redis

```bash
# Проверить статус Redis
docker compose logs redis

# Подключиться к Redis
docker compose exec redis redis-cli -a pingtower ping
```

### Проблемы с Cassandra

```bash
# Проверить статус Cassandra
docker compose logs cassandra

# Подключиться к Cassandra
docker compose exec cassandra cqlsh

# Проверить состояние кластера
docker compose exec cassandra nodetool status

# Сбросить данные Cassandra (удалит все данные!)
docker compose down -v cassandra
docker compose up -d cassandra
```

## Безопасность

- Все пароли задаются через переменные окружения
- Redis защищен паролем
- Traefik настроен с минимальными правами
- Сервисы запускаются с non-root пользователем (где возможно)

## Обновление

```bash
# Остановить сервисы
docker compose down

# Получить обновления
git pull

# Пересобрать образы
docker compose build --no-cache

# Запустить сервисы
docker compose up -d
```

## Лицензия

Этот проект лицензирован под MIT License.
