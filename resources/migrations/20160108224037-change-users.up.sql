DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id int IDENTITY,
    nickname VARCHAR,
    email VARCHAR,
    avatar_url VARCHAR,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
