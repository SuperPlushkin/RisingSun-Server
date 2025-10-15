# 🌅 Sunrise API Documentation

Полная документация по REST API для общения с сервером.

## 📋 Обзор API

- **Базовый URL:** `http://95.154.89.8:8888/app`
- **Формат данных:** JSON or Text
- **Аутентификация:** JWT токен (требуется для защищённых endpoints)

### 🚀 Быстрый старт

```http
# 1. Регистрация пользователя
POST /auth/register
{
  "username": "SuperPlushkin",
  "password": "12345",
  "name": "Kirill"
}

# 2. Авторизация
POST /auth/login
{
  "username": "SuperPlushkin",   // обязательно, 4-30 символов
  "password": "12345"    // обязательно, минимум 6 символов
}

# 3. Использование API с токеном
GET /app/user/getmany
Authorization: Bearer <ваш_токен>
{
  "limited": 20,
  "offset": 0,
  "filter": "loh"
}

# 4. ГОТОВО!!!
```

## 🔐 Аутентификация

### Регистрация

- **Метод:** POST /auth/register
- **Описание:** Создаёт нового пользователя в системе 

### 🧾 Тело запроса
```Json
{
  "username": "string",
  "password": "string",
  "name": "string"
}
```

### 📥 Пример
```Json
{
  "username": "SuperPlushkin",
  "password": "123456",
  "name": "Kirill"
}
```

### Ответы

- **✅ 200 OK:** — Успешно
```Text
User registered successfully
```
- **❌ 400 Bad Request:** — Пример ошибки валидации
```Json
{
  "errors": {
    "username": "Username must be between 4 and 30 characters",
    "password": "Password must be at least 6 characters",
    "name": "Name must be between 4 and 30 characters"
  }
}
```

### Авторизация

- **Метод:** POST /auth/login
- **Описание:** Аутентифицирует пользователя и возвращает JWT токен

### 🧾 Тело запроса
```Json
{
  "username": "string",
  "password": "string"
}
```

### 📥 Пример
```Json
{
  "username": "SuperPlushkin",
  "password": "123456"
}
```

### Ответы

- **✅ 200 OK** — JWT токен
```Text
"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```
- **❌ 400 Bad Request** — Неверные учётные данные
```Text
"Invalid credentials"
```
- **❌ 400 Bad Request** — Пример ошибки валидации
```Json
{
  "errors": {
    "username": "Username must be between 4 and 30 characters",
    "password": "Password must be at least 6 characters"
  }
}
```

## 👤 Действия с пользователями

### Получение списка пользователей

- **Метод:** GET /app/user/getmany
- **Описание:** Возвращает отфильтрованный список пользователей с поддержкой offset и текстового фильтра.

### 🧾 Тело запроса
```Json
{
  "limited": "integer",
  "offset": "integer",
  "filter": "string"
}
```

### Поля запроса и валидация:
|Поле   |Тип    |Обязательное|Описание                                                                                                    |
|-------|-------|------------|------------------------------------------------------------------------------------------------------------|
|limited|integer|✅ да       |Количество пользователей в ответе. Значение от 1 до 50                                                      |
|offset |integer|❌ нет      |Смещение от начала списка (счет идет в страницах по формуле limited * offset). Значение от 0. По умолчанию 0|
|filter |string |❌ нет      |Фильтр по имени/логину. По умолчанию — пустая строка.                                                       |

### 📥 Пример
```Json
{
  "limited": 10,
  "offset": 0,
  "filter": "kiril"
}
```

### Ответы

- **✅ 200 OK** — Успешный запрос
```Json
[
  {
    "username": "john_doe",
    "name": "John"
  },
  {
    "username": "johnny",
    "name": "Johnny"
  }
]
```
- **❌ 400 Bad Request** — Пример ошибки валидации
```Json
{
  "errors": {
    "limited": "limited must be at most 50",
    "offset": "offset must be at least 0"
  }
}
```

## 💬 Действия с сообщениями

> Все endpoints в этом разделе требуют JWT аутентификации 

> Будут позже

## 🖥️ Статусы сервера

### Общий статус сервера

- **Метод:** GET /actions/status
- **Описание:** Возвращает текущий статус системы

### Ответы
- **✅ 200 OK** — Возврат статуса
```Json
{
  "status": "🟢 Онлайн",
  "version": "1.0",
  "users": "1"
}
```