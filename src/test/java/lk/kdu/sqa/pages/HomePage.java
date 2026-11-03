package lk.kdu.sqa.pages;

import java.net.URI;
import org.openqa.selenium.*;

public final class HomePage extends BasePage {
    private static final By SEARCH = By.cssSelector("input.search-global-typeahead__input, input[placeholder*='Search'], input[aria-label*='Search']");
    public HomePage(WebDriver driver) { super(driver); }
    public boolean isLoggedIn() {
        return until(d -> {
            URI uri = URI.create(d.getCurrentUrl());
            return "www.linkedin.com".equalsIgnoreCase(uri.getHost()) && uri.getPath().startsWith("/feed")
                && d.findElements(SEARCH).stream().anyMatch(WebElement::isDisplayed);
        });
    }
    public SearchResultsPage search(String term) {
        WebElement search = visible(SEARCH);
        search.click(); search.clear(); search.sendKeys(term); search.sendKeys(Keys.ENTER);
        return new SearchResultsPage(driver);
    }
}
