# LabCorp QA Automation — Selenium + Cucumber BDD

## Overview

End-to-end Selenium + Cucumber BDD automation solution for the LabCorp Careers portal.  
Covers the full journey: homepage → Careers → job search → job listing → assertions → Apply Now → Return to Search.

---

## Technology Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 11+ | Language |
| Selenium WebDriver | 4.18.x | Browser automation |
| Cucumber | 7.15.x | BDD / Gherkin runner |
| JUnit 4 | 4.13.2 | Test runner |
| WebDriverManager | 5.7.x | Auto-downloads ChromeDriver |
| Chrome | Latest | Test browser |
| Maven | 3.8+ | Build / dependency management |
| SLF4J Simple | 2.0.x | Logging |

---

## Project Structure

```
labcorp-qa-automation/
├── pom.xml
└── src/
    └── test/
        ├── java/
        │   └── com/labcorp/
        │       ├── pages/                   # Page Object Model (POM)
        │       │   ├── BasePage.java        # Shared driver + wait access
        │       │   ├── HomePage.java        # www.labcorp.com
        │       │   ├── CareersPage.java     # Careers landing + search results
        │       │   ├── JobDetailsPage.java  # Individual job listing page
        │       │   └── ApplyPage.java       # Apply Now page
        │       ├── stepDefinitions/
        │       │   └── LabCorpStepDefinitions.java  # Cucumber glue code
        │       ├── runner/
        │       │   └── CucumberTestRunner.java      # JUnit runner
        │       └── utils/
        │           ├── DriverFactory.java   # Chrome WebDriver factory
        │           └── WaitHelper.java      # Explicit wait methods
        └── resources/
            ├── features/
            │   └── LabCorpCareers.feature   # BDD Gherkin scenarios
            └── cucumber.properties
```

---

## Prerequisites

1. **Java 11** (or higher) — `java -version`
2. **Maven 3.8+** — `mvn -version`
3. **Google Chrome** — Latest stable version
4. WebDriverManager handles the ChromeDriver download automatically — no manual setup needed.

---

## How to Run

### From the command line:

```bash
# Clone / unzip the project, then:
cd labcorp-qa-automation

# Run all @LabCorpCareers scenarios
mvn clean test

# Run a specific tag
mvn clean test -Dcucumber.filter.tags="@SmokeTest"

# Run in headless mode (add to ChromeOptions in DriverFactory — uncomment the headless line)
mvn clean test
```

### From an IDE (IntelliJ / Eclipse):

1. Import as a Maven project.
2. Right-click `CucumberTestRunner.java` → **Run As → JUnit Test**.

---

## Locator Strategy Summary

The framework uses **at least 3 different `By` types** per the requirement:

| `By` Type | Where Used | Rationale |
|---|---|---|
| `By.id` | Search input, apply page header | Most specific; fastest lookup |
| `By.name` | Search field fallback | Structural HTML attribute |
| `By.linkText` | Careers nav, Return to Search | Natural for anchor text matching |
| `By.cssSelector` | Job title, location, ID, buttons | Flexible, readable, widely supported |
| `By.xpath` | Complex DOM traversal, text matching | Power tool for tricky layouts |
| `By.tagName` | `h1` fallback, `body` text checks | Broad baseline coverage |
| `By.className` | Requirement list items | Class-based component targeting |

---

## Wait Strategy

All locator resolutions use **explicit waits** (`WebDriverWait` / `ExpectedConditions`):

| Wait Type | Method | Use Case |
|---|---|---|
| `visibilityOfElementLocated` | `waitForVisibility()` | Element must render on screen |
| `elementToBeClickable` | `waitForClickability()` | Element ready for interaction |
| `presenceOfElementLocated` | `waitForPresence()` | Element in DOM (may be hidden) |
| `urlContains` | `waitForUrlContains()` | Navigation confirmation |
| JS `readyState == complete` | `waitForPageLoad()` | Full page load |
| `stalenessOf` | `waitForStaleness()` | Post-navigation stale element cleanup |

> **Thread.sleep() is not used.** The one `hardWait()` method exists only for unavoidable CSS animation settling (e.g., cookie banner close).

---

## Assertions Covered (Step 5)

| # | Assertion | Method |
|---|---|---|
| 5a | Job Title matches expected | `getJobTitle()` + `Assert.assertTrue(contains)` |
| 5b | Job Location contains city | `getJobLocation()` + `Assert.assertTrue(contains)` |
| 5c | Job ID is present and non-empty | `getJobId()` + `Assert.assertFalse(isEmpty)` |
| 5d | Description contains "automation" | `descriptionContains("automation")` |
| 5e | Requirements contain "Selenium" | `descriptionContains("Selenium")` |
| 5f | Page body contains "quality assurance" | `getPageBodyText().contains(...)` |

## Apply Page Cross-Validation (Step 6)

| Check | How |
|---|---|
| Job title on apply page | `getApplyPageJobTitle()` vs expected string |
| Job location on apply page | `getApplyPageJobLocation()` vs captured location |
| Job ID matches listing page | `getApplyPageJobId()` vs `capturedJobId` |
| Page contains position keywords | `applyPageContains()` with title tokens |

---

## Reports

After test run, reports are in `target/cucumber-reports/`:

- `cucumber.html` — Human-readable HTML report
- `cucumber.json` — JSON for CI dashboards (Allure, Extent)
- `cucumber.xml` — JUnit XML for Jenkins

---

## Configuration Notes

### Running headless (CI/CD)

Uncomment in `DriverFactory.java`:
```java
options.addArguments("--headless=new");
```

### Adjusting the job title / location

Edit `LabCorpCareers.feature`:
```gherkin
When I search for the position "Your Job Title Here"
...
Then the job title should be "Your Job Title Here"
And the job location should contain "Your City"
```

### Adjusting locators

If LabCorp's site markup changes, update the `By.*` constants in the relevant Page Object class. All locators are centrally defined at the top of each class with comments explaining the strategy.

---

## Troubleshooting

| Issue | Fix |
|---|---|
| `SessionNotCreatedException` | Chrome version mismatch — WebDriverManager should auto-fix; ensure Chrome is updated |
| Element not found | Site DOM may have changed; inspect with browser DevTools and update the `By.*` constant |
| Timeout on page load | Increase `DEFAULT_TIMEOUT_SECONDS` in `WaitHelper` |
| Cookie banner blocks click | `dismissCookieBannerIfPresent()` handles it; update the selector if the banner id changes |
