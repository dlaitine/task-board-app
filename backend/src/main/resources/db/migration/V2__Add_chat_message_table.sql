CREATE TABLE chat_message (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    content TEXT,
    created_at BIGINT
);