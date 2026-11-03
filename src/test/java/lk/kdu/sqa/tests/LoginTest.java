package lk.kdu.sqa.tests;

import java.util.UUID;
import lk.kdu.sqa.base.BaseTest;
import lk.kdu.sqa.config.TestConfig;
import lk.kdu.sqa.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {
    @Test(priority=1, description="TC01: Valid credentials open the feed with a search box")
    public void validLogin() { Assert.assertTrue(login().isLoggedIn()); }
    @Test(priority=2, description="TC02: One invalid password attempt displays a password error")
    public void invalidPassword() {
        String wrong = "Invalid-" + UUID.randomUUID();
        while (wrong.equals(TestConfig.required("LINKEDIN_PASSWORD"))) wrong = "Invalid-" + UUID.randomUUID();
        LoginPage page = new LoginPage(driver).open();
        page.submit(TestConfig.required("LINKEDIN_USERNAME"), wrong);
        Assert.assertTrue(page.invalidPasswordErrorShown(), "A visible incorrect-password error is required; a checkpoint is not a pass.");
    }
}
