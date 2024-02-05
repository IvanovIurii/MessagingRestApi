CREATE TABLE users (
  id UUID DEFAULT gen_random_uuid(),
  email VARCHAR(256) NOT NULL UNIQUE,
  PRIMARY KEY (id)
);