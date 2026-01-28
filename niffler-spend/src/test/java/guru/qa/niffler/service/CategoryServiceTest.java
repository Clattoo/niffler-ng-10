package guru.qa.niffler.service;

import guru.qa.niffler.data.CategoryEntity;
import guru.qa.niffler.data.repository.CategoryRepository;
import guru.qa.niffler.ex.CategoryNotFoundException;
import guru.qa.niffler.ex.InvalidCategoryNameException;
import guru.qa.niffler.ex.TooManyCategoriesException;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Test
  void categoryNotFoundExceptionShouldBeThrown(@Mock CategoryRepository categoryRepository) {
    final String username = "not_found";
    final UUID id = UUID.randomUUID();

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.empty());

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        "",
        username,
        true
    );

    CategoryNotFoundException ex = Assertions.assertThrows(
        CategoryNotFoundException.class,
        () -> categoryService.update(categoryJson)
    );
    Assertions.assertEquals(
        "Can`t find category by id: '" + id + "'",
        ex.getMessage()
    );
  }

  @ValueSource(strings = {"Archived", "ARCHIVED", "ArchIved"})
  @ParameterizedTest
  void categoryNameArchivedShouldBeDenied(String catName, @Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID id = UUID.randomUUID();
    final CategoryEntity cat = new CategoryEntity();

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.of(
            cat
        ));

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        catName,
        username,
        true
    );

    InvalidCategoryNameException ex = Assertions.assertThrows(
        InvalidCategoryNameException.class,
        () -> categoryService.update(categoryJson)
    );
    Assertions.assertEquals(
        "Can`t add category with name: '" + catName + "'",
        ex.getMessage()
    );
  }

  @Test
  void onlyTwoFieldsShouldBeUpdated(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID id = UUID.randomUUID();
    final CategoryEntity cat = new CategoryEntity();
    cat.setId(id);
    cat.setUsername(username);
    cat.setName("Магазины");
    cat.setArchived(false);

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.of(
            cat
        ));
    Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        "Бары",
        username,
        true
    );

    categoryService.update(categoryJson);
    ArgumentCaptor<CategoryEntity> argumentCaptor = ArgumentCaptor.forClass(CategoryEntity.class);
    verify(categoryRepository).save(argumentCaptor.capture());
    assertEquals("Бары", argumentCaptor.getValue().getName());
    assertEquals("duck", argumentCaptor.getValue().getUsername());
    assertTrue(argumentCaptor.getValue().isArchived());
    assertEquals(id, argumentCaptor.getValue().getId());
  }

  @Test
  void getAllCategoriesShouldFilterArchived(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";

    CategoryEntity cat1 = new CategoryEntity();
    cat1.setName("A"); cat1.setUsername(username); cat1.setArchived(false);

    CategoryEntity cat2 = new CategoryEntity();
    cat2.setName("B"); cat2.setUsername(username); cat2.setArchived(true);

    Mockito.when(categoryRepository.findAllByUsernameOrderByName(username))
            .thenReturn(List.of(cat1, cat2));

    CategoryService service = new CategoryService(categoryRepository);

    List<CategoryJson> filtered = service.getAllCategories(username, true);
    assertEquals(1, filtered.size());
    assertEquals("A", filtered.get(0).name());

    List<CategoryJson> all = service.getAllCategories(username, false);
    assertEquals(2, all.size());
  }

  @Test
  void updateShouldThrowTooManyCategoriesException(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID id = UUID.randomUUID();

    CategoryEntity cat = new CategoryEntity();
    cat.setId(id); cat.setUsername(username); cat.setName("Old"); cat.setArchived(true);

    Mockito.when(categoryRepository.findByUsernameAndId(username, id))
            .thenReturn(Optional.of(cat));

    Mockito.when(categoryRepository.countByUsernameAndArchived(username, false))
            .thenReturn(8L);

    CategoryService service = new CategoryService(categoryRepository);
    CategoryJson categoryJson = new CategoryJson(id, "New", username, false);

    TooManyCategoriesException ex = assertThrows(
            TooManyCategoriesException.class,
            () -> service.update(categoryJson)
    );
    assertEquals("Can`t unarchive category for user: '" + username + "'", ex.getMessage());
  }

  @Test
  void saveShouldThrowInvalidCategoryNameException(@Mock CategoryRepository categoryRepository) {
    CategoryService service = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(null, "Archived", "duck", false);

    InvalidCategoryNameException ex = assertThrows(
            InvalidCategoryNameException.class,
            () -> service.save(categoryJson)
    );
    assertEquals("Can`t add category with name: 'Archived'", ex.getMessage());
  }

  @Test
  void saveShouldThrowTooManyCategoriesException(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    CategoryService service = new CategoryService(categoryRepository);

    Mockito.when(categoryRepository.countByUsernameAndArchived(username, false))
            .thenReturn(8L);

    CategoryJson categoryJson = new CategoryJson(null, "NewCat", username, false);

    TooManyCategoriesException ex = assertThrows(
            TooManyCategoriesException.class,
            () -> service.save(categoryJson)
    );
    assertEquals("Can`t add over than 8 categories for user: '" + username + "'", ex.getMessage());
  }

  @Test
  void saveShouldSuccessfullySaveCategory(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    CategoryEntity savedEntity = new CategoryEntity();
    savedEntity.setId(UUID.randomUUID());
    savedEntity.setUsername(username);
    savedEntity.setName("NewCat");
    savedEntity.setArchived(false);

    Mockito.when(categoryRepository.countByUsernameAndArchived(username, false))
            .thenReturn(0L);
    Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
            .thenReturn(savedEntity);

    CategoryService service = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(null, "NewCat", username, false);
    CategoryJson result = service.addCategory(categoryJson);

    assertEquals(savedEntity.getId(), result.id());
    assertEquals("NewCat", result.name());
    assertEquals(username, result.username());
    assertFalse(result.archived());
  }

  @Test
  void updateShouldSuccessfullyUpdateCategory(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID id = UUID.randomUUID();

    CategoryEntity existing = new CategoryEntity();
    existing.setId(id); existing.setUsername(username); existing.setName("Old"); existing.setArchived(true);

    Mockito.when(categoryRepository.findByUsernameAndId(username, id))
            .thenReturn(Optional.of(existing));
    Mockito.when(categoryRepository.countByUsernameAndArchived(username, false))
            .thenReturn(0L);
    Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    CategoryService service = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(id, "UpdatedName", username, false);
    CategoryJson result = service.update(categoryJson);

    assertEquals("UpdatedName", result.name());
    assertEquals(username, result.username());
    assertFalse(result.archived());
  }

  @Test
  void getOrSaveShouldReturnExistingCategory(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final CategoryEntity existing = new CategoryEntity();
    existing.setId(UUID.randomUUID());
    existing.setUsername(username);
    existing.setName("Food");

    Mockito.when(categoryRepository.findByUsernameAndName(username, "Food"))
            .thenReturn(Optional.of(existing));

    CategoryService service = new CategoryService(categoryRepository);
    CategoryJson input = new CategoryJson(null, "Food", username, false);

    CategoryEntity result = service.getOrSave(input);
    assertEquals(existing.getId(), result.getId());
    verify(categoryRepository, Mockito.never()).save(any());
  }

  @Test
  void getOrSaveShouldSaveNewCategoryIfNotExist(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final CategoryEntity saved = new CategoryEntity();
    saved.setId(UUID.randomUUID());
    saved.setUsername(username);
    saved.setName("Food");

    Mockito.when(categoryRepository.findByUsernameAndName(username, "Food"))
            .thenReturn(Optional.empty());
    Mockito.when(categoryRepository.countByUsernameAndArchived(username, false))
            .thenReturn(0L);
    Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
            .thenReturn(saved);

    CategoryService service = new CategoryService(categoryRepository);
    CategoryJson input = new CategoryJson(null, "Food", username, false);

    CategoryEntity result = service.getOrSave(input);
    assertEquals(saved.getId(), result.getId());
    verify(categoryRepository).save(any());
  }

  @Test
  void addCategoryShouldThrowInvalidCategoryNameException(@Mock CategoryRepository categoryRepository) {
    CategoryService service = new CategoryService(categoryRepository);
    CategoryJson categoryJson = new CategoryJson(null, "Archived", "duck", false);

    InvalidCategoryNameException ex = assertThrows(
            InvalidCategoryNameException.class,
            () -> service.addCategory(categoryJson)
    );

    assertEquals("Can`t add category with name: 'Archived'", ex.getMessage());
  }
}