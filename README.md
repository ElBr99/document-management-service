# Document Management Service

```
Разработан Backend-сервис по работе c документами.
Документы создаются, переводятся по статусам (DRAFT → SUBMITTED → APPROVED), по изменениям статуса ведётся
история. При переводе документа в статус APPROVED создается запись в реестре утверждений. 
Дополнительно создана утилита для массового создания документов (утилита реализована в рамках этого же микросервиса, отдельным API )
Также, реализована фоновая обработка документов пачками.
```
## Стек

```
Java 17 + Spring Boot
PostgreSQL 16 (Docker Compose)
JPA/Hibernate, 
Liquibase, 
Maven,
Testcontainers для интеграционных тестов`
```

## Запуск приложения и его компонентов 

```
1. Реляционная бд PostgreSQL

- Запустить Docker
- выполнить в терминале IntelliJ IDEA команду: docker compose up -d --build

2. Сам backend-микросервис (запуск кнопкой Run)
```
![img_1.png](img_1.png)

Сервис стартует на http://localhost:8083.
Liquibase автоматически накатывает скрипты миграции (схема бд, реляционное представление сущностей JPA)

## Утилита генерации документов

Утилита реализована отдельным API в рамках текущего микросервиса.
Для того, чтобы воспользоваться автоматической генерацией документов, нужно отправить запрос:

`
POST http://localhost:8083/api/utils/generate-documents
`
Размер батча задан в application.properties файле и его можно переопределить:
`
util.generate-document.batch-size=100
`

## Фоновые процессы (шедуллеры)

В сервисе есть две джобы, которые запускаются при старте сервиса.
```
SubmitWorker каждые 10 сек находит документы DRAFT и переводит их в SUBMITTED
ApproveWorker каждые 10 сек находит документы SUBMITTED и переводит в APPROVED
```

Периодичность запуска шедуллеров - это настраиваемый параметр, который задется в application.properties.
Также параметры настроек можно переопределить через environment переменные.

Настройка шедуллеров (service/src/main/resources/application.properties):
```
document.job.move-to-submitted.cron=0 0/10 * * * ?
document.job.move-to-approved.cron=0 0/10 * * * ?
document.job.move-to-submitted-batch-size=100
document.job.move-to-approved-batch-size=50
```