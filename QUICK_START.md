# PingTower Docker - Быстрый старт

## 🚀 Быстрый запуск

```bash
# 1. Создать .env файл
cp .env.example .env

# 2. Запустить все сервисы
./docker-manage.sh start

# 3. Проверить статус
./docker-manage.sh status
```

## 📋 Доступ к сервисам

### Веб-интерфейсы

- **Traefik Dashboard**: <http://localhost:8080>
- **Pinger Service**: <http://localhost/pinger>
- **Notificator**: <http://localhost/notificator>
- **Settings Manager**: <http://localhost/settings>
- **Statistics**: <http://localhost/statistics>

### Базы данных

- **PostgreSQL**: localhost:5432
- **Cassandra**: localhost:9042
- **Redis**: localhost:6379
- **Kafka (RedPanda)**: localhost:9092

## 🛠 Основные команды

### Управление сервисами

```bash
./docker-manage.sh start          # Запустить все
./docker-manage.sh stop           # Остановить все
./docker-manage.sh status         # Статус сервисов
./docker-manage.sh logs           # Логи всех сервисов
```

### Доступ к базам данных

```bash
./docker-manage.sh exec postgres psql -U pingtower
./docker-manage.sh exec cassandra cqlsh
./docker-manage.sh exec redis redis-cli -a pingtower
./docker-manage.sh exec redpanda rpk topic list
```

### Сборка

```bash
./docker-manage.sh build          # Собрать все сервисы
./docker-manage.sh build pingtower-pinger  # Собрать конкретный сервис
```

## 📊 Мониторинг

### Логи

```bash
./docker-manage.sh logs           # Все логи
./docker-manage.sh logs postgres  # Логи PostgreSQL
./docker-manage.sh logs cassandra # Логи Cassandra
./docker-manage.sh logs pingtower-pinger  # Логи сервиса
```

### Health check

```bash
./docker-manage.sh status         # Проверить все сервисы
docker compose ps                 # Альтернатива
```

## 🧹 Очистка

```bash
./docker-manage.sh stop           # Остановить сервисы
./docker-manage.sh stop-clean     # Остановить с удалением данных
./docker-manage.sh cleanup        # Полная очистка
```

## 📖 Подробная документация

Смотрите [DOCKER_README.md](DOCKER_README.md) для полной документации.
