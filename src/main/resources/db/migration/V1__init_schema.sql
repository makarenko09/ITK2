-- Схема базы данных для Task API
-- NFT: до 10к пользователей, до 100к задач

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS tasks (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    assignee_id UUID REFERENCES users(id)
);

-- Индексы для производительности (NFT: 100к задач, 10к пользователей)
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status);
CREATE INDEX IF NOT EXISTS idx_tasks_assignee ON tasks(assignee_id);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Комментарии
COMMENT ON TABLE tasks IS 'Задачи сервиса управления задачами';
COMMENT ON TABLE users IS 'Пользователи системы';
COMMENT ON COLUMN tasks.status IS 'Статус задачи: NEW, IN_PROGRESS, DONE, CLOSED';
