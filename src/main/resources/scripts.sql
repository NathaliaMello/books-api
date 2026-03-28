CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE books (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    rating DOUBLE,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE book_categories (
    book_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (book_id, category_id),
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

INSERT INTO categories (name) VALUES
    ('Tecnologia e Programação'),
    ('Desenvolvimento Pessoal'),
    ('Ficção Científica'),
    ('Romance'),
    ('Negócios e Empreendedorismo');

INSERT INTO books (title, author, isbn, description, created_at) VALUES
    ('Clean Code', 'Robert C. Martin', '9780132350884', NULL, NOW()),
    ('The Pragmatic Programmer', 'David Thomas', '9780135957059', NULL, NOW()),
    ('Domain-Driven Design', 'Eric Evans', '9780321125217', NULL, NOW()),
    ('O Poder do Hábito', 'Charles Duhigg', '9788539004119', NULL, NOW()),
    ('Sapiens', 'Yuval Noah Harari', '9780099590088', NULL, NOW());

INSERT INTO book_categories (book_id, category_id) VALUES
    (1, 1),
    (2, 1),
    (3, 1),
    (4, 2),
    (5, 2);
