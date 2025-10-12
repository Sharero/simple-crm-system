# Тестовое задание для Лаборатории ШИФТ: Разработка упрощенной CRM-системы

## Описание проекта

CRM-система, которая управляет информацией о продавцах и их
транзакциях. Система включает возможности для создания, чтения, обновления и
удаления данных о продавцах и транзакциях, а также включает функции аналитики
для обработки и анализа данных.

## Основная функциональность

- CRUD-операции для продавцов (`Seller`) и транзакций (`Transaction`)
- Аналитика продаж:
    - Самый продуктивный продавец за период
    - Список продавцов, у которых сумма транзакций за период меньше заданной
- Валидация и обработка ошибок
- Unit тесты

## Инструкция по сборке и запуску

1. Установить PostgreSQL.
2. Создать и запустить базу данных.

```
sudo -iu postgres
psql
CREATE USER {имя_пользователя} WITH PASSWORD '{пароль}';
CREATE DATABASE {название_бд} OWNER {имя_пользователя};
\q
sudo systemctl start postgresql
psql -h localhost -U {имя_пользователя} -d {название_бд}
```

3. В файле application.properties указать название созданной базы данных, имя пользователя и пароль:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/{название_бд}
spring.datasource.username={имя_пользователя}
spring.datasource.password={пароль}
```

3. Запустить проект через Gradle ./gradlew bootRun

Приложение будет доступно на http://localhost:8080

## Примеры использования API

Для удобства использования API создана коллекция Postman: `Shift.postman_collection.json`.  
В ней уже добавлены все доступные эндпоинты с примерами запросов.

### Seller

1. Список всех продавцов

- **Метод:** GET
- **URL:** /sellers
- **Пример ответа:**

```json 
[
  {
    "id": 1,
    "name": "Yaroslav",
    "contactInfo": "my@mail.ru",
    "registrationDate": "2025-10-12T20:00:35.516604"
  },
  {
    "id": 2,
    "name": "Andrey",
    "contactInfo": "andrey@gmail.com",
    "registrationDate": "2025-10-12T20:11:19.538232"
  }
]
```

2. Информация о конкретном продавце

- **Метод:** GET
- **URL:** /sellers/{id}
- **Пример ответа:**

```json 
{
  "id": 1,
  "name": "Yaroslav",
  "contactInfo": "my@mail.ru",
  "registrationDate": "2025-10-12T20:00:35.516604"
}
```

3. Создать нового продавца

- **Метод:** POST
- **URL:** /sellers
- **Пример запроса:**

```json 
{
  "name": "Andrey",
  "contactInfo": "andrey@gmail.com"
}
```

- **Пример ответа:**

```json 
{
  "id": 2,
  "name": "Andrey",
  "contactInfo": "andrey@gmail.com",
  "registrationDate": "2025-10-12T20:11:19.538231844"
}
```

4. Обновить информацию о продавце

- **Метод:** PUT
- **URL:** /sellers/{id}
- **Пример запроса:**

```json 
{
  "name": "Kostya",
  "contactInfo": "kostya@gmail.com"
}
```

- **Пример ответа:**

```json 
{
  "id": 2,
  "name": "Kostya",
  "contactInfo": "kostya@gmail.com",
  "registrationDate": "2025-10-12T20:11:19.538232"
}
```

5. Удалить продавца

- **Метод:** DELETE
- **URL:** /sellers/{id}

6. Получить самого продуктивного продавца

- **Метод:** GET
- **URL:** /sellers/top-seller
- **Параметры:**

| Query-параметры | Описание       | Пример заполнения          | 
|-----------------|----------------|----------------------------|
| start           | начало периода | 2025-10-11T21:48:04.111123 |
| end             | конец периода  | 2025-10-12T20:18:34.494674 |

- **Пример ответа:**

```json 
{
  "seller": {
    "id": 3,
    "name": "Andrey",
    "contactInfo": "andrey@gmail.com",
    "registrationDate": "2025-10-12T20:15:15.6005"
  },
  "totalAmount": 2000.0
}
```

7. Получить список продавцов с суммой меньше указанной

- **Метод:** GET
- **URL:** /sellers/sellers-below
- **Параметры:**

| Query-параметры | Описание              | Пример заполнения          | 
|-----------------|-----------------------|----------------------------|
| start           | начало периода        | 2025-10-11T21:48:04.111123 |
| end             | конец периода         | 2025-10-12T20:18:34.494674 |
| limit           | сумма всех транзакций | 500                        |

- **Пример ответа:**

```json 
[
  {
    "seller": {
      "id": 4,
      "name": "Kostya",
      "contactInfo": "kostya@gmail.com",
      "registrationDate": "2025-10-12T20:15:42.895993"
    },
    "totalAmount": 10.0
  },
  {
    "seller": {
      "id": 1,
      "name": "Yaroslav",
      "contactInfo": "my@mail.ru",
      "registrationDate": "2025-10-12T20:00:35.516604"
    },
    "totalAmount": 100.0
  }
]
```

### Transaction

1. Получить список всех транзакций

- **Метод:** GET
- **URL:** /transactions
- **Пример ответа:**

```json 
[
  {
    "id": 1,
    "seller": {
      "id": 1,
      "name": "Yaroslav",
      "contactInfo": "my@mail.ru",
      "registrationDate": "2025-10-12T20:00:35.516604"
    },
    "amount": 100.0,
    "paymentType": "CASH",
    "transactionDate": "2025-10-12T20:16:10.991356"
  },
  {
    "id": 2,
    "seller": {
      "id": 3,
      "name": "Andrey",
      "contactInfo": "andrey@gmail.com",
      "registrationDate": "2025-10-12T20:15:15.6005"
    },
    "amount": 1000.0,
    "paymentType": "CARD",
    "transactionDate": "2025-10-12T20:16:56.078846"
  }
]
```

2. Получить информацию о конкретной транзакции

- **Метод:** POST
- **URL:** /transactions/{id}
- **Пример ответа:**

```json 
{
  "id": 2,
  "seller": {
    "id": 3,
    "name": "Andrey",
    "contactInfo": "andrey@gmail.com",
    "registrationDate": "2025-10-12T20:15:15.6005"
  },
  "amount": 1000.0,
  "paymentType": "CARD",
  "transactionDate": "2025-10-12T20:16:56.078846"
}
```

3. Создать новую транзакцию

- **Метод:** POST
- **URL:** /transactions
- **Пример запроса:**

```json 
{
  "sellerId": 1,
  "amount": 100,
  "paymentType": "CASH"
}
```

- **Пример ответа:**

```json 
{
  "id": 1,
  "seller": {
    "id": 1,
    "name": "Yaroslav",
    "contactInfo": "my@mail.ru",
    "registrationDate": "2025-10-12T20:00:35.516604"
  },
  "amount": 100.0,
  "paymentType": "CASH",
  "transactionDate": "2025-10-12T20:16:10.991355845"
}
```

4. Получить все транзакции продавца

- **Метод:** POST
- **URL:** /transactions/{id_seller}
- **Пример ответа:**

```json 
[
  {
    "id": 2,
    "seller": {
      "id": 3,
      "name": "Andrey",
      "contactInfo": "andrey@gmail.com",
      "registrationDate": "2025-10-12T20:15:15.6005"
    },
    "amount": 1000.0,
    "paymentType": "CARD",
    "transactionDate": "2025-10-12T20:16:56.078846"
  },
  {
    "id": 3,
    "seller": {
      "id": 3,
      "name": "Andrey",
      "contactInfo": "andrey@gmail.com",
      "registrationDate": "2025-10-12T20:15:15.6005"
    },
    "amount": 1000.0,
    "paymentType": "CASH",
    "transactionDate": "2025-10-12T20:17:02.124621"
  }
]
```

## Зависимости

Проект использует следующие основные зависимости:

- spring-boot-starter
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-validation
- org.projectlombok:lombok
- org.postgresql:postgresql
- spring-boot-starter-test

Пример конфигурации `build.gradle.kts`:

```
plugins {
    id("java")
    id("jacoco")
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "com.example"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    runtimeOnly("org.postgresql:postgresql:42.6.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // сначала выполняем тесты

    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco"))
    }
}
```