CREATE TABLE IF NOT EXISTS roles
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS users_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

INSERT INTO roles (name)
SELECT 'USER'
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE name = 'USER'
);

INSERT INTO users (username, password)
SELECT 'usuario', '$2a$10$4l4GaRfl/g9SzPTMMFyAv.3mc0/dgRkWvgwD.qw6tHOlBom1sKrXy'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'usuario'
);

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u,
     roles r
WHERE u.username = 'usuario'
  AND r.name = 'USER'
  AND NOT EXISTS (
    SELECT 1
    FROM users_roles ur
    WHERE ur.user_id = u.id
      AND ur.role_id = r.id
);