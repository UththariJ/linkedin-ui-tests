package lk.kdu.sqa.pages;

import org.openqa.selenium.*;
import java.util.Locale;
import java.time.Duration;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class LoginPage extends BasePage {
    private static final By USER = By.cssSelector(
        "input#username, input[name='session_key'], input[autocomplete='username'], " +
        "input[type='email'], input[aria-label*='Email'], input[placeholder*='Email']");
    private static final By PASSWORD = By.cssSelector(
        "input#password, input[name='session_password'], input[autocomplete='current-password'], " +
        "input[type='password'], input[aria-label*='Password'], input[placeholder*='Password']");
    private static final By SUBMIT = By.cssSelector("button[type='submit']");
    private static final By PASSWORD_ERROR = By.cssSelector("#error-for-password, [data-test-id='sign-in-error']");
    public LoginPage(WebDriver driver) { super(driver); }
    public LoginPage open() { driver.get("https://www.linkedin.com/login"); visible(USER); return this; }
    public void submit(String username, String password) {
        WebElement user = visible(USER); user.clear(); user.sendKeys(username);
        WebElement pass = visible(PASSWORD); pass.clear(); pass.sendKeys(password);
        click(SUBMIT);
    }
    public void waitForManualLogin() {
        System.out.println("Enter your LinkedIn credentials in the open browser.");
        System.out.println("Complete any CAPTCHA or verification manually. Waiting up to 5 minutes.");
        new WebDriverWait(driver, Duration.ofMinutes(5))
            .until(d -> d.getCurrentUrl().contains("/feed"));
    }
    public boolean invalidPasswordErrorShown() {
        return until(d -> {
            for (WebElement error : d.findElements(PASSWORD_ERROR)) {
                if (!error.isDisplayed()) continue;
                String text = error.getText().toLowerCase(Locale.ROOT);
                if (!text.isBlank() && (text.contains("wrong") || text.contains("incorrect") || text.contains("invalid") || text.contains("try again"))) return true;
            }
            return false;
        });
    }
}
