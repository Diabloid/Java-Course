-- Ідемпотентний DDL-скрипт ініціалізації схеми реляційної БД (ЛР 10: Рівень 1-2)

-- 1. Таблиця проєктів (Батьківська сутність, 1 у зв'язку 1:N)
CREATE TABLE IF NOT EXISTS projects (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    client_name VARCHAR(255) NOT NULL,
    budget NUMERIC(14, 2) NOT NULL DEFAULT 0.00 CHECK (budget >= 0)
);

-- 2. Таблиця завдань (Дочірня сутність, N у зв'язку 1:N)
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT PRIMARY KEY,
    project_id BIGINT,
    name VARCHAR(255) NOT NULL,
    task_type VARCHAR(50) NOT NULL,
    assignee VARCHAR(255) NOT NULL,
    priority INT NOT NULL CHECK (priority BETWEEN 1 AND 3),
    estimated_hours NUMERIC(8, 2) NOT NULL CHECK (estimated_hours > 0),
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    cost_strategy VARCHAR(100) NOT NULL DEFAULT 'Priority-Based Pricing',
    repository_url VARCHAR(500),
    severity VARCHAR(50),
    is_hotfix BOOLEAN DEFAULT FALSE,
    module_name VARCHAR(100),
    story_points INT DEFAULT 0,
    doc_format VARCHAR(50),
    target_audience VARCHAR(100),
    CONSTRAINT fk_tasks_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- 3. Індекси для оптимізації зовнішнього ключа та сортування (Рівень 2)
CREATE INDEX IF NOT EXISTS idx_tasks_project_id ON tasks(project_id);
CREATE INDEX IF NOT EXISTS idx_tasks_name_hours ON tasks(name, estimated_hours);
