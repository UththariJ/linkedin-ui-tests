package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class HomePage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final By SEARCH = By.cssSelector(
        "input.search-global-typeahead__input, input[placeholder*='Search'], input[aria-label*='Search']");

    public HomePage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public boolean isAuthenticated() {
        return wait.until(ExpectedConditions.or(
            ExpectedConditions.urlContains("/feed"),
            ExpectedConditions.visibilityOfElementLocated(SEARCH)));
    }

    public SearchResultsPage search(String term) {
        WebElement search = wait.until(ExpectedConditions.elementToBeClickable(SEARCH));
        search.clear();
        search.sendKeys(term, Keys.ENTER);
        return new SearchResultsPage(driver, wait);
    }
}
