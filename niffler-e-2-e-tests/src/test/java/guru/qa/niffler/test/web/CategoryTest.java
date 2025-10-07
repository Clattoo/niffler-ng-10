package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class CategoryTest {
    private static final Config CFG = Config.getInstance();

    @Category(
            username = "Clatto",
            archived = false
    )
    @Test
    @DisplayName("Архивация категории у пользователя")
    public void archiveCategoryTest(CategoryJson categoryJson) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(categoryJson.username(), "qwerty123")
                .editProfile()
                .archiveCategory(categoryJson.name())
                .checkShowArchivedCategories()
                .isCategoryExists(categoryJson.name());
    }

    @Category(
            username = "Clatto",
            archived = true
    )
    @Test
    @DisplayName("Разархивация категории у пользователя")
    public void UnarchiveCategoryTest(CategoryJson categoryJson) {

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(categoryJson.username(), "qwerty123")
                .editProfile()
                .checkShowArchivedCategories()
                .unArchiveCategory(categoryJson.name())
                .uncheckShowArchivedCategories()
                .isCategoryExists(categoryJson.name());
    }
}