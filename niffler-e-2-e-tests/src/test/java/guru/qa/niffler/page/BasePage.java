package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;

public abstract class BasePage<T extends BasePage<?>> {

    protected final Header header = new Header();
    protected final SelenideElement snackbar;

    protected BasePage(SelenideDriver driver) {
        this.snackbar = driver.$(".MuiAlert-message");
    }

    protected BasePage() {
        this.snackbar = Selenide.$(".MuiAlert-message");
    }

    @SuppressWarnings("unchecked")
    @Step("Проверить сообщение алерта: {text}")
    public T checkSnackbarText(String text) {
        snackbar.shouldHave(text(text));
        return (T) this;
    }
}
