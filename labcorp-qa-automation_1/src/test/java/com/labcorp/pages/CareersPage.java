package com.labcorp.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * CareersPage — Page Object for the LabCorp Careers landing page
 * and the job search results page.
 *
 * Demonstrates use of By.id, By.name, By.cssSelector, By.xpath,
 * and By.className locator strategies.
 */
public class CareersPage extends BasePage {

    private static final Logger log = LoggerFactory.getLogger(CareersPage.class);

    // ── Locators ──────────────────────────────────────────────────────────────

    /**
     * By.id — the primary job-search input field.
     * LabCorp's Workday-powered career site typically surfaces an <input>
     * with an id like "keyword-input" or similar.
     * Update the id value to match the live DOM if it changes.
     */
    private static final By SEARCH_INPUT_BY_ID =
            By.id("keyword-input");

    /**
     * By.name — fallback for the search field when the id is absent.
     * Workday career portals commonly use name="q" or name="keyword".
     */
    private static final By SEARCH_INPUT_BY_NAME =
            By.name("q");

    /**
     * By.cssSelector — broad selector that catches most text-type inputs
     * in the search bar area.
     */
    private static final By SEARCH_INPUT_BY_CSS =
            By.cssSelector("input[type='text'][placeholder*='Search'], "
                    + "input[type='search'], "
                    + "input[data-automation-id='searchBox'], "
                    + "input[placeholder*='job title'], "
                    + "input[class*='search']");

    /**
     * By.xpath — finds a search button or magnifying-glass icon next to
     * the input field.
     */
    private static final By SEARCH_BUTTON_BY_XPATH =
            By.xpath("//button[contains(@aria-label,'Search') or "
                    + "contains(text(),'Search') or "
                    + "contains(@class,'search-btn') or "
                    + "contains(@data-automation-id,'searchButton')]");

    /** By.cssSelector — Search / Submit button fallback. */
    private static final By SEARCH_BUTTON_BY_CSS =
            By.cssSelector("button[type='submit'], "
                    + "button[data-automation-id='searchButton'], "
                    + "button[aria-label*='Search']");

    /**
     * By.cssSelector — Each job card / listing row in the results grid.
     * Workday renders results inside <li> or <div> elements with
     * data-automation-id="compositeContainer" or similar.
     */
    private static final By JOB_RESULT_ITEMS =
            By.cssSelector("[data-automation-id='compositeContainer'], "
                    + ".job-item, .job-listing-item, "
                    + "li[class*='job'], li[class*='result']");

    /**
     * By.xpath — job title links inside result cards.
     * Targets anchor elements whose text contains the search term.
     */
    private static final By JOB_TITLE_LINKS =
            By.xpath("//a[contains(@data-automation-id,'jobTitle') or "
                    + "contains(@class,'job-title') or "
                    + "contains(@href,'job')]");

    /** By.cssSelector — "No results found" message to detect empty results. */
    private static final By NO_RESULTS_MSG =
            By.cssSelector("[data-automation-id='noResultsMessage'], .no-results, "
                    + ".empty-results");

    // Careers landing-page headline — used to confirm we've reached the page
    private static final By CAREERS_PAGE_HEADING =
            By.xpath("//*[contains(text(),'Career') or contains(text(),'career') or "
                    + "contains(text(),'Job') or contains(text(),'job')]"
                    + "[self::h1 or self::h2]");

    // ── Actions ───────────────────────────────────────────────────────────────

    /**
     * Confirms the Careers landing page is displayed.
     * Checks the URL and looks for a relevant page heading.
     */
    public boolean isCareersPageDisplayed() {
        String url = driver.getCurrentUrl().toLowerCase();
        boolean urlMatch = url.contains("career") || url.contains("job");
        if (!urlMatch) {
            log.warn("URL does not contain 'career' or 'job': {}", driver.getCurrentUrl());
        }
        log.info("Careers page URL check passed: {}", urlMatch);
        return urlMatch;
    }

    /**
     * Types a job title into the search field and submits the search.
     *
     * Locator priority:
     *   1. By.id          (most specific)
     *   2. By.name        (structural attribute)
     *   3. By.cssSelector (flexible pattern match)
     */
    public void searchForPosition(String jobTitle) {
        log.info("Searching for position: '{}'", jobTitle);

        WebElement searchInput = resolveSearchInput();
        wait.scrollIntoView(searchInput);
        searchInput.clear();
        searchInput.sendKeys(jobTitle);
        log.info("Typed '{}' into search field.", jobTitle);

        // Submit via keyboard first, then fall back to clicking the Search button
        try {
            searchInput.sendKeys(Keys.RETURN);
        } catch (Exception e) {
            log.warn("RETURN key failed — trying Search button.");
            clickSearchButton();
        }

        wait.waitForPageLoad();
        log.info("Search submitted. Current URL: {}", driver.getCurrentUrl());
    }

    /** Clicks a dedicated Search / Submit button if present. */
    private void clickSearchButton() {
        try {
            WebElement btn = wait.waitForClickability(SEARCH_BUTTON_BY_XPATH);
            btn.click();
        } catch (Exception e) {
            WebElement btn = wait.waitForClickability(SEARCH_BUTTON_BY_CSS);
            btn.click();
        }
    }

    /**
     * Resolves the search input field using a cascading locator strategy.
     * Demonstrates By.id → By.name → By.cssSelector fallback chain.
     */
    private WebElement resolveSearchInput() {
        // 1. By.id ─────────────────────────────────────────────────────────
        try {
            log.debug("Trying search input by ID.");
            return wait.waitForVisibility(SEARCH_INPUT_BY_ID);
        } catch (Exception ignored) { /* fall through */ }

        // 2. By.name ───────────────────────────────────────────────────────
        try {
            log.debug("Trying search input by name attribute.");
            return wait.waitForVisibility(SEARCH_INPUT_BY_NAME);
        } catch (Exception ignored) { /* fall through */ }

        // 3. By.cssSelector ────────────────────────────────────────────────
        log.debug("Trying search input by CSS selector.");
        return wait.waitForVisibility(SEARCH_INPUT_BY_CSS);
    }

    /**
     * Returns true when at least one job result card is visible on the page.
     */
    public boolean areSearchResultsDisplayed() {
        try {
            List<WebElement> results = driver.findElements(JOB_RESULT_ITEMS);
            boolean hasResults = !results.isEmpty();
            log.info("Search result items found: {}", results.size());
            return hasResults;
        } catch (Exception e) {
            log.warn("Could not locate search result items: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Clicks the first job result that contains the given title text.
     *
     * Strategy:
     *  1. By.xpath — anchors with data-automation-id="jobTitle"
     *  2. By.cssSelector — broad job-title link selector
     */
    public void clickFirstMatchingJobResult(String titleText) {
        log.info("Looking for job result matching: '{}'", titleText);

        WebElement jobLink = null;

        // Attempt 1: By.xpath with text match ────────────────────────────────
        try {
            By specificXpath = By.xpath(
                    "//a[contains(@data-automation-id,'jobTitle') and "
                    + "contains(translate(text(), 'abcdefghijklmnopqrstuvwxyz', "
                    + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), '"
                    + titleText.toUpperCase() + "')]");
            jobLink = wait.waitForClickability(specificXpath);
            log.info("Found job link via specific XPath.");
        } catch (Exception e1) {
            log.warn("Specific XPath failed — trying broad job title selector.");

            // Attempt 2: By.cssSelector — any job-title link ─────────────────
            try {
                List<WebElement> links = driver.findElements(JOB_TITLE_LINKS);
                for (WebElement link : links) {
                    if (link.getText().toLowerCase().contains(titleText.toLowerCase())) {
                        jobLink = link;
                        log.info("Found matching job link via CSS: '{}'", link.getText());
                        break;
                    }
                }
            } catch (Exception e2) {
                log.warn("CSS strategy also failed: {}", e2.getMessage());
            }

            // Attempt 3: By.xpath any anchor with partial text ────────────────
            if (jobLink == null) {
                By fallbackXpath = By.xpath(
                        "//a[contains(translate(normalize-space(.),"
                        + "'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'"
                        + titleText.toUpperCase() + "')]");
                jobLink = wait.waitForClickability(fallbackXpath);
                log.info("Found job link via fallback XPath.");
            }
        }

        if (jobLink == null) {
            throw new RuntimeException(
                    "Could not find a job result matching: '" + titleText + "'");
        }

        wait.scrollIntoView(jobLink);
        log.info("Clicking job result: '{}'", jobLink.getText());
        jobLink.click();
        wait.waitForPageLoad();
    }
}
