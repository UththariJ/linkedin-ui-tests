package lk.kdu.sqa.tests;

import java.net.URI;
import lk.kdu.sqa.base.BaseTest;
import lk.kdu.sqa.config.TestConfig;
import lk.kdu.sqa.pages.SearchResultsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SearchResultClickTest extends BaseTest {
    @Test(description="TC04: First entity result opens its captured destination path")
    public void firstResultOpens() {
        SearchResultsPage results = login().search(TestConfig.searchTerm());
        Assert.assertTrue(results.isDisplayed());
        String expectedPath = results.openFirstResult();
        Assert.assertEquals(SearchResultsPage.normalize(URI.create(driver.getCurrentUrl()).getPath()), expectedPath,
            "Opened destination must match the actual link clicked.");
    }
}
