package tests;

import base.BaseTest;
import pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class LoginTest extends BaseTest {
    @Test(priority = 1, description = "TC01: valid login reaches authenticated UI")
    public void validLogin() {
        login();
        Assert.assertTrue(new pages.HomePage(driver, wait).isAuthenticated(),
            "Authenticated feed or global search must be visible after login.");
    }

    @Test(priority = 2, description = "TC02: invalid password shows a password error")
    public void invalidPassword() {
        requireReady();
        LoginPage page = new LoginPage(driver, wait).open();
        page.submit(environment("LINKEDIN_USERNAME"), dummyPassword());
        rejectSecurityInterruption();
        Assert.assertTrue(page.hasInvalidPasswordError(),
            "A visible incorrect-password error is required; a checkpoint is not a pass.");
    }
}
