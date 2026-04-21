package com.labcorp.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ApplyPage — Page Object for the Job Application page.
 *
 * Reached after clicking "Apply Now" on the JobDetailsPage.
 * Step 6 verifies that the job title, location, job ID, and
 * additional text match what was shown on the details page.
 *
 * Locator strategy mix:
 *   By.cssSelector  — primary (Workday data-automation-id attributes)
 *   By.xpath        — text-based and hierarchical traversal
 *   By.id           — unique page-level identifiers
 *   By.tagName      — broad fallbacks
 */
public class ApplyPage extends BasePage {

    private static final Logger log = LoggerFactory.getLogger(ApplyPage.class);

    // ══════════════════════════════════════════════════════════════════════════
    //  Locators
    // ══════════════════════════════════════════════════════════════════════════

    // ── Job Title on Apply Page ────────────────────────────────────────────
    /** By.cssSelector — Workday apply page job title container */
    private static final By APPLY_JOB_TITLE_CSS =
            By.cssSelector("[data-automation-id='jobPostingTitle'], "
                    + "[data-automation-id='jobTitle'], "
                    + "h1.apply-title, h1[class*='job'], .apply-job-title");

    /** By.xpath — any <h1> or <h2> whose text contains the word "Developer" or "QA" */
    private static final By APPLY_JOB_TITLE_XPATH =
            By.xpath("//h1 | //h2[contains(@class,'title') or contains(@class,'job')]");

    // ── Job Location on Apply Page ─────────────────────────────────────────
    /** By.cssSelector */
    private static final By APPLY_JOB_LOCATION_CSS =
            By.cssSelector("[data-automation-id='locationHierarchy'], "
                    + "[data-automation-id='locations'], .apply-location, "
                    + "[class*='location']");

    /** By.xpath fallback */
    private static final By APPLY_JOB_LOCATION_XPATH =
            By.xpath("//*[contains(@data-automation-id,'location') "
                    + "or contains(@class,'location')]");

    // ── Job ID on Apply Page ───────────────────────────────────────────────
    /** By.cssSelector */
    private static final By APPLY_JOB_ID_CSS =
            By.cssSelector("[data-automation-id='requisitionId'], "
                    + "[data-automation-id='jobPostingId'], "
                    + ".req-id, [class*='jobId'], [class*='requisition']");

    /** By.xpath — label + sibling value pair */
    private static final By APPLY_JOB_ID_XPATH =
            By.xpath("//*[contains(text(),'Job ID') or contains(text(),'Req') or "
                    + "contains(text(),'Requisition')]/following-sibling::*[1]");

    // ── Apply Page general body ────────────────────────────────────────────
    /** By.tagName — broadest possible content check */
    private static final By PAGE_BODY = By.tagName("body");

    // ── Page header / breadcrumb ───────────────────────────────────────────
    /**
     * By.id — Workday apply page sometimes exposes a "wd-ApplyPage" or
     * "apply-header" id on the top container.
     */
    private static final By APPLY_PAGE_HEADER_ID =
            By.id("apply-header");

    // ══════════════════════════════════════════════════════════════════════════
    //  Page-state checks
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Returns true when the apply page URL and/or a recognisable element
     * confirms we are on the application page.
     */
    public boolean isApplyPageDisplayed() {
        String url = driver.getCurrentUrl().toLowerCase();
        boolean urlMatch = url.contains("apply") || url.contains("application");
        log.info("Apply page URL check: {} ({})", urlMatch, driver.getCurrentUrl());
        return urlMatch;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Getters — mirroring JobDetailsPage fields
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Returns the job title displayed on the Apply page.
     * Cascades: By.cssSelector → By.xpath
     */
    public String getApplyPageJobTitle() {
        WebElement el;
        try {
            el = wait.waitForVisibility(APPLY_JOB_TITLE_CSS);
        } catch (Exception e) {
            log.warn("Apply page job title CSS failed — trying XPath.");
            el = wait.waitForVisibility(APPLY_JOB_TITLE_XPATH);
        }
        String title = el.getText().trim();
        log.info("Apply page job title: '{}'", title);
        return title;
    }

    /**
     * Returns the job location displayed on the Apply page.
     * Cascades: By.cssSelector → By.xpath
     */
    public String getApplyPageJobLocation() {
        WebElement el;
        try {
            el = wait.waitForVisibility(APPLY_JOB_LOCATION_CSS);
        } catch (Exception e) {
            log.warn("Apply page location CSS failed — trying XPath.");
            el = wait.waitForVisibility(APPLY_JOB_LOCATION_XPATH);
        }
        String location = el.getText().trim();
        log.info("Apply page job location: '{}'", location);
        return location;
    }

    /**
     * Returns the Job ID displayed on the Apply page.
     * Cascades: By.cssSelector → By.xpath
     */
    public String getApplyPageJobId() {
        WebElement el;
        try {
            el = wait.waitForPresence(APPLY_JOB_ID_CSS);
        } catch (Exception e) {
            log.warn("Apply page job ID CSS failed — trying XPath.");
            el = wait.waitForPresence(APPLY_JOB_ID_XPATH);
        }
        String jobId = el.getText().trim();
        log.info("Apply page job ID: '{}'", jobId);
        return jobId;
    }

    /**
     * Returns the full text of the apply page body (lowercased).
     * By.tagName — widest possible assertion coverage.
     */
    public String getApplyPageBodyText() {
        return driver.findElement(PAGE_BODY).getText().toLowerCase();
    }

    /**
     * Returns true if the apply page body text contains the given keyword
     * (case-insensitive).
     */
    public boolean applyPageContains(String keyword) {
        boolean found = getApplyPageBodyText().contains(keyword.toLowerCase());
        log.info("Apply page contains '{}': {}", keyword, found);
        return found;
    }
}
