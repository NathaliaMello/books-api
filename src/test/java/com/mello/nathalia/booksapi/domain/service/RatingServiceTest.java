package com.mello.nathalia.booksapi.domain.service;

import com.mello.nathalia.booksapi.common.exception.BookNotFoundException;
import com.mello.nathalia.booksapi.domain.model.Book;
import com.mello.nathalia.booksapi.domain.model.BookRating;
import com.mello.nathalia.booksapi.domain.model.User;
import com.mello.nathalia.booksapi.domain.repository.BookRatingRepository;
import com.mello.nathalia.booksapi.domain.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookRatingRepository bookRatingRepository;

    @InjectMocks
    private RatingService ratingService;

    private Book book;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("João");
        user.setEmail("joao@email.com");

        book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");
        book.setRating(null);
        book.setRatingCount(0);
    }

    @Test
    @DisplayName("deve criar primeiro voto e calcular média corretamente")
    void rate_deveCriarPrimeiroVoto_quandoUsuarioNuncaVotou() {
        BigDecimal novoVoto = new BigDecimal("5.0");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRatingRepository.findByBookIdAndUserId(1L, 1L))
                .thenReturn(Optional.empty());
        when(bookRatingRepository.sumRatingsByBookId(1L))
                .thenReturn(new BigDecimal("5.0"));
        when(bookRatingRepository.countRatingsByBookId(1L)).thenReturn(1);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book result = ratingService.rate(1L, novoVoto, user);

        assertThat(result).isNotNull();

        verify(bookRatingRepository, times(1)).save(any(BookRating.class));

        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("deve atualizar voto existente e recalcular média mantendo ratingCount")
    void rate_deveAtualizarVotoExistente_quandoUsuarioJaVotou() {
        BigDecimal votoAntigo = new BigDecimal("3.0");
        BigDecimal novoVoto = new BigDecimal("5.0");

        book.setRating(new BigDecimal("4.0"));
        book.setRatingCount(2);

        BookRating votoExistente = new BookRating();
        votoExistente.setRating(votoAntigo);
        votoExistente.setBook(book);
        votoExistente.setUser(user);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRatingRepository.findByBookIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(votoExistente));
        when(bookRatingRepository.sumRatingsByBookId(1L))
                .thenReturn(new BigDecimal("9.0"));
        when(bookRatingRepository.countRatingsByBookId(1L)).thenReturn(2);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book result = ratingService.rate(1L, novoVoto, user);

        assertThat(result).isNotNull();

        verify(bookRatingRepository, times(1)).save(votoExistente);

        assertThat(book.getRatingCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("deve lançar BookNotFoundException quando livro não existe")
    void rate_deveLancarBookNotFoundException_quandoLivroNaoExiste() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        BookNotFoundException exception = catchThrowableOfType(
                BookNotFoundException.class,
                () -> ratingService.rate(99L, new BigDecimal("4.0"), user)
        );
        assertThat(exception).isNotNull();

        verify(bookRatingRepository, never()).save(any(BookRating.class));
        verify(bookRepository, never()).save(any(Book.class));
    }
}
