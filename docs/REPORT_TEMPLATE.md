# IT3153 — Assignment 04: LinkedIn UI Automation

Student name: [fill in]   Student ID: [fill in]   Date: [fill in]
Repository: [private link; confirm instructor access]

DRAFT TEMPLATE — replace every bracket with your own observations. Do not submit invented results. Aim for 2–4 pages after adding evidence.

## 1. Design and environment

Java [version], Maven [version], Selenium 4.52.0, TestNG 7.11.0, Chrome [version], Windows [version], VS Code.
The suite uses Page Object Model. Each test opens a fresh Chrome session and closes it afterwards. Explicit waits check visible elements and expected navigation. Tests run sequentially.

## 2. Test cases and actual results

| ID | Description | Steps | Expected result | Actual result | Pass/Fail/Blocked |
|---|---|---|---|---|---|
| TC01 | Valid login | Open login; enter own valid credentials; submit | Feed URL and global search visible | [observation] | [status] |
| TC02 | Invalid password | Open login; own username; one wrong password; submit | Visible incorrect-password error | [observation] | [status] |
| TC03 | Search | Login; type software testing; Enter | Results URL and visible entity link | [observation] | [status] |
| TC04 | Open first result | Login; search; capture first entity link; click | Opened path matches captured destination | [observation] | [status] |

## 3. Evidence

Insert a redacted test-run report screenshot showing test counts and at least one image captured automatically by Screenshots.java. Include captions: test ID, date, actual status and automatic/manual capture. The automated masking hides inputs/images/emails but requires manual privacy review. If no test-body screenshot was possible, state why and collect it in a later permitted run; do not manufacture evidence.

## 4. Locators and waits

Summarize the actual locators used in the page classes: IDs/names for credentials; submit button type; visible error control; global search class/accessibility fallback; entity result links scoped to main.
Explain why these were chosen over positional XPath. Describe WebDriverWait, the 25-second maximum, stale-element re-finding and why Object.wait() is unsuitable for page synchronization. Record any locator changes you made.

## 5. Challenges and login security

[Describe only encountered challenges: dynamic DOM, stale elements, Chrome/CDP warnings, checkpoint, CAPTCHA or MFA. Explain actual responses and remaining limits.]
Credentials were provided through hidden prompting and environment variables, excluded from source and logs. A fresh browser was used for each test. Verification was not bypassed. [Record whether the circuit breaker skipped any tests.]

## 6. Reflection

Suggested starting point — adapt to your experience:
UI automation against an external site depends on page structures and policies that I cannot control. Even correct test code can fail after layout changes, slow loading or security checks. A checkpoint shows that the flow was blocked; it does not prove that LinkedIn login is defective. I should use stable locators, meaningful assertions and bounded waits, protect credentials, report limitations honestly and avoid repeated automated access when the site requests verification.
