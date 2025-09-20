#!/bin/bash

# PingTower Docker Management Script
# Упрощенное управление Docker Compose окружением

set -e

COMPOSE_FILE="compose.yml"
PROJECT_NAME="pingtower"

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Функции логирования
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Проверка наличия Docker и Docker Compose
check_dependencies() {
    if ! command -v docker &> /dev/null; then
        log_error "Docker не установлен"
        exit 1
    fi

    if ! command -v docker &> /dev/null || ! docker compose version &> /dev/null; then
        log_error "Docker Compose v2 не установлен"
        exit 1
    fi
}

# Создание .env файла из примера
setup_env() {
    if [ ! -f .env ]; then
        log_info "Создание .env файла из .env.example..."
        cp .env.example .env
        log_success ".env файл создан"
        log_warning "Пожалуйста, отредактируйте .env файл с вашими настройками"
    else
        log_info ".env файл уже существует"
    fi
}

# Сборка всех образов
build_all() {
    log_info "Сборка всех Docker образов..."
    docker compose -f $COMPOSE_FILE build --no-cache
    log_success "Все образы собраны"
}

# Сборка конкретного сервиса
build_service() {
    local service=$1
    if [ -z "$service" ]; then
        log_error "Укажите имя сервиса для сборки"
        echo "Пример: $0 build pingtower-pinger"
        exit 1
    fi

    log_info "Сборка сервиса $service..."
    docker compose -f $COMPOSE_FILE build $service
    log_success "Сервис $service собран"
}

# Запуск всех сервисов
start_all() {
    log_info "Запуск всех сервисов..."
    docker compose -f $COMPOSE_FILE -p $PROJECT_NAME up -d
    log_success "Все сервисы запущены"

    # Проверка здоровья
    sleep 5
    show_status
}

# Остановка всех сервисов
stop_all() {
    log_info "Остановка всех сервисов..."
    docker compose -f $COMPOSE_FILE -p $PROJECT_NAME down
    log_success "Все сервисы остановлены"
}

# Остановка с удалением volumes
stop_clean() {
    log_warning "Остановка всех сервисов с удалением данных..."
    read -p "Вы уверены? Это удалит все данные PostgreSQL, Cassandra и RedPanda (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        docker compose -f $COMPOSE_FILE -p $PROJECT_NAME down -v
        log_success "Все сервисы остановлены, volumes удалены"
    else
        log_info "Операция отменена"
    fi
}

# Показать статус сервисов
show_status() {
    log_info "Статус сервисов:"
    docker compose -f $COMPOSE_FILE -p $PROJECT_NAME ps

    echo
    log_info "Доступ к сервисам:"
    echo "  Traefik Dashboard: http://localhost:8080"
    echo "  Pinger Service:    http://localhost/pinger"
    echo "  Notificator:       http://localhost/notificator"
    echo "  Settings Manager:  http://localhost/settings"
    echo "  Statistics:        http://localhost/statistics"
    echo
    log_info "Базы данных:"
    echo "  PostgreSQL:        localhost:5432"
    echo "  Cassandra:         localhost:9042"
    echo "  Redis:             localhost:6379"
    echo "  Kafka (RedPanda):  localhost:9092"
}

# Показать логи
show_logs() {
    local service=$1
    if [ -z "$service" ]; then
        log_info "Логи всех сервисов (Ctrl+C для выхода):"
        docker compose -f $COMPOSE_FILE -p $PROJECT_NAME logs -f
    else
        log_info "Логи сервиса $service (Ctrl+C для выхода):"
        docker compose -f $COMPOSE_FILE -p $PROJECT_NAME logs -f $service
    fi
}

# Очистка ресурсов
cleanup() {
    log_warning "Очистка Docker ресурсов..."

    # Остановка всех контейнеров проекта
    docker compose -f $COMPOSE_FILE -p $PROJECT_NAME down 2>/dev/null || true

    # Удаление образов проекта
    log_info "Удаление образов проекта..."
    docker images "$PROJECT_NAME*" -q | xargs -r docker rmi -f 2>/dev/null || true

    # Удаление volumes проекта
    docker volume ls | grep "$PROJECT_NAME" | awk '{print $2}' | xargs -r docker volume rm 2>/dev/null || true

    log_success "Очистка завершена"
}

# Выполнить команду в контейнере
exec_service() {
    local service=$1
    local command=$2

    if [ -z "$service" ]; then
        log_error "Укажите имя сервиса"
        echo "Пример: $0 exec pingtower-pinger sh"
        exit 1
    fi

    if [ -z "$command" ]; then
        command="sh"
    fi

    log_info "Выполнение команды '$command' в сервисе $service..."
    docker compose -f $COMPOSE_FILE -p $PROJECT_NAME exec $service $command
}

# Показать справку
show_help() {
    echo "PingTower Docker Management Script"
    echo
    echo "Использование:"
    echo "  $0 setup          - Создать .env файл из примера"
    echo "  $0 build [service] - Собрать все образы или конкретный сервис"
    echo "  $0 start          - Запустить все сервисы"
    echo "  $0 stop           - Остановить все сервисы"
    echo "  $0 stop-clean     - Остановить сервисы с удалением данных"
    echo "  $0 status         - Показать статус сервисов"
    echo "  $0 logs [service] - Показать логи всех сервисов или конкретного"
    echo "  $0 exec service cmd - Выполнить команду в контейнере"
    echo "  $0 cleanup        - Очистить все ресурсы проекта"
    echo "  $0 help           - Показать эту справку"
    echo
    echo "Примеры:"
    echo "  $0 build pingtower-pinger"
    echo "  $0 logs pingtower-postgres"
    echo "  $0 logs cassandra"
    echo "  $0 exec cassandra cqlsh"
    echo "  $0 exec pingtower-pinger sh"
}

# Обработка аргументов
case "${1:-help}" in
    setup)
        check_dependencies
        setup_env
        ;;
    build)
        check_dependencies
        build_service "$2"
        ;;
    start)
        check_dependencies
        start_all
        ;;
    stop)
        check_dependencies
        stop_all
        ;;
    stop-clean)
        check_dependencies
        stop_clean
        ;;
    status)
        check_dependencies
        show_status
        ;;
    logs)
        check_dependencies
        show_logs "$2"
        ;;
    exec)
        check_dependencies
        exec_service "$2" "$3"
        ;;
    cleanup)
        check_dependencies
        cleanup
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        log_error "Неизвестная команда: $1"
        echo
        show_help
        exit 1
        ;;
esac