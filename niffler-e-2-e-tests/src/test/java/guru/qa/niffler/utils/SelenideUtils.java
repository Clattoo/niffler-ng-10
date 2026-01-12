package guru.qa.niffler.utils;

import com.codeborne.selenide.SelenideConfig;

public class SelenideUtils {
    public static final SelenideConfig CHROME_CONFIG = new SelenideConfig()
            .browser("chrome")
            .pageLoadStrategy("eager")
            .timeout(5000L);

    public static final SelenideConfig FIREFOX_CONFIG = new SelenideConfig()
            .browser("firefox")
            .pageLoadStrategy("eager")
            .timeout(5000L);
}
