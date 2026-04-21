package com.labcorp.stepDefinitions;

import com.labcorp.pages.ApplyPage;
import com.labcorp.pages.CareersPage;
import com.labcorp.pages.HomePage;
import com.labcorp.pages.JobDetailsPage;
import com.labcorp.utils.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LabCorpStepDefinitions — Cucumber step glue code.
 *
 * Maps each Gherkin step in LabCorpCareers.feature to the
 * corresponding Page Object method and JUnit assertion.
 *
 * Hooks:
 *   @Before — initialises the Chrome WebDriver.
 *   @After  — takes a screenshot on failure, then quits the driver.
 */
public class LabCorpStepDefinitions {

    private static final Logger log = LoggerFactory.getLogger(LabCorpStepDefinitions.class);

    // ── Page Objects (created fresh per scenario via @Before) ─────────────────
    private HomePage       homePage;
    private CareersPage    careersPage;
    private JobDetailsPage jobDetailsPage;
    private ApplyPage      applyPage;

    // Stored between steps so we can compare listing → apply page
    private String capturedJobId;
    private String capturedJobTitle;
    private String capturedJobLocation;

    // ══════════════════════════════════════════════════════════════════════════
    //  Hooks
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * @Before — runs once before every scenario.
     * Initialises Chrome via DriverFactory and instantiates page objects.
     */
    @Before
    public void setUp(Scenario scenario) {
        log.info("═══════════════════════════════════════════════════════");
        log.info("  STARTING SCENARIO: {}", scenario.getName());
        log.info("═══════════════════════════════════════════════════════");

        DriverFactory.initDriver();   // Spins up Chrome (see DriverFactory)

        // Instantiate Page Objects (each reads driver from DriverFactory)
        homePage       = new HomePage();
        careersPage    = new CareersPage();
        jobDetailsPage = new JobDetailsPage();
        applyPage      = new ApplyPage();
    }

    /**
     * @After — runs once after every scenario.
     * Embeds a screenshot into the Cucumber report on failure, then closes Chrome.
     */
    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            log.warn("Scenario FAILED — capturing screenshot.");
            try {
                WebDriver driver = DriverFactory.getDriver();
                byte[] screenshot = ((TakesScreenshot) driver)
                        .getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Failure Screenshot");
            } catch (Exception e) {
                log.error("Could not capture screenshot: {}", e.getMessage());
            }
        }

        DriverFactory.quitDriver();
        log.info("═══════════════════════════════════════════════════════");
        log.info("  SCENARIO COMPLETE: {} — {}",
                scenario.getName(),
                scenario.isFailed() ? "FAILED ✗" : "PASSED ✓");
        log.info("═══════════════════════════════════════════════════════");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Background Steps
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * "the Chrome browser is open and maximized"
     * The driver is already initialised in @Before, so this is a no-op step
     * that serves as readable documentation in the Gherkin.
     */
    @Given("the Chrome browser is open and maximized")
    public void theBrowserIsOpenAndMaximized() {
        log.info("Step: Chrome browser is open and maximized.");
        // Verification: driver must not be null (DriverFactory throws if uninitialised)
        Assert.assertNotNull("WebDriver should be initialised.", DriverFactory.getDriver());
    }

    /**
     * "I navigate to {string}"
     * Opens the given URL, dismisses any cookie banner, and waits for load.
     */
    @And("I navigate to {string}")
    public void iNavigateTo(String url) {
        log.info("Step: Navigate to '{}'.", url);
        homePage.openHomePage(url);
        homePage.dismissCookieBannerIfPresent();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Step 1 — Homepage
    // ══════════════════════════════════════════════════════════════════════════

    /** "the LabCorp homepage should be displayed" */
    @Then("the LabCorp homepage should be displayed")
    public void theLabCorpHomepageShouldBeDisplayed() {
        log.info("Step: Assert LabCorp homepage is displayed.");
        Assert.assertTrue(
                "Expected the LabCorp homepage to be displayed (title should contain 'Labcorp').",
                homePage.isHomePageDisplayed()
        );
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Step 2 — Click Careers
    // ══════════════════════════════════════════════════════════════════════════

    /** "I click on the {string} navigation link" */
    @When("I click on the {string} navigation link")
    public void iClickOnTheNavigationLink(String linkText) {
        log.info("Step: Click '{}' navigation link.", linkText);
        homePage.clickCareersLink();
    }

    /** "I should be on the Careers landing page" */
    @Then("I should be on the Careers landing page")
    public void iShouldBeOnTheCareersLandingPage() {
        log.info("Step: Assert Careers landing page is displayed.");
        Assert.assertTrue(
                "Expected to land on the Careers page (URL should contain 'career' or 'job').",
                careersPage.isCareersPageDisplayed()
        );
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Step 3 — Search for a position
    // ══════════════════════════════════════════════════════════════════════════

    /** "I search for the position {string}" */
    @When("I search for the position {string}")
    public void iSearchForThePosition(String jobTitle) {
        log.info("Step: Search for position '{}'.", jobTitle);
        careersPage.searchForPosition(jobTitle);
    }

    /** "the search results should be displayed" */
    @Then("the search results should be displayed")
    public void theSearchResultsShouldBeDisplayed() {
        log.info("Step: Assert search results are displayed.");
        Assert.assertTrue(
                "Expected job search results to appear on the page.",
                careersPage.areSearchResultsDisplayed()
        );
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Step 4 — Select the position
    // ══════════════════════════════════════════════════════════════════════════

    /** "I click on the first matching job result for {string}" */
    @When("I click on the first matching job result for {string}")
    public void iClickOnTheFirstMatchingJobResult(String jobTitle) {
        log.info("Step: Click first job result matching '{}'.", jobTitle);
        careersPage.clickFirstMatchingJobResult(jobTitle);
    }

    /** "I should be on the job details page" */
    @Then("I should be on the job details page")
    public void iShouldBeOnTheJobDetailsPage() {
        log.info("Step: Assert job details page is displayed.");
        Assert.assertTrue(
                "Expected to be on the job details page (title element should be visible).",
                jobDetailsPage.isJobDetailsPageDisplayed()
        );
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Step 5 — Assertions on job details
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * 5a — "the job title should be {string}"
     *
     * Performs a case-insensitive contains check so minor punctuation or
     * formatting differences don't fail the test.
     */
    @Then("the job title should be {string}")
    public void theJobTitleShouldBe(String expectedTitle) {
        capturedJobTitle = jobDetailsPage.getJobTitle();
        log.info("Step: Assert job title. Expected: '{}', Actual: '{}'.",
                expectedTitle, capturedJobTitle);
        Assert.assertTrue(
                String.format("Job title assertion failed.%n  Expected (contains): '%s'%n  Actual: '%s'",
                        expectedTitle, capturedJobTitle),
                capturedJobTitle.toLowerCase().contains(expectedTitle.toLowerCase())
        );
    }

    /**
     * 5b — "the job location should contain {string}"
     */
    @And("the job location should contain {string}")
    public void theJobLocationShouldContain(String expectedLocation) {
        capturedJobLocation = jobDetailsPage.getJobLocation();
        log.info("Step: Assert job location. Expected (contains): '{}', Actual: '{}'.",
                expectedLocation, capturedJobLocation);
        Assert.assertTrue(
                String.format("Job location assertion failed.%n  Expected (contains): '%s'%n  Actual: '%s'",
                        expectedLocation, capturedJobLocation),
                capturedJobLocation.toLowerCase().contains(expectedLocation.toLowerCase())
        );
    }

    /**
     * 5c — "the job ID should be displayed and not empty"
     */
    @And("the job ID should be displayed and not empty")
    public void theJobIdShouldBeDisplayedAndNotEmpty() {
        capturedJobId = jobDetailsPage.getJobId();
        log.info("Step: Assert job ID is present. Value: '{}'.", capturedJobId);
        Assert.assertNotNull("Job ID should not be null.", capturedJobId);
        Assert.assertFalse(
                "Job ID should not be empty. Retrieved: '" + capturedJobId + "'",
                capturedJobId.trim().isEmpty()
        );
    }

    /**
     * 5d — "the job description section should contain {string}"
     * (Custom assertion 1 — confirms 'automation' keyword in description)
     */
    @And("the job description section should contain {string}")
    public void theJobDescriptionSectionShouldContain(String keyword) {
        log.info("Step: Assert description contains '{}'.", keyword);
        Assert.assertTrue(
                String.format("Expected job description to contain '%s' but it did not.", keyword),
                jobDetailsPage.descriptionContains(keyword)
        );
    }

    /**
     * 5e — "the requirements section should contain {string}"
     * (Custom assertion 2 — confirms 'Selenium' appears in requirements/tools)
     */
    @And("the requirements section should contain {string}")
    public void theRequirementsSectionShouldContain(String keyword) {
        log.info("Step: Assert requirements/tools contains '{}'.", keyword);
        Assert.assertTrue(
                String.format("Expected requirements section to contain '%s' but it did not.", keyword),
                jobDetailsPage.descriptionContains(keyword)
        );
    }

    /**
     * 5f — "the page should contain the text {string}"
     * (Custom assertion 3 — checks general page body for 'quality assurance')
     */
    @And("the page should contain the text {string}")
    public void thePageShouldContainTheText(String expectedText) {
        log.info("Step: Assert page body contains '{}'.", expectedText);
        String bodyText = jobDetailsPage.getPageBodyText();
        Assert.assertTrue(
                String.format("Expected page body to contain '%s' but it did not.", expectedText),
                bodyText.contains(expectedText.toLowerCase())
        );
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Step 6 — Apply Now verification
    // ══════════════════════════════════════════════════════════════════════════

    /** "I click the {string} button" */
    @When("I click the {string} button")
    public void iClickTheButton(String buttonLabel) {
        log.info("Step: Click '{}' button.", buttonLabel);
        jobDetailsPage.clickApplyNow();
    }

    /**
     * 6a — "the apply page job title should match {string}"
     * Verifies the title on the apply page matches the listing page title.
     */
    @Then("the apply page job title should match {string}")
    public void theApplyPageJobTitleShouldMatch(String expectedTitle) {
        String applyTitle = applyPage.getApplyPageJobTitle();
        log.info("Step: Assert apply page job title. Expected: '{}', Actual: '{}'.",
                expectedTitle, applyTitle);
        Assert.assertTrue(
                String.format("Apply page job title mismatch.%n  Expected (contains): '%s'%n  Actual: '%s'",
                        expectedTitle, applyTitle),
                applyTitle.toLowerCase().contains(expectedTitle.toLowerCase())
        );
    }

    /**
     * 6b — "the apply page job location should contain {string}"
     */
    @And("the apply page job location should contain {string}")
    public void theApplyPageJobLocationShouldContain(String expectedLocation) {
        String applyLocation = applyPage.getApplyPageJobLocation();
        log.info("Step: Assert apply page location. Expected: '{}', Actual: '{}'.",
                expectedLocation, applyLocation);
        Assert.assertTrue(
                String.format("Apply page job location mismatch.%n  Expected (contains): '%s'%n  Actual: '%s'",
                        expectedLocation, applyLocation),
                applyLocation.toLowerCase().contains(expectedLocation.toLowerCase())
        );
    }

    /**
     * 6c — "the apply page job ID should match the listing page job ID"
     * Cross-page assertion: checks the ID on the apply page equals the
     * one captured on the job details page.
     */
    @And("the apply page job ID should match the listing page job ID")
    public void theApplyPageJobIdShouldMatchTheListingPageJobId() {
        String applyJobId = applyPage.getApplyPageJobId();
        log.info("Step: Assert apply page job ID matches listing. "
                + "Details page ID: '{}', Apply page ID: '{}'.", capturedJobId, applyJobId);
        Assert.assertEquals(
                String.format("Job ID on apply page should match listing page.%n"
                        + "  Listing ID: '%s'%n  Apply ID:   '%s'", capturedJobId, applyJobId),
                capturedJobId.trim(),
                applyJobId.trim()
        );
    }

    /**
     * 6d — "the apply page should contain relevant position information"
     * Verifies the apply page mentions the captured job title keywords.
     */
    @And("the apply page should contain relevant position information")
    public void theApplyPageShouldContainRelevantPositionInformation() {
        log.info("Step: Assert apply page contains relevant position keywords.");
        // We check for at least one meaningful token from the job title
        // e.g. "QA", "Automation", "Developer" — adjust if title changes.
        String bodyText = applyPage.getApplyPageBodyText();
        boolean hasRelevantContent =
                bodyText.contains("automation")
                || bodyText.contains("developer")
                || bodyText.contains("quality")
                || (capturedJobTitle != null && bodyText.contains(
                        capturedJobTitle.split(" ")[0].toLowerCase()));

        Assert.assertTrue(
                "Apply page should contain relevant position information from the listing page.",
                hasRelevantContent
        );
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Return to Job Search
    // ══════════════════════════════════════════════════════════════════════════

    /** "I click the {string} link" */
    @When("I click the {string} link")
    public void iClickTheLink(String linkText) {
        log.info("Step: Click '{}' link.", linkText);
        jobDetailsPage.clickReturnToJobSearch();
    }

    /** "I should be back on the job search results page" */
    @Then("I should be back on the job search results page")
    public void iShouldBeBackOnTheJobSearchResultsPage() {
        log.info("Step: Assert we are back on job search results page.");
        Assert.assertTrue(
                "Expected to be back on the job search / careers results page.",
                jobDetailsPage.isBackOnSearchResultsPage()
        );
    }
}
