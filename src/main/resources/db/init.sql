-- 1. Функция для назначения задачи пользователю и обновления приоритета
CREATE OR REPLACE FUNCTION assign_task_to_user(
    p_task_id BIGINT,
    p_user_id BIGINT,
    p_priority INTEGER DEFAULT 1
) RETURNS BOOLEAN AS $$
BEGIN
    -- Проверяем существование задачи и пользователя
    IF NOT EXISTS (SELECT 1 FROM task WHERE id = p_task_id) OR
       NOT EXISTS (SELECT 1 FROM user_ WHERE id = p_user_id) THEN
        RETURN FALSE;
    END IF;

    -- Проверяем, что пользователь и задача находятся в одной группе
    IF NOT EXISTS (
        SELECT 1
        FROM task t
        JOIN group_user gu ON t.group_id = gu.group_id
        WHERE t.id = p_task_id 
        AND gu.user_id = p_user_id
    ) THEN
        RETURN FALSE;
    END IF;

    -- Проверяем, не назначена ли уже задача этому пользователю
    IF EXISTS (
        SELECT 1 
        FROM user_task 
        WHERE task_id = p_task_id 
        AND user_id = p_user_id
    ) THEN
        RETURN FALSE;
    END IF;

    -- Создаем связь пользователь-задача
    INSERT INTO user_task (
        task_id,
        user_id,
        assigned_date,
        completion_status
    )
    VALUES (
        p_task_id,
        p_user_id,
        CURRENT_TIMESTAMP,
        FALSE
    );
    
    -- Обновляем приоритет задачи
    UPDATE task 
    SET current_priority = p_priority
    WHERE id = p_task_id;

    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;

-- 2. Функция для получения всех задач пользователя с фильтрацией
CREATE OR REPLACE FUNCTION get_user_tasks(
    p_user_id BIGINT,
    p_completed BOOLEAN DEFAULT NULL,
    p_priority INTEGER DEFAULT NULL
)
RETURNS TABLE (
    task_id BIGINT,
    title VARCHAR,
    description TEXT,
    current_priority INTEGER,
    deadline TIMESTAMP,
    is_completed BOOLEAN
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        t.id,
        t.title,
        t.description,
        t.current_priority,
        t.deadline,
        t.is_completed
    FROM user_task ut
    JOIN task t ON ut.task_id = t.id
    WHERE ut.user_id = p_user_id
      AND (p_completed IS NULL OR t.is_completed = p_completed)
      AND (p_priority IS NULL OR t.current_priority = p_priority);
END;
$$ LANGUAGE plpgsql;


-- 3. Функция для подсчета статистики выполнения задач пользователем
CREATE OR REPLACE FUNCTION get_user_task_statistics(
    p_user_id INTEGER,
    p_start_date TIMESTAMP DEFAULT NULL,
    p_end_date TIMESTAMP DEFAULT NULL
)
RETURNS TABLE (
    total_tasks BIGINT,
    completed_tasks BIGINT,
    completion_rate NUMERIC,
    high_priority_tasks BIGINT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        COUNT(*) AS total_tasks,
        COUNT(CASE WHEN ut.completion_status = TRUE THEN 1 END) AS completed_tasks,
        CASE 
            WHEN COUNT(*) > 0 THEN ROUND(
                COUNT(CASE WHEN ut.completion_status = TRUE THEN 1 END)::NUMERIC / COUNT(*) * 100, 2
            )
            ELSE 0
        END AS completion_rate,
        COUNT(CASE WHEN t.current_priority > 2 THEN 1 END) AS high_priority_tasks
    FROM user_task ut
    JOIN task t ON ut.task_id = t.id
    WHERE ut.user_id = p_user_id
      AND (p_start_date IS NULL OR ut.assigned_date >= p_start_date)
      AND (p_end_date IS NULL OR ut.assigned_date <= p_end_date);
END;
$$ LANGUAGE plpgsql;
