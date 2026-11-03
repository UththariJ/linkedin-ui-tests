package lk.kdu.sqa.tests;

import lk.kdu.sqa.base.BaseTest;
import lk.kdu.sqa.config.TestConfig;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SearchTest extends BaseTest {
    @Test(description="TC03: Search displays a results URL and at least one visible entity result")
    public void searchShowsResults() {
        Assert.assertTrue(login().search(TestConfig.searchTerm()).isDisplayed());
    }
}
