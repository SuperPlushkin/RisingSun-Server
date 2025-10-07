# 🌅 RisingSun Messenger Server

<div align="center">

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

**Серверная часть мессенджера RisingSun на Java Spring Boot**

</div>

## 🚀 Быстрый старт

### Предварительные требования
- **Docker** и **Docker Compose** должны быть установлены на вашей системе

### Запуск приложения

1. **Скачайте Docker образ:**
   ```bash
   docker pull superplushkin/risingsun-server

Запустите приложение:
bash

docker-compose up -d

Приложение готово к работе! 🎉
⚙️ Конфигурация
📁 Файл окружения (.env)

❗ ВАЖНО: Файл .env ОБЯЗАТЕЛЕН для запуска!

    Расположение: /RisingSun-Server/.env

    Используйте пример: .env-example как шаблон

    Содержит критически важные настройки и пароли для сервисов

🐳 Docker Compose

    Файл: /RisingSun-Server/docker-compose.yml

    Уже находится в корне проекта - создавать не нужно

📁 Структура проекта
text

RisingSun-Server/
├── .env                    # ⚠️  Обязательный файл окружения
├── .env-example           # 📋 Пример конфигурации
├── docker-compose.yml     # 🐳 Конфигурация Docker
└── ...                    # Исходный код приложения

🔧 Технологии

    Backend: Java Spring Boot

    Containerization: Docker

    Orchestration: Docker Compose

    Registry: Docker Hub

📞 Поддержка

Если у вас возникли вопросы или проблемы с запуском, создайте issue в репозитории.
<div align="center">

Приятного использования! ✨
</div> ```
