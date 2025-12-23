package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.*;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class Header {

    private final SelenideElement self = $("#root header");
    private final SelenideElement mainPageLink = self.$("a[href='/main']");
    private final SelenideElement profileButton = self.$("[data-testid='PersonIcon']");
    private final SelenideElement newSpendingButton = self.find("[href='/spending']");

    private final SelenideElement profileMenu = $("ul[role='menu']");
    private final ElementsCollection profileMenuTabs = profileMenu.$$("li[role='menuitem']");

    @Step("Открыть меню профиля пользователя")
    private void openProfileMenu() {
        profileButton.click();
        profileMenu.shouldBe(visible);
    }

    @Step("Переход на страницу Friends")
    public FriendsPage toFriendsPage() {
        openProfileMenu();
        profileMenuTabs.findBy(text("Friends")).click();
        return new FriendsPage();
    }

    @Step("Переход на страницу All Peoples")
    public PeoplePage toAllPeoplesPage() {
        openProfileMenu();
        profileMenuTabs.findBy(text("All People")).click();
        return new PeoplePage();
    }

    @Step("Переход на страницу Profile")
    public ProfilePage toProfilePage() {
        openProfileMenu();
        profileMenuTabs.findBy(text("Profile")).click();
        return new ProfilePage();
    }

    @Step("Выйти из профиля через пункт Sign Out")
    public LoginPage signOut() {
        openProfileMenu();
        profileMenuTabs.findBy(text("Sign out")).click();
        return new LoginPage();
    }

    @Step("Добавить новый spending через кнопку New spending")
    public EditSpendingPage addSpendingPage() {
        newSpendingButton.shouldBe(visible).click();
        return new EditSpendingPage();
    }

    @Step("Переход на главную страницу")
    public MainPage toMainPage() {
        mainPageLink.click();
        return new MainPage();
    }
}
