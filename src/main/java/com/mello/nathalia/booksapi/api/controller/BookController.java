package com.mello.nathalia.booksapi.api.controller;

import com.mello.nathalia.booksapi.api.mapper.BookMapper;
import com.mello.nathalia.booksapi.api.request.BookRequest;
import com.mello.nathalia.booksapi.api.response.BookResponse;
import com.mello.nathalia.booksapi.common.response.ErrorResponse;
import com.mello.nathalia.booksapi.domain.model.Book;
import com.mello.nathalia.booksapi.domain.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@Tag(name = "Books", description = "API para gerenciamento de livros")
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    public BookController(BookService bookService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    @GetMapping
    @Operation(summary = "Listar todos os livros",
            description = "Retorna uma lista de livros com possibilidade de filtrar por título, autor ou categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", 
                    description = "Livros encontrados com sucesso",
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "400", 
                    description = "Parâmetros de filtro inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<BookResponse>> getAllBooks(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "author", required = false) String author,
            @RequestParam(name = "category", required = false) String category
    ) {
        List<Book> books = bookService.filterBooks(title, author, category);
        List<BookResponse> response = books.stream()
                .map(bookMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar livro por ID",
            description = "Retorna os detalhes de um livro específico pelo seu identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Livro encontrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))),
            @ApiResponse(responseCode = "404",
                    description = "Livro não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo livro",
            description = "Cria um novo livro na base de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Livro criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Dados inválidos ou incompletos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409",
                    description = "Livro já existe no sistema",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BookResponse> createBook(
            @RequestBody(description = "Dados do livro a ser criado", required = true) 
            @Valid @org.springframework.web.bind.annotation.RequestBody BookRequest bookRequest) {
        Book newBook = bookService.createBook(bookRequest);
        return ResponseEntity.ok(bookMapper.toResponse(newBook));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar livro",
            description = "Atualiza os dados de um livro existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Livro atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Dados inválidos ou incompletos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Livro não encontrado",content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BookResponse> updateBookById(
            @PathVariable Long id,
            @RequestBody(description = "Dados atualizados do livro", required = true)
            @Valid @org.springframework.web.bind.annotation.RequestBody BookRequest updateBook) {
        Book updatedBook = bookService.updateBookById(id, updateBook);
        return ResponseEntity.ok(bookMapper.toResponse(updatedBook));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar livro",
            description = "Remove um livro da base de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Livro deletado com sucesso"),
            @ApiResponse(responseCode = "404",
                    description = "Livro não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<Void> deleteBookById(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return ResponseEntity.noContent().build();
    }

}