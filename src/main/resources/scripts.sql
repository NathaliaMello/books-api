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