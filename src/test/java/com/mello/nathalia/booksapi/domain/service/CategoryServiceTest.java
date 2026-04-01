package com.mello.nathalia.booksapi.domain.service;

import com.mello.nathalia.booksapi.api.mapper.CategoryMapper;
import com.mello.nathalia.booksapi.api.request.CreateCategoryRequest;
import com.mello.nathalia.booksapi.api.request.UpdateCategoryRequest;
import com.mello.nathalia.booksapi.common.exception.CategoryAlreadyExistsException;
import com.mello.nathalia.booksapi.common.exception.CategoryNotFoundException;
import com.mello.nathalia.booksapi.domain.model.Category;
import com.mello.nathalia.booksapi.domain.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CreateCategoryRequest saveRequest;
    private UpdateCategoryRequest updateRequest;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Tecnologia");

        saveRequest = new CreateCategoryRequest("Tecnologia");
        updateRequest = new UpdateCategoryRequest("Fantasia");
    }

    @Test
    @DisplayName("Deve criar categoria com sucesso quando os dados são validos")
    void create_deveCriarCategoria_quandoDadosSaoValidos() {
        when(categoryMapper.toEntity(saveRequest)).thenReturn(category);
        when(categoryRepository.existsByName(any())).thenReturn(Boolean.FALSE);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.create(saveRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Tecnologia");
        assertThat(result.getId()).isEqualTo(1L);

        verify(categoryRepository, times(1)).save(any(Category.class));

    }

    @Test
    @DisplayName("Deve lançar CategoryAlreadyExistsException quando o nome for duplicado")
    void create_deveLancarException_quandoNomeJaExiste() {
        when(categoryRepository.existsByName(any())).thenReturn(Boolean.TRUE);

        assertThatThrownBy(() -> categoryService.create(saveRequest))
                .isInstanceOf(CategoryAlreadyExistsException.class);

        verify(categoryRepository, times(1)).existsByName(any());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar a categoria com sucesso quandos os dados são validos")
    void update_deveAtualizarCategoria_quandoDadosSaoValidos() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName(any())).thenReturn(Boolean.FALSE);
        doAnswer(invocation -> {
            category.setName("Fantasia");
            return null;
        }).when(categoryMapper).updateEntity(updateRequest, category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.update(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Fantasia");
        assertThat(result.getId()).isEqualTo(1L);

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).existsByName(any());
        verify(categoryMapper, times(1)).updateEntity(updateRequest, category);
        verify(categoryRepository, times(1)).save(any(Category.class));

    }

    @Test
    @DisplayName("Deve lançar CategoryAlreadyExistsException quando nome já existe em outra categoria")
    void update_deveLancarException_quandoNomeJaExiste() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName(any())).thenReturn(Boolean.TRUE);

        assertThatThrownBy(() -> categoryService.update(1L, updateRequest))
                .isInstanceOf(CategoryAlreadyExistsException.class);

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).existsByName(any());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar CategoryNotFoundException quando a categoria não existe")
    void update_deveLancarException_quandoCategoriaNaoExiste() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.update(1L, updateRequest))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, never()).existsByName(any());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar categoria por id com sucesso")
    void findById_deveBuscarCategoriaPorId_quandoIdExiste() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category result = categoryService.findById(1L);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Tecnologia");

        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar CategoryNotFoundException quando categoria não é encontrada")
    void findById_deveLancarException_quandoIdNaoExiste() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(1L))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve buscar todas as categorias com sucesso")
    void findAll_deveBuscarTodasCategorias_quandoExistem() {

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Fantasia");

        when(categoryRepository.findAll()).thenReturn(java.util.List.of(category, category2));

        List<Category> result = categoryService.findAll();

        assertThat(result)
            .isNotNull()
            .hasSize(2)
            .satisfies(list -> {
                assertThat(result.get(0).getId()).isEqualTo(1L);
                assertThat(result.get(0).getName()).isEqualTo("Tecnologia");
                assertThat(result.get(1).getId()).isEqualTo(2L);
                assertThat(result.get(1).getName()).isEqualTo("Fantasia");
            });

        verify(categoryRepository, times(1)).findAll();

    }

    @Test
    @DisplayName("Deve retronar vazia quando não existe categoria")
    void findAll_deveRetornarVazio_quandoNaoExistem() {
        when(categoryRepository.findAll()).thenReturn(List.of());

        List<Category> result = categoryService.findAll();

        assertThat(result)
            .isNotNull()
            .isEmpty();

        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve deletar categoria com sucesso quando existir")
    void delete_deveDeletarCategoria_quandoExiste() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).delete(category);

        categoryService.delete(1L);

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    @DisplayName("Deve lançar CategoryNotFoundException quando a categoria não existir")
    void delete_deveLancarException_quandoNaoExistir() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.delete(1L))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, never()).delete(any());
    }

}
