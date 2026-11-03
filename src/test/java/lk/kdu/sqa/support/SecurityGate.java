package lk.kdu.sqa.support;

import java.net.URI;
import java.util.Locale;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.SkipException;

public final class SecurityGate {
    private static volatile boolean blocked;
    private SecurityGate() { }
    public static void beforeTest() {
        if (blocked) throw new SkipException("Suite stopped after a LinkedIn security checkpoint. Document the block before another run.");
    }
    public static void check(WebDriver driver) {
        String url = driver.getCurrentUrl().toLowerCase(Locale.ROOT);
        String path = URI.create(url).getPath();
        boolean challenge = path != null && ((path.contains("/checkpoint/") && !path.equals("/checkpoint/lg/login-submit")) || path.contains("/challenge/") || path.contains("/authwall"));
        challenge |= driver.findElements(By.cssSelector("iframe[src*='captcha'], iframe[src*='recaptcha'], input[name='pin'], input[name='verificationCode'], #captcha-internal")).stream().anyMatch(e -> {
            try { return e.isDisplayed(); } catch (org.openqa.selenium.StaleElementReferenceException ex) { return false; }
        });
        if (challenge) {
            blocked = true;
            throw new SkipException("BLOCKED: LinkedIn requested verification. No bypass attempted; remaining tests are skipped.");
        }
    }
}
