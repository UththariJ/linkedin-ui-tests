# LinkedIn UI Tests — IT3153 Assignment 04

Java + Maven + Selenium + TestNG. Uses Page Object Model (POM).
Built for Java 17 or later; Java 21.0.7 and Maven 3.9.6 are suitable.
Selenium 4.52.0 and TestNG 7.11.0 are pinned in pom.xml.

## 1. Install/check software

On Windows, install VS Code, Google Chrome, a JDK (not just a JRE), Maven, and Git.
If your existing versions work, do not reinstall them.
In VS Code press Ctrl+Shift+X and install **Extension Pack for Java**, publisher Microsoft.
It includes Java language support, debugger, Test Runner for Java, Maven for Java and Project Manager for Java.
No Selenium IDE, manual ChromeDriver installation, Python, Node.js or separate TestNG extension is needed.
Selenium Manager downloads a compatible driver on first use; internet access is required.

Open a NEW PowerShell terminal and run:

```powershell
java -version
javac -version
mvn -version
git --version
```

`javac` confirms the compiler exists. Maven should report Java 21.0.7 (or another JDK 17+).
If commands are not found, add the JDK bin and Maven bin folders to Windows PATH and restart VS Code.
JAVA_HOME should refer to the JDK folder, without `\bin` on the end.
In VS Code use Ctrl+Shift+P → Java: Configure Java Runtime if Java projects show an incorrect runtime.

## 2. Extract and open the project

Extract the ZIP using Windows **Extract All**. Move the enclosed `linkedin-ui-tests` folder to `D:\linkedin-ui-tests` (or another folder).
Do not run from inside the ZIP. VS Code → File → Open Folder → select `D:\linkedin-ui-tests`.
Only trust the workspace after you have reviewed its contents.
Wait for Java/Maven importing to finish. Select Terminal → New Terminal (PowerShell).
Check `pom.xml` is directly inside the opened folder.

```powershell
cd D:\linkedin-ui-tests
mvn -DskipTests test-compile
```

This downloads libraries and compiles the code without logging into LinkedIn.
The compile command should end with BUILD SUCCESS. It is not evidence that UI tests passed.

## 3. Credentials — never place them in Java or pom.xml

Only use your own account, preferably a dedicated test account. Do not send credentials to anyone.
The provided PowerShell script asks for your email and a hidden password, passes them to Maven as process environment variables, and removes those variables afterwards.
Credentials are never written by this project to a configuration file. The password must briefly exist in process memory; this is not a secret vault.
Do not run on a shared/untrusted machine. Do not pass a password with `-Dpassword=...`, which can expose it in command history/process listings.
A `.env` file is NOT read by this project.

## 4. Run the positive login first

```powershell
.\scripts\run-tests.ps1 -Test 'LoginTest#validLogin'
```

Type your email when prompted, then type your password at the hidden prompt.
Chrome opens a fresh incognito session. The browser closes when the test finishes; this is expected.
The test waits for both the feed URL and visible global search input.
If PowerShell blocks scripts, use this command for this invocation only:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\run-tests.ps1 -Test 'LoginTest#validLogin'
```

Or use the command-file launcher, which applies the bypass only to that child PowerShell process:

```powershell
.\scripts\run-tests.cmd "LoginTest#validLogin"
```

Read the script first. This does not permanently change the machine's execution policy. If your organization blocks scripts, follow its policy; do not override managed controls.

## 5. Run the other flows individually

```powershell
.\scripts\run-tests.ps1 -Test 'SearchTest'
.\scripts\run-tests.ps1 -Test 'SearchResultClickTest'
.\scripts\run-tests.ps1 -Test 'LoginTest#invalidPassword'
```

If unsigned scripts are blocked, use the launcher:

```powershell
.\scripts\run-tests.cmd SearchTest
.\scripts\run-tests.cmd SearchResultClickTest
.\scripts\run-tests.cmd "LoginTest#invalidPassword"
```

Run each command separately, checking its results before continuing.
The negative test makes ONE invalid-password submission using your own username and a randomly generated wrong password. Do not repeatedly run it; invalid logins can trigger restrictions.
For simpler selectors and password-error matching, set LinkedIn's interface language to English. Chrome's language setting alone may not override your account language.
The default search is `software testing`; edit the search term in the script if necessary.

Once the individual flows are working, you can run all four tests in one sequential suite:

```powershell
.\scripts\run-tests.ps1
```

Running all tests repeats the invalid attempt; do it only when needed for your evidence.
Each test gets a fresh browser. Each search test logs in independently, so it does not depend on another test's session.
If environment variables are already configured securely in your terminal, `mvn test` also works directly.

## 6. Security checkpoints, CAPTCHA and MFA

The suite detects common LinkedIn checkpoint/authwall URLs and visible CAPTCHA/verification controls, marks the affected test SKIPPED and prevents later tests from starting in that Maven run.
A skipped test is BLOCKED evidence, not a pass. TestNG/Maven can report BUILD SUCCESS with skipped tests: always check the counts in the report.
There is no CAPTCHA bypass, stealth mode, proxy rotation, automated MFA or login retry loop.
Detection is best-effort because the site can change. If an unrecognized verification page appears, stop the run (Ctrl+C), document it and close its browser.
This implementation stops and closes Chrome rather than pausing for manual verification inside the test. If necessary, complete verification manually in your normal browser, then decide whether one fresh run is appropriate. A fresh incognito session may ask again.
Never disable MFA or reduce account security for the assignment. If blocked, discuss an approved alternative with the instructor.

## 7. Results and automatic screenshots

After running, open `target/surefire-reports/`.
Look for TestNG's `index.html` or `emailable-report.html`, `testng-results.xml`, Maven XML and text reports.
From PowerShell, if present:

```powershell
Start-Process .\target\surefire-reports\index.html
```

The code automatically captures PNG screenshots of passed and failed test bodies under `screenshots/`, before browser cleanup. If WebDriver itself cannot start, no screenshot is possible.
It hides input fields, common account navigation, images and visible email text first. This alters the evidence view after assertions; record that masking was applied.
Masking is best-effort, NOT guaranteed anonymization: names, posts, URLs, background images, unusual widgets or report exception text can still reveal personal information.
REVIEW every screenshot/report and blur/remove personal information before sharing. Share edited copies; keep originals private.
Skipped checkpoint tests do not automatically capture a potentially sensitive challenge screen. Document the blocker in text, and only capture a manual image if safe and needed.
The Git ignore file excludes screenshots, recordings, Maven output, secrets and local IDE settings. Git ignore is not encryption and cannot remove files already committed.

## 8. Understand the code

- `config/TestConfig.java`: reads credentials from environment; fails clearly if missing.
- `base/BaseTest.java`: TestNG @BeforeMethod opens Chrome; @AfterMethod captures evidence and quits even after failures.
- `pages/BasePage.java`: explicit WebDriverWait with a 25-second limit; stale elements are re-found while polling.
- `pages/LoginPage.java`: username/password/sign-in locators and negative login error check.
- `pages/HomePage.java`: checks login, types search, presses Enter.
- `pages/SearchResultsPage.java`: waits for results URL plus a visible entity result; clicks first supported entity result and compares its captured destination path, handling a new tab too.
- `support/SecurityGate.java`: stops on common security challenges.
- `support/Screenshots.java`: best-effort masking and local screenshot capture.
- `tests/`: four test cases in three classes.
- `testng.xml`: sequential suite (no parallel login traffic).

POM means **Page Object Model** here: a class groups a page's locators and actions. `pom.xml` separately means Maven's **Project Object Model**.
`WebDriverWait` waits until a condition is true, up to a limit. Java `Object.wait()` is for thread synchronization, not waiting for web elements. No Thread.sleep() is needed here. Implicit waits are zero to avoid mixing wait strategies.

## 9. Locator choices and maintenance

| Page/action | Locator strategy | Reason |
|---|---|---|
| Username | input#username or input[name=session_key] | Stable form ID/name; limited fallback |
| Password | input#password or input[name=session_password] | Stable form ID/name |
| Sign in | button[type=submit] on login page | Semantic form control |
| Invalid password | #error-for-password or data-test-id sign-in error | Error-specific, visible, nonempty rejection text; never treats CAPTCHA as an invalid-password pass |
| Search | search-global-typeahead class, placeholder/aria-label fallback | Visible global control; accessible attributes as fallback |
| Result | Entity title/result-container links within main | Avoids unrelated header/sidebar links |
| Destination | Captured HTTPS LinkedIn entity link path | Checks the actual result clicked, rather than any profile URL |

The click test opens the first visible supported entity result (person/company/school/post), not a category/filter button. A no-results page intentionally fails because the assignment requires a visible result.
LinkedIn's DOM is not guaranteed. If a locator stops working, inspect the live element with F12 and update only its page class. Log the change in your report.
For example, in the DevTools Console inspect the count with `document.querySelectorAll('input#username').length`.
Only inspect your own pages; do not copy passwords, session cookies or tokens from DevTools.

## 10. Troubleshooting

| Symptom | What to do |
|---|---|
| mvn/java/javac not recognized | Fix PATH/JAVA_HOME, restart terminal; ensure a full JDK |
| Missing environment variable | Use the provided script in the same terminal/session |
| Dependency download error | Check network/proxy and Maven Central access; do not disable TLS checks |
| Browser/driver startup error | Update normal Chrome, check Selenium Manager network access and ChromeDriver conflicts on PATH |
| NoSuchElement / timeout | Check current page, possible login failure/checkpoint, interface language and live locator |
| StaleElementReference | Current waits re-find elements; inspect the page object if the site changed |
| CDP warning | This suite uses normal WebDriver, not DevTools APIs. Check the actual test exception/report; a warning alone does not prove why login failed. Keep Selenium/browser compatible; don't add random devtools artifact versions |
| Window closes quickly | Expected @AfterMethod cleanup; read reports and automatic screenshots |
| BUILD SUCCESS with skipped tests | A checkpoint can skip tests; read status counts, never claim all passed |
| Account restriction/CAPTCHA | Stop, document, avoid repeated login attempts; contact instructor if necessary |

## 11. Git repository

Use a PRIVATE repository for coursework unless your instructor asks for public. Invite the instructor as required.
Install Git, create an empty private GitHub repository (without README/gitignore because these exist locally), then run from the project root:

```powershell
git init
git add .
git status
git diff --cached
```

Check the staged files contain NO password, email, session data, screenshots, recordings or target folder. Then:

```powershell
git commit -m "Add Selenium TestNG assignment project"
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/linkedin-ui-tests.git
git push -u origin main
```

Replace YOUR-USERNAME with yours. Authenticate through Git's normal credential manager; do not embed access tokens in the remote URL.
This is called **pushing** your commits. Confirm the repository contents and instructor access afterwards.
If a secret was ever committed, removing it in a new commit is insufficient: change/revoke it immediately and clean Git history before sharing.

## 12. Finish the assignment

1. Run the real tests and record true Pass/Fail/Blocked outcomes.
2. Fill in `docs/REPORT_TEMPLATE.md` using your actual evidence. It is a draft scaffold, not a finished report.
3. Make a 2–4 page report in Word using that template, insert redacted screenshots, export to PDF.
4. Make a recording of at most 3 minutes: start AFTER the credential prompts, show Chrome running and the result counts, hide email/profile/name/browser account details. Use Windows Snipping Tool recording if available, or Xbox Game Bar (Win+G).
5. Submit complete source, README, report PDF, recording and repository link/access. Never submit credentials or browser profiles.

No live LinkedIn results are supplied with this starter. Account access, site locators and security behavior must be checked on your computer.

## References

- https://www.selenium.dev/documentation/webdriver/getting_started/
- https://www.selenium.dev/documentation/webdriver/waits/
- https://www.selenium.dev/documentation/selenium_manager/
- https://www.selenium.dev/downloads/
- https://testng.org/
- https://testng.org/maven
