package base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;
import java.util.UUID;

import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.ITestResult;

public abstract class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    private String blockedReason;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        blockedReason = null;
        driver = null;
        if (environment("LINKEDIN_USERNAME") == null || environment("LINKEDIN_PASSWORD") == null) {
            throw new SkipException(
                "SKIPPED: Set LINKEDIN_USERNAME and LINKEDIN_PASSWORD in this PowerShell session " +
                "before starting Maven.");
        }
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        options.addArguments("--incognito", "--start-maximized", "--lang=en-US");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        if (environment("LINKEDIN_USERNAME") == null || environment("LINKEDIN_PASSWORD") == null) {
            blockedReason = "SKIPPED: LINKEDIN_USERNAME and LINKEDIN_PASSWORD are not configured.";
            return;
        }
        driver.get("https://www.linkedin.com/login");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(d ->
                hasSecurityInterruption() || d.findElements(By.cssSelector(
                    "input#username, input[name='session_key'], input[autocomplete='username'], " +
                    "input[type='email'], input[placeholder*='Email'], input[aria-label*='Email'], " +
                    "input[type='text']")).stream().anyMatch(WebElement::isDisplayed));
            if (hasSecurityInterruption()) {
                blockedReason = "BLOCKED: LinkedIn requested CAPTCHA, verification, or a security checkpoint.";
            }
        } catch (org.openqa.selenium.TimeoutException timeout) {
            blockedReason = "BLOCKED: LinkedIn login form did not become available.";
        }
    }

    protected String environment(String name) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? null : value;
    }

    protected void rejectSecurityInterruption() {
        if (hasSecurityInterruption()) {
            throw new SkipException(
                "BLOCKED: LinkedIn requested CAPTCHA, verification, or a security checkpoint. " +
                "No bypass or retry was attempted.");
        }
    }

    protected void requireReady() {
        if (blockedReason != null) {
            throw new SkipException(blockedReason + " No bypass or retry was attempted.");
        }
    }

    private boolean hasSecurityInterruption() {
        String url = driver.getCurrentUrl().toLowerCase(Locale.ROOT);
        boolean challengeUrl =
            url.contains("/checkpoint/challenge")
            || url.contains("/checkpoint/verify")
            || url.contains("/checkpoint/security")
            || url.contains("/checkpoint/uas/")
            || url.contains("/challenge/")
            || url.contains("/authwall");
        boolean challengeElement = driver.findElements(By.cssSelector(
            "iframe[src*='captcha'], iframe[src*='recaptcha'], iframe[title*='challenge'], " +
            "input[name='pin'], input[name='verificationCode'], #captcha-internal")).stream()
            .anyMatch(element -> {
                try {
                    return element.isDisplayed();
                } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
                    return false;
                }
            });
        boolean visibleChallengeText = driver.findElements(By.xpath(
            "//*[self::h1 or self::h2 or self::p or @role='heading'][" +
            "contains(translate(normalize-space(.), " +
            "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'security check')" +
            " or contains(translate(normalize-space(.), " +
            "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), \"i'm not a robot\")" +
            " or contains(translate(normalize-space(.), " +
            "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'select all images')" +
            "]")).stream()
            .anyMatch(element -> {
                try {
                    return element.isDisplayed() && !element.getText().isBlank();
                } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
                    return false;
                }
            });
        String pageText = driver.findElements(By.tagName("body")).stream()
            .filter(WebElement::isDisplayed)
            .map(WebElement::getText)
            .map(String::toLowerCase)
            .findFirst()
            .orElse("");
        boolean challengeText = visibleChallengeText || pageText.contains("security check")
            || pageText.contains("quick security")
            || pageText.contains("i'm not a robot")
            || pageText.contains("select all images")
            || pageText.contains("verification required");
        return challengeUrl || challengeElement || challengeText;
    }

    protected void login() {
        requireReady();
        new pages.LoginPage(driver, wait)
            .submit(environment("LINKEDIN_USERNAME"), environment("LINKEDIN_PASSWORD"));
        wait.until(d -> {
            rejectSecurityInterruption();
            return d.getCurrentUrl().contains("/feed")
                || !d.findElements(By.cssSelector(
                    "input[placeholder*='Search'], input[aria-label*='Search']")).isEmpty();
        });
    }

    protected String dummyPassword() {
        return "Invalid-" + UUID.randomUUID();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            if (driver != null
                && (result.getStatus() == ITestResult.FAILURE
                || result.getStatus() == ITestResult.SKIP)) {
                try {
                    Path directory = Path.of("screenshots");
                    Files.createDirectories(directory);
                    ((JavascriptExecutor) driver).executeScript(
                        "document.querySelectorAll('input,textarea,[contenteditable=true]').forEach(e => {" +
                        "e.value=''; e.textContent=''; e.style.visibility='hidden';});");
                    Path file = directory.resolve(result.getMethod().getMethodName()
                        + "_" + System.currentTimeMillis() + ".png");
                    FileHandler.copy(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE), file.toFile());
                    System.out.println(file);
                } catch (IOException | WebDriverException screenshotFailure) {
                    System.err.println("Screenshot capture failed; test failure is unchanged.");
                }
            }
        } finally {
            if (driver != null) {
                try {
                    if (Boolean.getBoolean("pauseBeforeClose")) {
                        System.out.println(
                            "Browser paused for inspection. Press ENTER here to close it.");
                        new java.util.Scanner(System.in).nextLine();
                    }
                    driver.quit();
                } catch (WebDriverException cleanupFailure) {
                    System.err.println("Browser cleanup failed: " + cleanupFailure.getMessage());
                } finally {
                    driver = null;
                }
            }
        }
    }
}
