package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.CurrencyValues;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage {
    private final SelenideElement amountInput = $("#amount");
    private final SelenideElement currencyInput = $("#currency");
    private final SelenideElement categoryInput = $("#category");
    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement saveBtn = $("#save");

    @Step("Установить описание для нового spending. Значение: {description}")
    public EditSpendingPage setNewSpendingDescription(String description) {
        descriptionInput.val(description);
        return this;
    }

    @Step("Установить сумму для нового spending. Значение: {amount}")
    public EditSpendingPage setAmountSpending(double amount) {
        amountInput.clear();
        amountInput.setValue(String.valueOf(amount));
        return this;
    }

    @Step("Установить валюту для нового spending. Значение: {currency}")
    public EditSpendingPage setCurrencySpending(CurrencyValues currency) {
        currencyInput.setValue(currency.name());
        return this;
    }

    @Step("Установить категорию для нового spending. Значение: {category}")
    public EditSpendingPage setCategorySpending(String category) {
        categoryInput.setValue(category);
        return this;
    }

    @Step("Сохранить изменения для нового spending")
    public MainPage save() {
        saveBtn.click();
        return new MainPage();
    }
}
