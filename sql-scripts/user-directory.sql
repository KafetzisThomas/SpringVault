DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS authorities;

CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(60) NOT NULL,
    enabled BOOLEAN NOT NULL
);

CREATE TABLE authorities (
    username VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    CONSTRAINT fk_auth_users FOREIGN KEY (username) REFERENCES users (username)
);

-- Unique constraint on username and authority
CREATE UNIQUE INDEX ix_auth_username ON authorities (username, authority);

-- Default user
-- username: john
-- password: test123

INSERT INTO users (username, password, enabled) VALUES
('john', '$2a$12$geC49PgWMdvp3vdZ7f/aUOdDiWxxSYkLaO24yQHwYATVngPhBPXPq', TRUE);

INSERT INTO authorities (username, authority) VALUES ('john','ROLE_USER')
