package com.mello.nathalia.booksapi.domain.service;

import com.mello.nathalia.booksapi.api.mapper.BookMapper;
import com.mello.nathalia.booksapi.api.request.CreateBookRequest;
import com.mello.nathalia.booksapi.common.exception.CategoryNotFoundException;
import com.mello.nathalia.booksapi.common.exception.DuplicateBookException;
import com.mello.nathalia.booksapi.common.exception.InvalidBookException;
import com.mello.nathalia.booksapi.domain.model.Book;
import com.mello.nathalia.booksapi.domain.model.Category;
import com.mello.nathalia.booksapi.domain.repository.BookRepository;
import com.mello.nathalia.booksapi.infrastructure.client.googlebooks.GoogleBooksClient;
import com.mello.nathalia.booksapi.infrastructure.client.googlebooks.GoogleBooksData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private GoogleBooksClient googleBooksClient;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    private CreateBookRequest request;
    private Book book;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Tecnologia");

        request = new CreateBookRequest(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                4.5,
                Set.of(1L)
        );

        book = new Book();
        book.setTitle("Clean Code");
        book.setAuthor("Robert C. Martin");
        book.setIsbn("9780132350884");
    }

    @Test
    @DisplayName("deve criar livro com sucesso quando dados são válidos")
    void create_deveRetornarLivro_quandoDadosSaoValidos() {
        when(bookMapper.toEntity(request)).thenReturn(book);
        when(categoryService.findById(1L)).thenReturn(category);
        when(googleBooksClient.filterByTitleAndAuthor("Clean Code", "Robert C. Martin"))
                .thenReturn(Optional.of(new GoogleBooksData("Uma descrição", "9780132350884")));
        when(bookRepository.findByIsbn("9780132350884")).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book result = bookService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Clean Code");

        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("deve lançar DuplicateBookException quando ISBN já existe")
    void create_deveLancarDuplicateBookException_quandoIsbnJaExiste() {
        when(bookMapper.toEntity(request)).thenReturn(book);
        when(categoryService.findById(1L)).thenReturn(category);
        when(googleBooksClient.filterByTitleAndAuthor(any(), any()))
                .thenReturn(Optional.empty());
        when(bookRepository.findByIsbn("9780132350884")).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookService.create(request))
                .isInstanceOf(DuplicateBookException.class);

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("deve salvar livro sem descrição quando Google Books não encontra")
    void create_deveSalvarSemDescricao_quandoGoogleBooksNaoEncontra() {
        when(bookMapper.toEntity(request)).thenReturn(book);
        when(categoryService.findById(1L)).thenReturn(category);
        when(googleBooksClient.filterByTitleAndAuthor(any(), any()))
                .thenReturn(Optional.empty());
        when(bookRepository.findByIsbn("9780132350884")).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book result = bookService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isNull(); // sem descrição
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("deve lançar InvalidBookException quando ISBN é nulo e Google Books não encontra")
    void create_deveLancarInvalidBookException_quandoIsbnNuloEGoogleBooksNaoEncontra() {
        // Arrange — request sem ISBN
        CreateBookRequest requestSemIsbn = new CreateBookRequest(
                "Título Inválido",
                "Autor Inválido",
                null,     // sem ISBN
                4.0,
                Set.of(1L)
        );

        Book bookSemIsbn = new Book();
        bookSemIsbn.setTitle("Título Inválido");
        bookSemIsbn.setAuthor("Autor Inválido");
        // isbn null

        when(bookMapper.toEntity(requestSemIsbn)).thenReturn(bookSemIsbn);
        when(categoryService.findById(1L)).thenReturn(category);
        when(googleBooksClient.filterByTitleAndAuthor(any(), any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.create(requestSemIsbn))
                .isInstanceOf(InvalidBookException.class)
                .hasMessageContaining("ISBN é obrigatório");

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("deve lançar CategoryNotFoundException quando categoria não existe")
    void create_deveLancarCategoryNotFoundException_quandoCategoriaInexistente() {
        when(bookMapper.toEntity(request)).thenReturn(book);
        when(categoryService.findById(1L))
                .thenThrow(new CategoryNotFoundException("Categoria não encontrada com id: 1"));

        assertThatThrownBy(() -> bookService.create(request))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("Categoria não encontrada");

        verify(bookRepository, never()).save(any(Book.class));
    }

}
