package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class SearchField {
    private final SelenideElement self = $("form[class*='MuiBox-root']");

    private final SelenideElement searchInput = self.$("input[aria-label='search']");
    private final SelenideElement clearSearchButton = self.$("#input-clear");

    @Step("Поиск по фразе: {query}")
    public SearchField search(String query) {
        clearIfNotEmpty();
        searchInput.shouldBe(visible).setValue(query);
        return this;
    }

    @Step("Очистить строку поиска в случае если она не пустая")
    public SearchField clearIfNotEmpty() {
        if (!searchInput.getValue().trim().isEmpty()) {
            clearSearchButton.click();
        }
        return this;
    }
}
