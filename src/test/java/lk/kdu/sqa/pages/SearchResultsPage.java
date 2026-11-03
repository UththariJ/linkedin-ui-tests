package lk.kdu.sqa.pages;

import java.net.URI;
import java.util.*;
import org.openqa.selenium.*;

public final class SearchResultsPage extends BasePage {
    // Restrict to actual entity result links in the main results region.
    private static final By RESULTS = By.cssSelector("main .entity-result__title-text a[href], main .reusable-search__result-container a.app-aware-link[href], main [data-chameleon-result-urn] a[href]");
    public SearchResultsPage(WebDriver driver) { super(driver); }
    private boolean entityUrl(String href) {
        if (href == null) return false;
        try {
            URI u = URI.create(href);
            String p = u.getPath();
            return "https".equalsIgnoreCase(u.getScheme()) && "www.linkedin.com".equalsIgnoreCase(u.getHost())
                && p != null && (p.startsWith("/in/") || p.startsWith("/company/") || p.startsWith("/school/") || p.startsWith("/posts/") || p.startsWith("/feed/update/"));
        } catch (IllegalArgumentException e) { return false; }
    }
    private List<WebElement> results() {
        return driver.findElements(RESULTS).stream().filter(e -> e.isDisplayed() && entityUrl(e.getAttribute("href"))).toList();
    }
    public boolean isDisplayed() {
        return until(d -> URI.create(d.getCurrentUrl()).getPath().startsWith("/search/results/") && !results().isEmpty());
    }
    public String openFirstResult() {
        String original = driver.getWindowHandle();
        Set<String> before = driver.getWindowHandles();
        String expected = until(d -> {
            List<WebElement> links = results();
            if (links.isEmpty()) return null;
            WebElement first = links.get(0);
            String href = first.getAttribute("href");
            if (!first.isEnabled()) return null;
            first.click();
            return href;
        });
        String path = normalize(URI.create(expected).getPath());
        until(d -> {
            for (String handle : d.getWindowHandles()) {
                if (!handle.equals(original) && !before.contains(handle)) { d.switchTo().window(handle); break; }
            }
            // Check again after switching tabs, so authwall redirects cannot pass.
            lk.kdu.sqa.support.SecurityGate.check(d);
            URI actual = URI.create(d.getCurrentUrl());
            return "www.linkedin.com".equalsIgnoreCase(actual.getHost()) && path.equals(normalize(actual.getPath()));
        });
        visible(By.cssSelector("main"));
        return path;
    }
    public static String normalize(String path) { return path == null ? "" : path.replaceAll("/+$", ""); }
}
