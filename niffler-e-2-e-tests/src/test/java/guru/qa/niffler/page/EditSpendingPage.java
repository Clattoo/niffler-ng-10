package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.CurrencyValues;

import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage {
    private final SelenideElement amountInput = $("#amount");
    private final SelenideElement currencyInput = $("#currency");
    private final SelenideElement categoryInput = $("#category");
    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement saveBtn = $("#save");

    public EditSpendingPage setNewSpendingDescription(String description) {
        descriptionInput.val(description);
        return this;
    }

    public EditSpendingPage setAmountSpending(double amount) {
        amountInput.clear();
        amountInput.setValue(String.valueOf(amount));
        return this;
    }

    public EditSpendingPage setCurrencySpending(CurrencyValues currency) {
        currencyInput.setValue(currency.name());
        return this;
    }

    public EditSpendingPage setCategorySpending(String category) {
        categoryInput.setValue(category);
        return this;
    }

    public MainPage save() {
        saveBtn.click();
        return new MainPage();
    }
}
