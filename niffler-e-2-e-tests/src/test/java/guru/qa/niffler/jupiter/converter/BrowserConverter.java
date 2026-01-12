package guru.qa.niffler.jupiter.converter;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

public class BrowserConverter implements ArgumentConverter {

    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        if (!(source instanceof Browser)) {
            throw new ArgumentConversionException("Wrong argument type");
        }

        Browser browser = (Browser) source;
        return new SelenideDriver(getConfig(browser));
    }

    private SelenideConfig getConfig(Browser browser) {
        return switch (browser) {
            case FIREFOX -> SelenideUtils.FIREFOX_CONFIG;
            default -> SelenideUtils.CHROME_CONFIG;
        };
    }
}
