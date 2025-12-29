package guru.qa.niffler.page.component;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class ConfirmDeleteDialog extends BaseComponent<ConfirmDeleteDialog> {
    private final SelenideElement title = self.$("h2");
    private final SelenideElement deleteButton = self.$(byText("Delete"));
    private final SelenideElement cancelButton = self.$(byText("Cancel"));

    public ConfirmDeleteDialog() {
        super($("div[role='dialog']"));
    }

    @Step("Подтвердить удаление")
    public void confirmDelete() {
        deleteButton.shouldBe(Condition.visible).shouldBe(Condition.enabled).click();
        self.shouldNotBe(Condition.exist);
    }

    @Step("Отменить удаление")
    public void cancel() {
        cancelButton.shouldBe(Condition.visible).click();
        self.shouldNotBe(Condition.exist);
    }

    @Step("Проверка заголовка диалога")
    public ConfirmDeleteDialog checkDialogTitle(String expectedTitle) {
        title.shouldHave(Condition.text(expectedTitle));
        return this;
    }
}
