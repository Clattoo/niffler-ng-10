package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.Bubble;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.condition.Statictics.*;

@ParametersAreNonnullByDefault
public class Statistics extends BaseComponent<Statistics>{

    public Statistics() {
        super($("#stat"));
    }

    private final ElementsCollection bubbles = self.$("#legend-container").$$("li");
    private final SelenideElement chart = $("canvas[role='img']");

    @Nonnull
    public BufferedImage chartScreenshot() throws IOException {
        return ImageIO.read($("canvas[role='img']").screenshot());
    }

    @Nonnull
    public Statistics checkBubbles(Bubble... expectedBubbles) {
        bubbles.should(statBubble(expectedBubbles));
        return this;
    }

    @Nonnull
    public Statistics checkBubblesInAnyOrder(Bubble... expectedBubbles) {
        bubbles.should(statBubblesInAnyOrder(expectedBubbles));
        return this;
    }

    @Nonnull
    public Statistics checkBubblesContains(Bubble... expectedBubbles) {
        bubbles.should(statBubblesContains(expectedBubbles));
        return this;
    }
}
