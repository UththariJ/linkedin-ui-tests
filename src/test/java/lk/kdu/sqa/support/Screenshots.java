package lk.kdu.sqa.support;

import java.nio.file.*;
import org.openqa.selenium.*;

public final class Screenshots {
    private Screenshots() { }
    public static void capture(WebDriver driver, String testName, String status) {
        // Best-effort masking only. Review every image before sharing.
        try {
            ((JavascriptExecutor) driver).executeScript("""
                document.querySelectorAll('input, textarea, [contenteditable="true"]').forEach(e => { e.value=''; e.textContent=''; e.style.visibility='hidden'; });
                document.querySelectorAll('img, svg, iframe, video, .global-nav__me, .feed-identity-module').forEach(e => e.style.visibility='hidden');
                const w = document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT);
                const nodes=[]; while(w.nextNode()) nodes.push(w.currentNode);
                nodes.forEach(n => { n.textContent=n.textContent.replace(/[A-Z0-9._%+-]+@[A-Z0-9.-]+[.][A-Z]{2,}/gi,'[email hidden]'); });
                """);
            Path dir = Path.of("screenshots"); Files.createDirectories(dir);
            Path target = dir.resolve(testName + "_" + status + "_" + System.currentTimeMillis() + ".png");
            Files.write(target, ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
            System.out.println("Automatic screenshot saved locally. Review and redact before sharing.");
        } catch (Exception e) { System.err.println("Could not capture screenshot; test status is unchanged."); }
    }
}
