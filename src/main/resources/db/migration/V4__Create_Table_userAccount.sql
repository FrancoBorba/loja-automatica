CREATE TABLE  IF NOT EXISTS  user_account (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    date_of_registration DATE NOT NULL,
    is_active BOOLEAN NOT NULL
);
