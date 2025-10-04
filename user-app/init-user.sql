-- -- init-user.sql
-- CREATE TABLE IF NOT EXISTS users (
--                                      id BIGINT PRIMARY KEY,
--                                      email VARCHAR(255) NOT NULL,
--     password VARCHAR(255) NOT NULL,
--     name VARCHAR(255)
--     );
--
-- INSERT INTO users (id, email, password, name)
-- VALUES (1, 'test@example.com', 'test123', 'Test User')
--     ON CONFLICT (id) DO NOTHING;
CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    preferences TEXT,
    is_validated BOOLEAN DEFAULT FALSE
    );

INSERT INTO users (id, email, password, name, address, preferences, is_validated)
VALUES
    (1, 'test@example.com', 'test123', 'Test User', '123 Main St', 'dark_mode=true;notifications=true', true);
