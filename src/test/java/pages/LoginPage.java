package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;

public final class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final By USERNAME = By.cssSelector(
        "input#username, input[name='session_key'], input[autocomplete='username'], input[type='email'], " +
        "input[placeholder*='Email'], input[aria-label*='Email'], input[type='text']");
    private static final By PASSWORD = By.cssSelector(
        "input#password, input[name='session_password'], input[autocomplete='current-password'], input[type='password']");
    private static final By SIGN_IN = By.xpath("//button[@type='submit' or normalize-space()='Sign in']");
    private static final By LOGIN_ERROR = By.cssSelector(
        "#error-for-password, [role='alert'], [aria-live='assertive'], .artdeco-inline-feedback");

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public LoginPage open() {
        driver.get("https://www.linkedin.com/login");
        wait.until(d -> {
            if (isSecurityChallenge()) {
                throw new SkipException(
                    "BLOCKED: LinkedIn displayed a CAPTCHA or security check before login. " +
                    "Do not bypass it; document the test as blocked.");
            }
            return d.findElements(USERNAME).stream().anyMatch(WebElement::isDisplayed);
        });
        return this;
    }

    private boolean isSecurityChallenge() {
        boolean challengeFrame = driver.findElements(By.cssSelector(
            "iframe[src*='captcha'], iframe[src*='recaptcha'], iframe[title*='reCAPTCHA'], " +
            "iframe[title*='challenge'], #captcha-internal")).stream()
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
        String pageText = driver.findElements(By.cssSelector("body, h1, h2, [role='heading']")).stream()
            .filter(WebElement::isDisplayed)
            .map(WebElement::getText)
            .map(String::toLowerCase)
            .reduce("", (all, text) -> all + " " + text);
        boolean challengeText = visibleChallengeText || pageText.contains("security check")
            || pageText.contains("quick security")
            || pageText.contains("i'm not a robot")
            || pageText.contains("select all images")
            || pageText.contains("verification required");
        return challengeFrame || challengeText;
    }

    private WebElement readyElement(By locator, boolean clickable) {
        return wait.until(d -> {
            if (isSecurityChallenge()) {
                throw new SkipException(
                    "BLOCKED: LinkedIn displayed security verification. " +
                    "The automation did not bypass it.");
            }
            return clickable
                ? ExpectedConditions.elementToBeClickable(locator).apply(d)
                : ExpectedConditions.visibilityOfElementLocated(locator).apply(d);
        });
    }

    public LoginPage submit(String username, String password) {
        WebElement user = readyElement(USERNAME, false);
        user.clear();
        user.sendKeys(username);
        WebElement pass = readyElement(PASSWORD, false);
        pass.clear();
        pass.sendKeys(password);
        readyElement(SIGN_IN, true).click();
        return this;
    }

    public boolean hasInvalidPasswordError() {
        return wait.until(d -> d.findElements(LOGIN_ERROR).stream()
            .filter(WebElement::isDisplayed)
            .map(WebElement::getText)
            .map(String::toLowerCase)
            .anyMatch(text -> text.contains("wrong") || text.contains("incorrect")
                || text.contains("invalid") || text.contains("try again")
                || text.contains("couldn't")));
    }
}
