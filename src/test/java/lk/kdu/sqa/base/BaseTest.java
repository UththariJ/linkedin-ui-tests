package lk.kdu.sqa.base;

import java.time.Duration;
import lk.kdu.sqa.config.TestConfig;
import lk.kdu.sqa.pages.*;
import lk.kdu.sqa.support.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriverException;
import org.testng.*;
import org.testng.annotations.*;

public abstract class BaseTest {
    protected WebDriver driver;
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        SecurityGate.beforeTest();
        if (!TestConfig.hasCredentials()) {
            boolean manualLogin = Boolean.parseBoolean(System.getProperty("manualLogin", "false"));
            if (!manualLogin) {
                throw new IllegalStateException(
                        "LinkedIn credentials are missing. Run .\\scripts\\run-tests.ps1 " +
                        "or set LINKEDIN_USERNAME and LINKEDIN_PASSWORD in this PowerShell session.");
            }
        }
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito", "--window-size=1366,900", "--lang=en-US");
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.setExperimentalOption("prefs", java.util.Map.of("credentials_enable_service", false, "profile.password_manager_enabled", false));
        if (Boolean.parseBoolean(System.getProperty("keepBrowserOpen", "false"))) {
            options.setExperimentalOption("detach", true);
        }
        driver = new ChromeDriver(options); // Selenium Manager resolves ChromeDriver.
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
    }
    protected HomePage login() {
        LoginPage page = new LoginPage(driver).open();
        if (Boolean.parseBoolean(System.getProperty("manualLogin", "false"))) {
            page.waitForManualLogin();
        } else {
            page.submit(TestConfig.required("LINKEDIN_USERNAME"), TestConfig.required("LINKEDIN_PASSWORD"));
        }
        HomePage home = new HomePage(driver);
        Assert.assertTrue(home.isLoggedIn(), "Feed and search box must be visible after login.");
        return home;
    }
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (driver == null) return;
        try {
            if (result.getStatus() == ITestResult.FAILURE || result.getStatus() == ITestResult.SUCCESS) {
                Screenshots.capture(driver, result.getMethod().getMethodName(), result.isSuccess() ? "PASS" : "FAIL");
            }
        } finally {
            boolean keepBrowserOpen = Boolean.parseBoolean(
                    System.getProperty("keepBrowserOpen", "false"));
            if (!keepBrowserOpen) {
                try {
                    driver.quit();
                } catch (WebDriverException e) {
                    System.err.println("Browser session already ended before cleanup: " + e.getMessage());
                }
            }
            driver = null;
        }
    }
}
