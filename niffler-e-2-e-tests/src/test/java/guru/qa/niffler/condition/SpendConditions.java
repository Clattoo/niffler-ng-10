package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.SpendJson;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

public class SpendConditions {

    public static WebElementsCondition spends(List<SpendJson> expectedSpends) {
        return new WebElementsCondition() {

            private final List<String> expected = mapToUiStrings(expectedSpends);

            @Nonnull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (expectedSpends.isEmpty()) {
                    throw new IllegalArgumentException("No expected spends given");
                }

                if (expected.size() != elements.size()) {
                    return rejected(
                            String.format(
                                    "List size mismatch (expected: %d, actual: %d)",
                                    expected.size(),
                                    elements.size()
                            ),
                            elements
                    );
                }

                boolean passed = true;
                List<String> actual = new ArrayList<>();

                for (int i = 0; i < elements.size(); i++) {
                    WebElement row = elements.get(i);
                    List<WebElement> cells = row.findElements(By.tagName("td"));
                    String actualRow = cells.get(1).getText().replace("\n", " ").trim()
                            + " " + cells.get(2).getText().replace("\n", " ").trim();

                    actual.add(actualRow);

                    if (passed) {
                        passed = actualRow.equals(expected.get(i));
                    }
                }

                if (!passed) {
                    return rejected(
                            String.format(
                                    "Spends mismatch (expected: %s, actual: %s)",
                                    expected,
                                    actual
                            ),
                            actual
                    );
                }

                return accepted();
            }

            @Override
            public String toString() {
                return expected.toString();
            }
        };
    }

    private static List<String> mapToUiStrings(List<SpendJson> spends) {
        DecimalFormat df = new DecimalFormat("0.#");

        return spends.stream()
                .map(s -> String.format(
                        "%s %s %s",
                        s.category().name(),
                        df.format(s.amount()),
                        s.currency().getCurrencySign()
                ))
                .toList();
    }
}