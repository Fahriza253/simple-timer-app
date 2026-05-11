CREATE TABLE IF NOT EXISTS app_config (
    id INTEGER PRIMARY KEY CHECK (id = 1), -- Forces only one row to exist

    pomodoro_minute INTEGER NOT NULL DEFAULT 25,
    short_break_minute INTEGER NOT NULL DEFAULT 5,
    long_break_minute INTEGER NOT NULL DEFAULT 15,

    auto_start_pomodoro INTEGER NOT NULL DEFAULT 0, -- 0 for false, 1 for true
    auto_start_break INTEGER NOT NULL DEFAULT 0,

    long_break_interval INTEGER NOT NULL DEFAULT 4
);

INSERT OR IGNORE INTO app_config (id) VALUES (1);

CREATE TABLE IF NOT EXISTS tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,

    -- Task-specific timer overrides
    pomodoro_minute INTEGER NOT NULL,
    short_break_minute INTEGER NOT NULL,
    long_break_minute INTEGER NOT NULL,
    long_break_interval INTEGER NOT NULL,

    -- Task progress and status
    target_sessions INTEGER NOT NULL,
    completed_sessions INTEGER NOT NULL DEFAULT 0,
    is_completed INTEGER NOT NULL DEFAULT 0,
    is_repeated INTEGER NOT NULL DEFAULT 0,

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS session_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    task_id INTEGER, -- Can be NULL if the user ran a timer without selecting a task
    completed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    status INTEGER NOT NULL DEFAULT 1, -- 1 for success, 0 for failed
    session_date DATE DEFAULT CURRENT_DATE, -- For daily analysis

    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_session_history_session_date ON session_history(session_date);
