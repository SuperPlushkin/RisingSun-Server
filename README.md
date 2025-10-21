# 🌅 Sunrise Messenger Server

<div align="center">

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)

**Серверная часть мессенджера Sunrise на Java Spring Boot**

</div>

## 🚀 Быстрый старт

### Предварительные требования
- **Docker** и **Docker Compose** должны быть установлены на вашей системе

### Запуск приложения

1. **Скачайте Docker образ:**

   ```bash
   docker pull superplushkin/risingsun-server
   ```
2. **Запустите приложение:**

   ```bash
   docker-compose up -d
   ```

Приложение готово к работе! 🎉

## ⚙️ Конфигурация

📁 Файл окружения (.env)

❗ ВАЖНО: Файл .env ОБЯЗАТЕЛЕН для запуска!

    Расположение: /Sunrise-Server/.env

    Используйте пример: .env-example как шаблон

    Содержит критически важные настройки и пароли для сервисов

🐳 Docker Compose

    Файл: /Sunrise-Server/docker-compose.yml

    Уже находится в корне проекта - создавать не нужно

## 📁 Структура проекта

```
Sunrise-Server/
├── .gradle/              # 🛠️  Кэш Gradle
├── gradle/wrapper/       # 📦 Gradle Wrapper файлы
├── src/                  # 📁 Исходный код приложения
├── .dockerignore         # 🐳 Исключения для Docker
├── .env-example          # 📋 Пример конфигурации окружения
├── .gitignore            # 🔒 Игнорируемые файлы Git
├── Dockerfile            # 🐳 Конфигурация Docker образа
├── DocumentationAPI.md   # 📚 Документация API
├── build.gradle.kts      # ⚙️ Конфигурация сборки Gradle
├── docker-compose.yml    # 🐳 Компоновка Docker сервисов
├── gradlew               # 🐧 Linux/Mac скрипт Gradle Wrapper
├── gradlew.bat           # 🪟 Windows скрипт Gradle Wrapper
├── init.sql              # 🗃️  SQL скрипт инициализации БД
└── settings.gradle.kts   # ⚙️  Настройки проекта Gradle
```

**Приятного использования! ✨**
