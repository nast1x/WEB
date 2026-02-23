# WEB
ЛР по предмету "Разработка WEB-приложений"

[Задания на ЛР](https://github.com/Das-dasein/web-ssau/tree/main) 

# Лабораторная работа 1

![Антон и чай](./Gifs/IMG_6906.gif)

Сдана ✔️

Для улучшения: 
- добавить свою ошибку (не использвать общий класс Exception e)
- не везде возращать ResponseEntity<>
  
# Лабораторная работа 2

![Макс](./Gifs/max-verstappen-f1.gif)

Сдана ✔️

Создание БД

```sql
CREATE TABLE task (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL 
        CHECK (status IN ('OPEN', 'DONE', 'IN_PROGRESS', 'CLOSED')),
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

);
```

# Лабораторная работа 3

![Юдзуру](./Gifs/yuzureaction-yuzuru-reaction.gif)

Не сдана :x:

Расширение БД 

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id),
    role_id BIGINT NOT NULL REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);

-- Изменение таблицы task (добавление внешнего ключа)
ALTER TABLE task ADD CONSTRAINT fk_task_created_by FOREIGN KEY (created_by) REFERENCES users(id);

-- Новые роли
INSERT INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_USER');
INSERT INTO users (username) VALUES ('admin'), ('user');

-- Привязка ролей 
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1); -- admin -> ROLE_ADMIN
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2); -- user -> ROLE_USER
```

# Лабораторная работа 4
Не сдана :x:
# Лабораторная работа 5

![Нафаня и Широ](./Gifs/nafany-sh1ro.gif)

Не сдана :x:
# Если у вас остались вопросы, то

![Не ко мне вопросы](./Gifs/not_my_q.gif)
