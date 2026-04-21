package com.labcorp.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * JobDetailsPage — Page Object for the individual job posting page.
 *
 * This class covers Steps 5 and 6 of the test:
 *   - Assert Job Title, Location, and Job ID
 *   - Assert 3 additional custom fields from the job description
 *   - Click "Apply Now" and verify data carries over to the application page
 *   - Click "Return to Job Search"
 *
 * Locator strategies used (demonstrates 3+ By types):
 *   By.cssSelector  — primary for structured Workday elements
 *   By.xpath        — for text-based traversal and complex conditions
 *   By.id           — for uniquely identified page sections
 *   By.tagName      — for broad structural queries (e.g., all <h1> elements)
 *   By.className    — for elements identified by CSS class only
 */
public class JobDetailsPage extends BasePage {

    private static final Logger log = LoggerFactory.getLogger(JobDetailsPage.class);

    // ══════════════════════════════════════════════════════════════════════════
    //  Locators — Job Details Page
    // ══════════════════════════════════════════════════════════════════════════

    // ── Job Title ─────────────────────────────────────────────────────────────
    /** By.cssSelector — Workday job title element */
    private static final By JOB_TITLE_CSS =
            By.cssSelector("[data-automation-id='jobPostingTitle'], "
                    + "h1.job-title, .job-header h1, h1[class*='title']");

    /** By.tagName — fall back: grab the first <h1> on the page */
    private static final By JOB_TITLE_H1 = By.tagName("h1");

    // ── Job Location ──────────────────────────────────────────────────────────
    /** By.cssSelector — Workday location element */
    private static final By JOB_LOCATION_CSS =
            By.cssSelector("[data-automation-id='locationHierarchy'], "
                    + "[data-automation-id='locations'], "
                    + ".job-location, [class*='location']");

    /** By.xpath — any element whose label/heading text says "Location" */
    private static final By JOB_LOCATION_XPATH =
            By.xpath("//*[contains(@data-automation-id,'location') or "
                    + "contains(@class,'location')]");

    // ── Job ID ────────────────────────────────────────────────────────────────
    /**
     * By.cssSelector — the requisition / job ID.
     * Workday exposes this as data-automation-id="requisitionId"
     * or in a span/div with class containing "job-id".
     */
    private static final By JOB_ID_CSS =
            By.cssSelector("[data-automation-id='requisitionId'], "
                    + "[data-automation-id='jobPostingId'], "
                    + ".job-id, [class*='requisition'], [class*='jobId']");

    /**
     * By.xpath — searches for a labelled pair where nearby text says "Job ID"
     * or "Requisition ID" — handles layouts where the id is plain text.
     */
    private static final By JOB_ID_XPATH =
            By.xpath("//*[contains(text(),'Job ID') or contains(text(),'Req ID') "
                    + "or contains(text(),'Requisition')]/following-sibling::*[1] | "
                    + "//*[@data-automation-id='requisitionId']");

    // ── Job Description Body ──────────────────────────────────────────────────
    /**
     * By.cssSelector — the main description/body section.
     * Workday typically wraps everything in a rich-text container.
     */
    private static final By JOB_DESCRIPTION_BODY =
            By.cssSelector("[data-automation-id='jobPostingDescription'], "
                    + ".job-description, .description-body, "
                    + "article.job-details, section.posting-body");

    /** By.xpath — all paragraphs inside the description section */
    private static final By DESCRIPTION_PARAGRAPHS =
            By.xpath("//div[contains(@data-automation-id,'description') or "
                    + "contains(@class,'description') or "
                    + "contains(@class,'posting')]//p");

    /** By.className — requirement bullet items */
    private static final By REQUIREMENT_LIST_ITEMS =
            By.className("gwt-Label");   // adjust to actual class on live site

    // ── Buttons / Navigation ──────────────────────────────────────────────────
    /** By.cssSelector — "Apply Now" primary CTA button */
    private static final By APPLY_NOW_BUTTON =
            By.cssSelector("[data-automation-id='applyButton'], "
                    + "a[href*='apply'], button[class*='apply'], "
                    + "a.apply-now, button.apply-now");

    /** By.xpath — "Apply Now" fallback text match */
    private static final By APPLY_NOW_XPATH =
            By.xpath("//a[contains(translate(text(),'apply now','APPLY NOW'),'APPLY NOW')] | "
                    + "//button[contains(translate(text(),'apply now','APPLY NOW'),'APPLY NOW')]");

    /** By.linkText — "Return to Job Search" link */
    private static final By RETURN_TO_SEARCH_LINK_TEXT =
            By.linkText("Return to Job Search");

    /** By.cssSelector fallback for "Return to Job Search" */
    private static final By RETURN_TO_SEARCH_CSS =
            By.cssSelector("a[href*='search'], a[class*='return'], "
                    + "[data-automation-id='backToSearchResultsLink']");

    /** By.xpath fallback for "Return to Job Search" */
    private static final By RETURN_TO_SEARCH_XPATH =
            By.xpath("//a[contains(translate(.,'return to job search','RETURN TO JOB SEARCH'),"
                    + "'RETURN TO JOB SEARCH') or contains(@class,'back') or "
                    + "contains(@data-automation-id,'back')]");

    // ══════════════════════════════════════════════════════════════════════════
    //  Page-state checks
    // ══════════════════════════════════════════════════════════════════════════

    /** Returns true once the job title element is visible on the page. */
    public boolean isJobDetailsPageDisplayed() {
        try {
            WebElement titleEl = resolveJobTitleElement();
            boolean visible = titleEl.isDisplayed() && !titleEl.getText().trim().isEmpty();
            log.info("Job details page loaded. Title visible: {}", visible);
            return visible;
        } catch (Exception e) {
            log.warn("Job details page not detected: {}", e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Getters — core job fields
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Returns the job title text from the page.
     * Cascades: By.cssSelector → By.tagName (h1)
     */
    public String getJobTitle() {
        WebElement el = resolveJobTitleElement();
        String title = el.getText().trim();
        log.info("Job title retrieved: '{}'", title);
        return title;
    }

    /**
     * Returns the job location text.
     * Cascades: By.cssSelector → By.xpath
     */
    public String getJobLocation() {
        WebElement el;
        try {
            el = wait.waitForVisibility(JOB_LOCATION_CSS);
        } catch (Exception e) {
            log.warn("Location CSS failed — trying XPath.");
            el = wait.waitForVisibility(JOB_LOCATION_XPATH);
        }
        String location = el.getText().trim();
        log.info("Job location retrieved: '{}'", location);
        return location;
    }

    /**
     * Returns the Job ID / Requisition ID text.
     * Cascades: By.cssSelector → By.xpath
     */
    public String getJobId() {
        WebElement el;
        try {
            el = wait.waitForPresence(JOB_ID_CSS);
        } catch (Exception e) {
            log.warn("Job ID CSS failed — trying XPath.");
            el = wait.waitForPresence(JOB_ID_XPATH);
        }
        String jobId = el.getText().trim();
        log.info("Job ID retrieved: '{}'", jobId);
        return jobId;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Additional content assertions
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Returns the full text content of the job description body section.
     * By.cssSelector — primary; falls back to reading the full page source text.
     */
    public String getJobDescriptionText() {
        try {
            WebElement descEl = wait.waitForVisibility(JOB_DESCRIPTION_BODY);
            wait.scrollIntoView(descEl);
            String text = descEl.getText();
            log.info("Description body text length: {} chars", text.length());
            return text.toLowerCase();
        } catch (Exception e) {
            log.warn("Description body element not found via CSS — reading page body text.");
            return driver.findElement(By.tagName("body")).getText().toLowerCase();
        }
    }

    /**
     * Returns true if the description section contains the given keyword
     * (case-insensitive search).
     */
    public boolean descriptionContains(String keyword) {
        boolean found = getJobDescriptionText().contains(keyword.toLowerCase());
        log.info("Description contains '{}': {}", keyword, found);
        return found;
    }

    /**
     * Returns the text of the N-th paragraph (1-based index) inside the
     * description section.
     *
     * By.xpath — precise paragraph targeting.
     */
    public String getParagraphText(int paragraphIndex) {
        try {
            List<WebElement> paragraphs = driver.findElements(DESCRIPTION_PARAGRAPHS);
            if (paragraphIndex > paragraphs.size()) {
                log.warn("Paragraph index {} out of bounds (found {}); returning empty.",
                        paragraphIndex, paragraphs.size());
                return "";
            }
            String text = paragraphs.get(paragraphIndex - 1).getText().trim();
            log.info("Paragraph {}: '{}'", paragraphIndex, text);
            return text;
        } catch (Exception e) {
            log.warn("Could not retrieve paragraph {}: {}", paragraphIndex, e.getMessage());
            return "";
        }
    }

    /**
     * Returns the full page body text (lowercased) for broad text assertions.
     * By.tagName — simplest broad-coverage selector.
     */
    public String getPageBodyText() {
        return driver.findElement(By.tagName("body")).getText().toLowerCase();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Interactions
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Scrolls to and clicks the "Apply Now" button.
     *
     * Cascades: By.cssSelector → By.xpath → JS click fallback
     */
    public void clickApplyNow() {
        log.info("Clicking 'Apply Now' button.");
        WebElement applyBtn;

        try {
            applyBtn = wait.waitForClickability(APPLY_NOW_BUTTON);
        } catch (Exception e1) {
            log.warn("Apply Now CSS failed — trying XPath.");
            applyBtn = wait.waitForClickability(APPLY_NOW_XPATH);
        }

        wait.scrollIntoView(applyBtn);
        try {
            applyBtn.click();
        } catch (Exception e) {
            log.warn("Normal click intercepted — using JS click.");
            wait.jsClick(applyBtn);
        }

        wait.waitForPageLoad();
        log.info("Apply page URL: {}", driver.getCurrentUrl());
    }

    /**
     * Clicks the "Return to Job Search" link.
     *
     * Cascades: By.linkText → By.cssSelector → By.xpath
     */
    public void clickReturnToJobSearch() {
        log.info("Clicking 'Return to Job Search'.");
        WebElement returnLink;

        try {
            // By.linkText ─────────────────────────────────────────────────────
            returnLink = wait.waitForClickability(RETURN_TO_SEARCH_LINK_TEXT);
            log.info("Found 'Return to Job Search' via linkText.");
        } catch (Exception e1) {
            log.warn("linkText strategy failed — trying CSS.");
            try {
                // By.cssSelector ──────────────────────────────────────────────
                returnLink = wait.waitForClickability(RETURN_TO_SEARCH_CSS);
                log.info("Found 'Return to Job Search' via CSS.");
            } catch (Exception e2) {
                log.warn("CSS strategy failed — trying XPath.");
                // By.xpath ────────────────────────────────────────────────────
                returnLink = wait.waitForClickability(RETURN_TO_SEARCH_XPATH);
                log.info("Found 'Return to Job Search' via XPath.");
            }
        }

        wait.scrollIntoView(returnLink);
        returnLink.click();
        wait.waitForPageLoad();
        log.info("Returned to search. URL: {}", driver.getCurrentUrl());
    }

    /**
     * Verifies we are back on a page that looks like the job search results.
     */
    public boolean isBackOnSearchResultsPage() {
        String url = driver.getCurrentUrl().toLowerCase();
        boolean onSearch = url.contains("search") || url.contains("career") || url.contains("job");
        log.info("Back on search page: {} (URL: {})", onSearch, driver.getCurrentUrl());
        return onSearch;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Private helpers
    // ══════════════════════════════════════════════════════════════════════════

    /** Resolves the job title element via cascading By types. */
    private WebElement resolveJobTitleElement() {
        try {
            // By.cssSelector (Workday data attribute or semantic class)
            return wait.waitForVisibility(JOB_TITLE_CSS);
        } catch (Exception e) {
            // By.tagName — the page's first <h1>
            return wait.waitForVisibility(JOB_TITLE_H1);
        }
    }
}
