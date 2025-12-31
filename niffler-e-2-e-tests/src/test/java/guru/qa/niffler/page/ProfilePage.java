package guru.qa.niffler.page;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class ProfilePage extends BasePage<ProfilePage> {

    public static String url = Config.getInstance().frontUrl() + "profile";
    private final SelenideElement uploadPictureButton = $(".image__input-label");
    private final SelenideElement registerPassKey = $("#:r11:");
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement nameInput = $("#name");
    private final SelenideElement saveChangesButton = $$("button[type='submit']").findBy(text("Save changes"));
    private final SelenideElement showArchivedCategoriesCheckbox = $x("//span[contains(text(),'Show archived')]/..//input[@type='checkbox']");
    private final SelenideElement addNewCategoryInput = $("#category");
    private final SelenideElement submitArchive = $x("//button[contains(@class,'MuiButtonBase-root') and normalize-space(text())='Archive']");
    private final SelenideElement submitUnArchive = $x("//button[contains(@class,'MuiButtonBase-root') and normalize-space(text())='Unarchive']");
    private final SelenideElement uploadNewPictureInput = $("input[type='file']");


    private SelenideElement findCategory(String category) {
        return $x("//span[contains(@class,'MuiChip-label') and text()='" + category + "']/ancestor::*[2]");
    }

    private SelenideElement editCategoryButton(String category) {
        return findCategory(category).find("[aria-label=\"Edit category\"]");
    }

    private SelenideElement archiveCategoryButton(String category) {
        return findCategory(category).find("[aria-label=\"Archive category\"]");
    }

    private SelenideElement unArchiveCategoryButton(String category) {
        return findCategory(category).find("[aria-label=\"Unarchive category\"]");
    }

    @Step("Загрузить аватар пользователя: {imageFile.name}")
    public ProfilePage uploadPicture(File imageFile) {
        uploadPictureButton.uploadFile(imageFile);
        return this;
    }

    @Step("Клик по кнопке регистрации PassKey")
    public ProfilePage clickRegisterPassKey() {
        registerPassKey.click();
        // не очень понятно что дальше
        return this;
    }

    @Step("Проверить значение username: {value}")
    public ProfilePage checkUserNameInputValue(String value) {
        usernameInput.shouldHave(text(value));
        return this;
    }

    @Step("Проверить, что поле username отображается")
    public ProfilePage isUsernameFieldVisible() {
        usernameInput.shouldBe(visible);
        return this;
    }

    @Step("Установить имя пользователя: {name}")
    public ProfilePage setName(String name) {
        nameInput.val(name);
        return this;
    }

    @Step("Очистить поле имени пользователя")
    public ProfilePage clearName() {
        nameInput.clear();
        return this;
    }

    @Step("Проверить имя пользователя {value} в поле ввода")
    public ProfilePage checkNameInputValue(String value) {
        nameInput.shouldHave(value(value));
        return this;
    }

    @Step("Проверить, что поле имени отображается")
    public ProfilePage isNameFieldVisible() {
        nameInput.shouldBe(visible);
        return this;
    }

    @Step("Сохранить изменения в профиле")
    public ProfilePage clickSaveChanges() {
        saveChangesButton.click();
        return this;
    }

    @Step("Включить отображение архивных категорий")
    public ProfilePage checkShowArchivedCategories() {
        if (!showArchivedCategoriesCheckbox.isSelected()) {
            showArchivedCategoriesCheckbox.click(ClickOptions.usingJavaScript());
        }
        return this;
    }

    @Step("Выключить отображение архивных категорий")
    public ProfilePage uncheckShowArchivedCategories() {
        if (showArchivedCategoriesCheckbox.isSelected()) {
            showArchivedCategoriesCheckbox.click(ClickOptions.usingJavaScript());
        }
        return this;
    }

    @Step("Переключить чекбокс 'Show archived categories'")
    public ProfilePage toggleShowArchivedCategories() {
        showArchivedCategoriesCheckbox.shouldBe(clickable).click();
        return this;
    }

    @Step("Проверить, что чекбокс 'Show archived categories' включён")
    public ProfilePage isShowArchivedCategoriesChecked() {
        showArchivedCategoriesCheckbox.shouldBe(selected);
        return this;
    }

    @Step("Ввести имя новой категории: {categoryName}")
    public ProfilePage setNewCategoryName(String categoryName) {
        addNewCategoryInput.val(categoryName);
        return this;
    }

    @Step("Добавить новую категорию: {categoryName}")
    public ProfilePage addNewCategory(String categoryName) {
        setNewCategoryName(categoryName).pressEnterOnNewCategory();
        return this;
    }

    @Step("Очистить поле новой категории")
    public ProfilePage clearNewCategoryInput() {
        addNewCategoryInput.clear();
        return this;
    }

    @Step("Подтвердить добавление категории нажатием Enter")
    public ProfilePage pressEnterOnNewCategory() {
        addNewCategoryInput.pressEnter();
        return this;
    }

    @Step("Проверить значение поля новой категории: {value}")
    public ProfilePage checkNewCategoryInputValue(String value) {
        addNewCategoryInput.shouldHave(text(value));
        return this;
    }

    @Step("Нажать кнопку редактирования категории: {category}")
    public ProfilePage clickEditCategoryButton(String category) {
        editCategoryButton(category).click();
        return this;
    }

    @Step("Архивировать категорию: {category}")
    public ProfilePage archiveCategory(String category) {
        archiveCategoryButton(category).click();
        submitArchive.click();
        return this;
    }

    @Step("Разархивировать категорию: {category}")
    public ProfilePage unArchiveCategory(String category) {
        unArchiveCategoryButton(category).click();
        submitUnArchive.click();
        return this;
    }

    @Step("Проверить, что категория существует: {category}")
    public ProfilePage isCategoryExists(String category) {
        findCategory(category).shouldBe(exist);
        return this;
    }

    @Step("Вернуться на главную страницу")
    public MainPage returnToMainPage() {
        return header.toMainPage();
    }

    @Step("Открыть страницу профиля")
    public ProfilePage openProfile() {
        return header.toProfilePage();
    }

    @Step("Загрузка изображения профиля")
    public ProfilePage uploadProfileImage(String path) {
        uploadNewPictureInput.uploadFromClasspath(path);
        saveChangesButton.click();
        return this;
    }

    @Step("Сравнение скриншотов профиля")
    public void assertProfilePicScreenshot(BufferedImage expected) throws IOException {
        BufferedImage actual = ImageIO.read($("img[class*='MuiAvatar-img'][class*='css-1hy9t21']").screenshot());
        ScreenDiffResult diffResult = new ScreenDiffResult(expected, actual);
        assertFalse(diffResult.getAsBoolean(), "Скриншоты отличаются");
    }
}
