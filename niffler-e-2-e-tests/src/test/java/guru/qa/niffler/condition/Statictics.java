package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.Bubble;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import java.util.*;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

public class Statictics {
    public static WebElementCondition statBubble(Color expectedColor) {
        return new WebElementCondition("color") {
            @Nonnull
            @Override
            public CheckResult check(Driver driver, WebElement element) {
                final String rgba = element.getCssValue("background-color");
                return new CheckResult(
                        expectedColor.rgb.equals(rgba),
                        rgba
                );
            }
        };
    }

    public static WebElementsCondition statBubble(Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            private final String expected = Arrays.stream(expectedBubbles)
                    .map(c -> c.color().rgb + "=" + c.text())
                    .toList().toString();

            @Nonnull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected colors given");
                }

                if (expectedBubbles.length != elements.size()) {
                    String message = String.format("List size mismatch (expected: %s, actual: %s",
                            expectedBubbles.length, elements.size());
                    return rejected(message, elements);
                }

                List<String> actualList = new ArrayList<>();
                boolean passed = true;

                for (int i = 0; i < elements.size(); i++) {
                    WebElement element = elements.get(i);
                    String actual = element.getCssValue("background-color") + "=" + element.getText();
                    actualList.add(actual);

                    String expectedValue = expectedBubbles[i].color().rgb + "=" + expectedBubbles[i].text();
                    if (passed) {
                        passed = expectedValue.equals(actual);
                    }
                }

                if (!passed) {
                    return rejected("List colors mismatch (expected: " + expected + ", actual: " + actualList + ")", actualList);
                }

                return accepted();
            }

            @Override
            public String toString() {
                return expected;
            }
        };
    }

    public static WebElementsCondition statBubblesInAnyOrder(Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            private final String expected = Arrays.stream(expectedBubbles).map(c -> c.color().rgb + "=" + c.text()).toList().toString();

            @Override
            @Nonnull
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected colors given");
                }

                if (expectedBubbles.length != elements.size()) {
                    String message = String.format("List size mismatch (expected: %s, actual: %s", expectedBubbles.length, elements.size());
                    return rejected(message, elements);
                }

                Map<String, String> actualRgbaWithText = getActualElementsByMap(elements);
                boolean passed = true;
                for (int i = 0; i < elements.size(); i++) {
                    final String colorToCheck = expectedBubbles[i].color().rgb;
                    final String textToCheck = expectedBubbles[i].text();
                    var exsisted = actualRgbaWithText.get(colorToCheck);
                    if (passed) {
                        passed = exsisted.equals(textToCheck);
                    }
                }
                if (!passed) {
                    return getFailedResult(expected, actualRgbaWithText);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expected;
            }
        };
    }

    public static WebElementsCondition statBubblesContains(Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            private final String expected = Arrays.stream(expectedBubbles).map(c -> c.color().rgb + "=" + c.text()).toList().toString();

            @Override
            @Nonnull
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected colors given");
                }

                Map<String, String> actualRgbaWithText = getActualElementsByMap(elements);
                boolean passed = true;
                for (int i = 0; i < expectedBubbles.length; i++) {
                    final String colorToCheck = expectedBubbles[i].color().rgb;
                    final String textToCheck = expectedBubbles[i].text();
                    var exsisted = actualRgbaWithText.get(colorToCheck);
                    if (passed) {
                        passed = exsisted.equals(textToCheck);
                    }
                }
                if (!passed) {
                    return getFailedResult(expected, actualRgbaWithText);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expected;
            }
        };
    }


    @NotNull
    private static CheckResult getFailedResult(String expected, Map<String, String> actualRgbaWithText) {
        final String actualRgba = actualRgbaWithText.toString();
        final String message = String.format("List colors mismatch (expected: %s, actual: %s", expected, actualRgba);
        return rejected(message, actualRgba);
    }

    @NotNull
    private static Map<String, String> getActualElementsByMap(List<WebElement> elements) {
        Map<String, String> actualRgbaListWithText = new HashMap<>();
        for (final WebElement elementToCheck : elements) {
            final String textElement = elementToCheck.getText();
            final String rgbaElement = elementToCheck.getCssValue("background-color");
            actualRgbaListWithText.put(rgbaElement, textElement);
        }
        return actualRgbaListWithText;
    }
}