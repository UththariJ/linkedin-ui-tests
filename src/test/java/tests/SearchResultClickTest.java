package tests;

import base.BaseTest;
import pages.HomePage;
import pages.SearchResultsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class SearchResultClickTest extends BaseTest {
    @Test(priority = 4, description = "TC04: first result opens its captured destination")
    public void clickFirstSearchResult() {
        login();
        SearchResultsPage results = new HomePage(driver, wait).search("software testing");
        Assert.assertTrue(results.hasVisibleResult());
        String expectedUrl = results.clickFirstResult();
        Assert.assertEquals(results.normalize(driver.getCurrentUrl()), results.normalize(expectedUrl),
            "The opened page must match the destination captured from the selected result.");
    }
}
