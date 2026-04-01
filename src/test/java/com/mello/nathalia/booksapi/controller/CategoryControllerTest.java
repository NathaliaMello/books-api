package com.mello.nathalia.booksapi.controller;

import com.mello.nathalia.booksapi.api.controller.CategoryController;
import com.mello.nathalia.booksapi.api.mapper.CategoryMapper;
import com.mello.nathalia.booksapi.api.request.CreateCategoryRequest;
import com.mello.nathalia.booksapi.api.request.UpdateCategoryRequest;
import com.mello.nathalia.booksapi.api.response.CategoryResponse;
import com.mello.nathalia.booksapi.common.exception.CategoryAlreadyExistsException;
import com.mello.nathalia.booksapi.common.exception.CategoryNotFoundException;
import com.mello.nathalia.booksapi.common.handler.GlobalExceptionHandler;
import com.mello.nathalia.booksapi.common.response.PageResponse;
import com.mello.nathalia.booksapi.domain.model.Category;
import com.mello.nathalia.booksapi.domain.service.CategoryService;
import com.mello.nathalia.booksapi.domain.service.UserService;
import com.mello.nathalia.booksapi.infrastructure.security.JwtAuthFilter;
import com.mello.nathalia.booksapi.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private CategoryMapper categoryMapper;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;   // <- adiciona de volta

    @MockitoBean
    private JwtService jwtService;         // <- adiciona

    @MockitoBean
    private UserService userService;

    private Category category;
    private CreateCategoryRequest createRequest;
    private UpdateCategoryRequest updateRequest;
    private CategoryResponse categoryResponse;


    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Tecnologia");

        createRequest = new CreateCategoryRequest("Tecnologia");
        updateRequest = new UpdateCategoryRequest("Fantasia");
        categoryResponse = new CategoryResponse(1L, "Tecnologia");
    }

    @Test
    @DisplayName("deve criar categoria e retornar 201")
    void create_deveCriarCategoria_quandoDadosSaoValidos() throws Exception {
        when(categoryService.create(any(CreateCategoryRequest.class))).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Tecnologia"));
    }

    @Test
    @DisplayName("deve retornar 409 quando categoria já existe")
    void create_deveRetornar409_quandoNomeJaExiste() throws Exception {

        when(categoryService.create(any(CreateCategoryRequest.class)))
                .thenThrow(new CategoryAlreadyExistsException("Já existe uma categoria com o nome: Tecnologia"));

        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    @DisplayName("deve atualizar categoria e retornar 200")
    void update_deveAtualizarCategoria_quandoDadosSaoValidos() throws Exception {
        when(categoryService.update(1L, updateRequest)).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        mockMvc.perform(put("/api/v1/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Tecnologia"));
    }

    @Test
    @DisplayName("deve lançar exceção quando a categoria não existe")
    void update_deveRetornar404_quandoCategoriaNaoExiste() throws Exception {

        when(categoryService.update(eq(1L), any(UpdateCategoryRequest.class)))
                .thenThrow(new CategoryNotFoundException("Categoria não encontrada com id: 1"));

        mockMvc.perform(put("/api/v1/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @DisplayName("deve retornar 409 quando nome já existe em outra categoria")
    void update_deveRetornar409_quandoNomeJaExiste() throws Exception {
       when(categoryService.update(eq(1L), any(UpdateCategoryRequest.class)))
               .thenThrow(new CategoryAlreadyExistsException("Já existe uma categoria com o nome: Fantasia"));

       mockMvc.perform(put("/api/v1/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));

    }

    @Test
    @DisplayName("deve buscar categoria por id e retornar 200")
    void findById_deveBuscarCategoriaPorId_quandoIdExiste() throws Exception {
        when(categoryService.findById(1L)).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        mockMvc.perform(get("/api/v1/categories/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Tecnologia"));
    }

    @Test
    @DisplayName("deve retornar 404 quando a categoria não existe")
    void findById_deveRetornar404_quandoIdNaoExiste() throws Exception {
        when(categoryService.findById(1L))
                .thenThrow(new CategoryNotFoundException("Categoria não encontrada com id: 1"));

        mockMvc.perform(get("/api/v1/categories/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @DisplayName("deve retornar lista de categorias quando existem")
    void findAll_deveBuscarTodasCategorias_quandoExistem() throws Exception {
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Fantasia");
        CategoryResponse categoryResponse2 = new CategoryResponse(2L, "Fantasia");

        when(categoryService.findAll()).thenReturn(List.of(category, category2));
        when(categoryMapper.toResponseList(List.of(category, category2)))
                .thenReturn(List.of(categoryResponse, categoryResponse2));

        mockMvc.perform(get("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Tecnologia"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Fantasia"));

    }

    @Test
    @DisplayName("deve retornar lista vazia quando não existem categorias")
    void findAll_deveRetornarVazio_quandoNaoExistem() throws Exception {
        when(categoryService.findAll()).thenReturn(java.util.List.of());
        when(categoryMapper.toResponseList(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("deve deletar categoria quando categoria existe")
    void delete_deveDeletarCategoria_quandoIdExiste() throws Exception {
        mockMvc.perform(delete("/api/v1/categories/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("deve lançar exceção quando categoria existe")
    void delete_deveRetornar404_quandoIdNaoExiste() throws Exception {
        doThrow(new CategoryNotFoundException("Categoria não encontrada com id: 1"))
                .when(categoryService).delete(1L);

        mockMvc.perform(delete("/api/v1/categories/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("deve retornar categorias paginadas")
    void findAllPaginated_deveRetornarCategoriasPaginadas() throws Exception {
        PageResponse<CategoryResponse> pageResponse = new PageResponse<>(
                List.of(categoryResponse),  // content
                0,                          // page
                10,                         // size
                1L,                         // totalElements
                1,                          // totalPages
                true,                       // first
                true                        // last
        );

        when(categoryService.findAllPaginated(0, 10)).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/categories/paginated")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Tecnologia"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));
    }

}
