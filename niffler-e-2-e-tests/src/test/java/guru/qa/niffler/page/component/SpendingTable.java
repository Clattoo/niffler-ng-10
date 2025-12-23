package guru.qa.niffler.page.component;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.DataFilterValues;
import guru.qa.niffler.page.EditSpendingPage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class SpendingTable {
    private final SelenideElement self = $("#spendings");

    private final SearchField searchField = new SearchField();
    private final ConfirmDeleteDialog confirmDeleteDialog = new ConfirmDeleteDialog();

    private final By editSpendButton = By.cssSelector("button");
    private final SelenideElement periodSelect = self.$("#period");
    private final ElementsCollection periodList = self.$$(".MuiMenu-list");
    private final SelenideElement deleteButton = self.$("#delete");
    private final ElementsCollection spendingRows = self.$("tbody").$$("tr");
    private final SelenideElement prevPageButton = self.$("#page-prev");
    private final SelenideElement nextPageButton = self.$("#page-next");
    private final SelenideElement checkboxButton = self.$("[data-testid='CheckBoxIcon']");

    @Step("Выбрать период в таблице spending: {period}")
    public SpendingTable selectPeriod(DataFilterValues period) {
        periodSelect.click();
        periodList.find(text(period.name())).click();
        return this;
    }

    @Step("Изменить spending: {description}")
    public EditSpendingPage editSpending(String description) {
        searchField.search(description);
        spendingRows.first().find(editSpendButton).click();
        return new EditSpendingPage();
    }

    @Step("Удалить spending: {description}")
    public SpendingTable deleteSpending(String description) {
        searchField.search(description);
        checkboxButton.click();
        deleteButton.click();
        confirmDeleteDialog.confirmDelete();
        return this;
    }

    @Step("Найти spending: {description}")
    public SpendingTable searchSpendingByDescription(String description) {
        searchField.search(description);
        spendingRows.shouldHave(CollectionCondition.itemWithText(description));
        return this;
    }

    @Step("Проверить наличие в таблицы следующих spendings: {expectedSpends}")
    public SpendingTable checkTableContains(String... expectedSpends) {
        for (String spend : expectedSpends) {
            searchField.clearIfNotEmpty();
            searchField.search(spend);
            spendingRows.first().find("td", 3).shouldHave(text(spend));
        }
        return this;
    }

    @Step("Проверить размер таблицы Spendings")
    public SpendingTable checkTableSize(int expectedSize) {
        spendingRows.shouldHave(size(expectedSize));
        return this;
    }
}
