package lk.kdu.sqa.pages;

import java.time.Duration;
import java.util.function.Function;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import lk.kdu.sqa.support.SecurityGate;

public abstract class BasePage {
    protected final WebDriver driver;
    protected BasePage(WebDriver driver) { this.driver = driver; }
    protected <T> T until(Function<WebDriver, T> condition) {
        return new WebDriverWait(driver, Duration.ofSeconds(25))
            .ignoring(StaleElementReferenceException.class)
            .until(d -> { SecurityGate.check(d); return condition.apply(d); });
    }
    protected WebElement visible(By by) {
        return until(d -> d.findElements(by).stream().filter(WebElement::isDisplayed).findFirst().orElse(null));
    }
    protected void click(By by) {
        until(d -> {
            for (WebElement e : d.findElements(by)) {
                if (e.isDisplayed() && e.isEnabled()) { e.click(); return true; }
            }
            return false;
        });
    }
}
