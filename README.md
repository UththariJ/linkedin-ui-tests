# LinkedIn UI Tests - IT3153 Assignment 04

Minimal Java + Selenium WebDriver + TestNG automation using Page Object Model (POM).
The project is configured for Windows, Java 21.0.7, Maven 3.9.6, and Google Chrome.
Selenium Manager obtains a compatible ChromeDriver automatically.

## Requirements

- JDK 21 (Maven compiler release is `21`)
- Apache Maven 3.9.6
- Google Chrome
- VS Code with the Extension Pack for Java
- Internet access for Maven Central and Selenium Manager
- A LinkedIn account owned by you

Check the installed tools from a new PowerShell terminal:

```powershell
java -version
mvn -version
```

## Open and configure

Open `D:\linkedin-ui-tests` in VS Code. The folder must contain `pom.xml`.
In the PowerShell terminal opened in that folder, set credentials for that session only:

```powershell
$env:LINKEDIN_USERNAME = "your-own-linkedin-email"
$env:LINKEDIN_PASSWORD = Read-Host "LinkedIn password" -AsSecureString |
  ForEach-Object {
    $ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($_)
    try { [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr) }
    finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr) }
  }
```

Do not paste credentials into chat, Java files, `pom.xml`, Git, recordings, or screenshots.
Close the terminal when finished so the environment variables are removed.

## Compile and run

Compile without opening Chrome or logging in:

```powershell
mvn test-compile
```

Run the complete sequential suite:

```powershell
mvn test
```

Run one class or method:

```powershell
mvn test -Dtest=LoginTest
mvn test -Dtest=LoginTest#validLogin
```

For temporary browser inspection only, keep Chrome open after cleanup:

```powershell
mvn test "-Dtest=LoginTest#validLogin" "-DkeepBrowserOpen=true"
```

Close that browser manually. Do not use `keepBrowserOpen` for the final demonstration;
normal runs close every browser in `@AfterMethod`.

If either credential variable is missing, the affected tests are skipped with a clear
configuration message; no empty login is attempted. If LinkedIn displays a CAPTCHA or
security check before the login form, including an image-selection challenge, the test
is marked BLOCKED/SKIPPED rather than trying to solve or bypass it.

## Expected browser behavior

The suite runs exactly four TestNG methods sequentially:

1. Valid login: opens `/login`, signs in, and checks the authenticated feed/search UI.
2. Invalid password: uses your username and one generated wrong password, then checks a
   visible incorrect-password message. A CAPTCHA or checkpoint is not accepted as a pass.
3. Search: logs in, searches for `software testing`, checks the search URL and a visible
   person/company/school/post/job result.
4. Click result: logs in, searches, records the first supported result destination, clicks it,
   handles a new tab if needed, and checks that the opened destination matches the recorded one.

Each test starts a fresh maximized Chrome browser with `@BeforeMethod` and always calls
`driver.quit()` in `@AfterMethod`. Explicit `WebDriverWait` and
`ExpectedConditions`-style polling are used; implicit waits and `Thread.sleep()` are not used.

## Security interruptions

The code does not bypass CAPTCHA, MFA, verification, authwalls, or security checkpoints.
When a challenge is detected, the test is marked **SKIPPED/BLOCKED**, not passed, and later
tests are stopped to avoid repeated login attempts. Record the URL/page, test ID, date,
and blocked status in the report. If manual verification is required, the normal automated
flow did not complete; do not claim a pass.

## Results, screenshots, and locators

Maven/TestNG reports are generated under `target\surefire-reports\`, including
`testng-results.xml`, `emailable-report.html`, and `index.html`. Build output is under
`target\`. A screenshot is automatically captured only when a test fails, under
`screenshots\`, using the test name and timestamp. The folder can remain empty when all
tests pass. Review and redact every screenshot before sharing; masking is best effort.

LinkedIn can change its DOM or require an account-specific flow. If a locator breaks,
open the current page with F12, inspect the element's stable `id`, `name`, role, placeholder,
or accessible label, then update only the relevant page class:

- `LoginPage.java`: username, password, submit, and password-error locators
- `HomePage.java`: authenticated search box
- `SearchResultsPage.java`: supported result links and destination validation

Do not copy passwords, cookies, tokens, or personal data from DevTools.

## Assignment deliverables checklist

- [ ] Complete project source code
- [ ] `README.md`
- [ ] 2-4 page PDF report
- [ ] Screen recording of no more than 3 minutes, with personal information hidden
- [ ] Git repository shared with the instructor

## Report guidance

Leave actual results pending until a real run. Do not invent results or screenshots.
Include test design, locator choices and their stability rationale, explicit waits,
login security handling, dynamic elements/page changes, challenges and solutions, and
the limits of automating a website not controlled by the tester. Include run results and
at least one screenshot captured automatically by the code, after privacy review.

| Test ID | Description | Steps | Expected result | Actual result | Pass/Fail/Blocked |
|---|---|---|---|---|---|
| TC01 | Valid login | Open login; submit own valid credentials | Authenticated feed/search is visible | Pending run | Pending |
| TC02 | Invalid password | Submit own username and one dummy password | Visible incorrect-password error | Pending run | Pending |
| TC03 | Search | Login; search `software testing`; press Enter | Search URL and visible actual result | Pending run | Pending |
| TC04 | Click result | Login; search; capture and click first result | Opened page matches captured destination | Pending run | Pending |

Use a private repository unless the instructor says otherwise. Before pushing, verify that
credentials, personal information, screenshots, recordings, browser profiles, and `target\`
are not included. The `.gitignore` excludes generated output and local credential files.
