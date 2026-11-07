package pages;

import java.net.URI;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class SearchResultsPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final By RESULT = By.cssSelector(
        "main a[href*='/in/'], main a[href*='/company/'], main a[href*='/school/'], " +
        "main a[href*='/posts/'], main a[href*='/feed/update/'], main a[href*='/jobs/']");

    public SearchResultsPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public boolean hasVisibleResult() {
        wait.until(ExpectedConditions.urlContains("/search/results/"));
        return wait.until(d -> d.findElements(RESULT).stream()
            .anyMatch(element -> element.isDisplayed() && !element.getText().isBlank()));
    }

    public String clickFirstResult() {
        Set<String> originalWindows = driver.getWindowHandles();
        String originalUrl = driver.getCurrentUrl();
        WebElement result = wait.until(d -> d.findElements(RESULT).stream()
            .filter(element -> element.isDisplayed() && !element.getText().isBlank())
            .findFirst().orElse(null));
        String expectedUrl = result.getAttribute("href");
        wait.until(ExpectedConditions.elementToBeClickable(result)).click();
        wait.until(d -> d.getWindowHandles().size() > originalWindows.size()
            || !d.getCurrentUrl().equals(originalUrl));
        for (String handle : driver.getWindowHandles()) {
            if (!originalWindows.contains(handle)) {
                driver.switchTo().window(handle);
                break;
            }
        }
        wait.until(d -> normalize(d.getCurrentUrl()).equals(normalize(expectedUrl)));
        return expectedUrl;
    }

    public String normalize(String url) {
        URI uri = URI.create(url);
        return uri.getScheme() + "://" + uri.getHost() + uri.getPath().replaceAll("/+$", "");
    }
}
