-- Primeiro, dropar e recriar o banco com UTF-8
DROP DATABASE IF EXISTS books_db;
CREATE DATABASE books_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE books_db;

-- Recriar as tabelas com charset correto
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE books (
       id BIGINT PRIMARY KEY AUTO_INCREMENT,
       title VARCHAR(255) NOT NULL,
       author VARCHAR(255) NOT NULL,
       isbn VARCHAR(255) NOT NULL UNIQUE,
       description TEXT,
       rating DOUBLE,
       created_at TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE book_categories (
     book_id BIGINT NOT NULL,
     category_id BIGINT NOT NULL,
     PRIMARY KEY (book_id, category_id),
     FOREIGN KEY (book_id) REFERENCES books(id),
     FOREIGN KEY (category_id) REFERENCES categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE users (
       id         BIGINT PRIMARY KEY AUTO_INCREMENT,
       name       VARCHAR(100) NOT NULL,
       email      VARCHAR(255) NOT NULL UNIQUE,
       password   VARCHAR(255) NOT NULL,
       role       ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER',
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE book_ratings (
      id         BIGINT PRIMARY KEY AUTO_INCREMENT,
      book_id    BIGINT NOT NULL,
      user_id    BIGINT NOT NULL,
      rating     DECIMAL(3,1) NOT NULL,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
      UNIQUE KEY unique_book_user (book_id, user_id)
);

ALTER TABLE books DROP COLUMN rating;

ALTER TABLE books ADD COLUMN rating DECIMAL(3,1) DEFAULT NULL;
ALTER TABLE books ADD COLUMN rating_count INT DEFAULT 0;


-- Inserir os dados novamente
INSERT INTO categories (name) VALUES
      ('Tecnologia e Programação'),
      ('Desenvolvimento Pessoal'),
      ('Ficção Científica'),
      ('Romance'),
      ('Negócios e Empreendedorismo'),
      ('Fantasia'),
      ('Terror e Suspense'),
      ('Biografia e Autobiografia'),
      ('História'),
      ('Filosofia'),
      ('Psicologia'),
      ('Autoajuda'),
      ('Direito'),
      ('Medicina e Saúde'),
      ('Infantil'),
      ('Juvenil'),
      ('Quadrinhos e Graphic Novel'),
      ('Poesia'),
      ('Culinária'),
      ('Arte e Design');

