CREATE TABLE IF NOT EXISTS categories (
    name VARCHAR(100) NOT NULL,
    position INT NOT NULL,
    PRIMARY KEY (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tasks (
    id CHAR (36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(100) NULL,
    is_complete BOOLEAN NOT NULL DEFAULT FALSE,
    deadline DATETIME(0) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_tasks_open_deadline (is_complete, deadline),
    KEY idx_tasks_category (category),
    CONSTRAINT fk_task_category
        FOREIGN KEY (category) REFERENCES categories(name)
        ON UPDATE CASCADE
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
