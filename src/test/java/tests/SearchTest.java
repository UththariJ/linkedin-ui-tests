package tests;

import base.BaseTest;
import pages.HomePage;
import pages.SearchResultsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class SearchTest extends BaseTest {
    @Test(priority = 3, description = "TC03: search shows URL and an actual result")
    public void searchShowsResults() {
        login();
        SearchResultsPage results = new HomePage(driver, wait).search("software testing");
        Assert.assertTrue(results.hasVisibleResult(),
            "At least one visible person, company, school, post, or job result is required.");
        Assert.assertTrue(driver.getCurrentUrl().contains("/search/results/"),
            "The LinkedIn search results URL must be displayed.");
    }
}
