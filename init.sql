-- Создание таблицы пользователей
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(30) NOT NULL UNIQUE CHECK (char_length(username) >= 4),
    name VARCHAR(30) NOT NULL CHECK (char_length(name) >= 4),
    email VARCHAR(60) NOT NULL,
    hash_password VARCHAR(64) NOT NULL CHECK (char_length(hash_password) >= 8),
    last_login TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_enabled ON users(enabled);




-- История логинов
CREATE TABLE IF NOT EXISTS login_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    device_info VARCHAR NOT NULL,
    login_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_login_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_login_user_id ON login_history(user_id);


-- Таблица токенов подтверждения email
CREATE TABLE IF NOT EXISTS verification_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(128) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    token_type VARCHAR(30) NOT NULL DEFAULT 'email_confirmation'

    CONSTRAINT fk_verification_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_verification_token ON verification_tokens(token);
CREATE INDEX IF NOT EXISTS idx_verification_user_id ON verification_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_verification_token_type ON verification_tokens(token_type);

-- Чаты
CREATE TABLE IF NOT EXISTS chats (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL CHECK (char_length(name) >= 4),
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_group BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_chat_creator FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_chats_is_deleted ON chats(is_deleted);



-- Участники чатов
CREATE TABLE IF NOT EXISTS chat_members (
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

CREATE INDEX IF NOT EXISTS idx_chat_members_chat_id ON chat_members(chat_id);
CREATE INDEX IF NOT EXISTS idx_chat_members_user_id ON chat_members(user_id);



-- Сообщения
CREATE TABLE IF NOT EXISTS messages (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    chat_id BIGINT NOT NULL,
    text VARCHAR NOT NULL,
    sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_count BIGINT NOT NULL DEFAULT 0 CHECK (read_count >= 0),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_message_sender FOREIGN KEY (sender_id) REFERENCES users(id),
    CONSTRAINT fk_message_chat FOREIGN KEY (chat_id) REFERENCES chats(id)
);

CREATE INDEX IF NOT EXISTS idx_messages_chat_id ON messages(chat_id);
CREATE INDEX IF NOT EXISTS idx_messages_sender_id ON messages(sender_id);
CREATE INDEX IF NOT EXISTS idx_messages_sent_at ON messages(sent_at);
CREATE INDEX IF NOT EXISTS idx_messages_is_deleted ON messages(is_deleted);



-- Статусы прочтения сообщений
CREATE TABLE IF NOT EXISTS message_read_status (
    message_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    read_at TIMESTAMP NOT NULL,
    PRIMARY KEY (message_id, user_id),
    CONSTRAINT fk_read_message FOREIGN KEY (message_id) REFERENCES messages(id),
    CONSTRAINT fk_read_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_read_status_read_at ON message_read_status(read_at);




--CREATE OR REPLACE FUNCTION insert_user_if_not_exists(p_username TEXT, p_name TEXT, p_hash_password TEXT)
--RETURNS BOOLEAN AS $$
--DECLARE
--    inserted BOOLEAN := FALSE;
--BEGIN
--    WITH ins AS (
--        INSERT INTO users (username, name, hash_password, last_login)
--        SELECT p_username, p_name, p_hash_password, CURRENT_TIMESTAMP
--        WHERE NOT EXISTS (
--            SELECT 1 FROM users WHERE username = p_username
--        )
--        RETURNING id
--    )
--    SELECT COUNT(*) > 0 INTO inserted FROM ins;
--
--    RETURN inserted;
--END;
--$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insert_user_if_not_exists(p_username TEXT, p_name TEXT, p_email TEXT, p_hash_password TEXT)
RETURNS TABLE(success BOOLEAN, error_text TEXT, generated_token TEXT) AS $$
DECLARE
    v_user_id BIGINT;
    v_token TEXT;
BEGIN
    IF EXISTS (SELECT 1 FROM users WHERE username = p_username) THEN
        RETURN QUERY SELECT FALSE, 'Username already exists', NULL;
        RETURN;
    END IF;

    IF EXISTS (SELECT 1 FROM users WHERE email = p_email) THEN
        RETURN QUERY SELECT FALSE, 'Email already exists', NULL;
        RETURN;
    END IF;

    INSERT INTO users (username, name, email, hash_password, created_at, enabled)
    VALUES (p_username, p_name, p_email, p_hash_password, CURRENT_TIMESTAMP, FALSE)
    RETURNING id INTO v_user_id;

    -- Генерация токена
    v_token := encode(gen_random_bytes(32), 'hex');

    -- Вставка токена подтверждения
    INSERT INTO verification_tokens (user_id, token, expiry_date, created_at, token_type)
    VALUES (v_user_id, v_token, CURRENT_TIMESTAMP + INTERVAL '24 hours', CURRENT_TIMESTAMP, 'email_confirmation');

    RETURN QUERY SELECT TRUE, NULL, v_token;
END;
$$ LANGUAGE plpgsql;