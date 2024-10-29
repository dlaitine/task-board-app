CREATE TABLE chat_message (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(255) NOT NULL,
    content TEXT,
    created_at BIGINT
);