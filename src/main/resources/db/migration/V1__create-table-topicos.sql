CREATE TABLE topicos (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    fecha TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    autor VARCHAR(255) NOT NULL,
    curso VARCHAR(50) NOT NULL CHECK (curso IN ('JAVA', 'JAVASCRIPT', 'MYSQL', 'SOFTSKILLS', 'CSS')),
    CONSTRAINT unique_titulo UNIQUE (titulo)
);

