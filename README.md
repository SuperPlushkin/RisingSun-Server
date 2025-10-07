Привет путник. Тебя привествует сервер для мессенджера RisingSun на Java Spring Boot.
Чтобы приложение заработало, надо скачать docker-image приложения (лежит на Docker Hub "superplushkin/risingsun-server"). Затем приложение можно запускать где угодно (если стоит docker и docker-compose).
ПРОЧИТАТЬ ОБЯЗАТЕЛЬНО!!!!!
.env файл для запуска обязательно, должен лежать в корне проекта (/RisingSun-Server/.env). В нем храняться пароли для сервисов (смотрите пример: .env-example)
docker-compose.yml файл тоже обязателен, но находится в корне с проектом, так что ничего создавать не надо (/RisingSun-Server/docker-compose.yml)
