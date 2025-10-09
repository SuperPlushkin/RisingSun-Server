-- Создание таблицы пользователей
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username(32) VARCHAR NOT NULL UNIQUE
        CHECK (
            char_length(username) >= 4 AND
            username ~ '^[a-zA-Z0-9_]+$'
        ),
    password(50) VARCHAR NOT NULL
        CHECK (
            char_length(password) >= 8 AND
            password ~ '^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).+$'
        ),,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_enabled ON users(enabled);



-- История логинов
CREATE TABLE login_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    ip_address VARCHAR NOT NULL,
    device_info VARCHAR NOT NULL,
    success BOOLEAN NOT NULL,
    CONSTRAINT fk_login_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_login_user_id ON login_history(user_id);
CREATE INDEX idx_login_success ON login_history(success);



-- Чаты
CREATE TABLE chats (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
        CHECK (
            char_length(name) >= 4 AND
            name ~ '^[a-zA-Z0-9 _-]+$'
        ),
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_chat_creator FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE INDEX idx_chats_is_deleted ON chats(is_deleted);



-- Участники чатов
CREATE TABLE chat_members (
    id BIGSERIAL PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_chat_member_chat FOREIGN KEY (chat_id) REFERENCES chats(id),
    CONSTRAINT fk_chat_member_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uq_chat_user UNIQUE (chat_id, user_id)
);

CREATE INDEX idx_chat_members_chat_id ON chat_members(chat_id);
CREATE INDEX idx_chat_members_user_id ON chat_members(user_id);



-- Сообщения
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    chat_id BIGINT NOT NULL,
    text VARCHAR NOT NULL,
    sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_count BIGINT NOT NULL DEFAULT 0,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_message_sender FOREIGN KEY (sender_id) REFERENCES users(id),
    CONSTRAINT fk_message_chat FOREIGN KEY (chat_id) REFERENCES chats(id)
);

CREATE INDEX idx_messages_chat_id ON messages(chat_id);
CREATE INDEX idx_messages_sender_id ON messages(sender_id);
CREATE INDEX idx_messages_sent_at ON messages(sent_at);
CREATE INDEX idx_messages_is_deleted ON messages(is_deleted);



-- Статусы прочтения сообщений
CREATE TABLE message_read_status (
    message_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    read_at TIMESTAMP NOT NULL,
    PRIMARY KEY (message_id, user_id),
    CONSTRAINT fk_read_message FOREIGN KEY (message_id) REFERENCES messages(id),
    CONSTRAINT fk_read_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_read_status_read_at ON message_read_status(read_at);


