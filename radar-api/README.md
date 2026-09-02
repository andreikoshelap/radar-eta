# Bolt Radar — Tallinn MVP 0.1

Небольшой Spring Boot сервис для оценки зон Таллина по принципу:

**спрос + дефицит машин + внешние факторы − стоимость перемещения**

## Стек

- Java 21
- Spring Boot 4.1.1
- Spring Web
- Spring Data JPA
- H2 file database
- Flyway
- Gradle

## Запуск

```bash
./gradlew bootRun
```

API будет доступен на:

```text
http://localhost:8080
```

## Зоны

```bash
curl http://localhost:8080/api/zones
```

## Добавить текущую оценку зоны

Пример: Ülemiste, высокий спрос, ETA 6 минут:

```bash
curl -X POST http://localhost:8080/api/demand   -H "Content-Type: application/json"   -d '{
    "zoneCode": "ULEMISTE",
    "demand": 0.8,
    "pickupMinutes": 6,
    "airportPressure": 0.6,
    "ferryPressure": 0.0,
    "eventPressure": 0.0
  }'
```

Viru: высокий спрос, но очень много водителей:

```bash
curl -X POST http://localhost:8080/api/demand   -H "Content-Type: application/json"   -d '{
    "zoneCode": "VIRU",
    "demand": 0.9,
    "pickupMinutes": 2,
    "airportPressure": 0.0,
    "ferryPressure": 0.0,
    "eventPressure": 0.2
  }'
```

Terminal D перед прибытием парома:

```bash
curl -X POST http://localhost:8080/api/demand   -H "Content-Type: application/json"   -d '{
    "zoneCode": "TERMINAL_D",
    "demand": 0.65,
    "pickupMinutes": 5,
    "airportPressure": 0.0,
    "ferryPressure": 1.0,
    "eventPressure": 0.0
  }'
```

## Получить рейтинг

```bash
curl "http://localhost:8080/api/radar?lat=59.42130&lon=24.79380"
```

Пример ответа:

```json
[
  {
    "zoneCode": "ULEMISTE",
    "zoneName": "Ülemiste",
    "score": 57.5,
    "demand": 0.8,
    "pickupMinutes": 6,
    "driverSupply": 0.35,
    "reasons": [
      "high demand",
      "few nearby cars"
    ]
  }
]
```

## Текущая формула

```text
score =
    40 * demand
  + 30 * (1 - driverSupply)
  + 10 * airportPressure
  + 10 * ferryPressure
  + 10 * eventPressure
  - 1.5 * relocationMinutes
```

`driverSupply` вычисляется из ETA пассажирского Bolt:

```text
1–2 min -> 1.00
3 min   -> 0.80
4 min   -> 0.65
5 min   -> 0.50
6 min   -> 0.35
7+ min  -> 0.20
```

## Что делать дальше

MVP 0.2:

1. учитывать фактическую позицию водителя;
2. вычислять время до каждой зоны;
3. автоматически читать расписание паромов;
4. автоматически читать прилёты аэропорта;
5. разделить статистику Bolt / Comfort / Priority;
6. сохранять фактические поездки и строить персональную модель доходности.

Важно: проект не использует внутренние или закрытые API Bolt.
