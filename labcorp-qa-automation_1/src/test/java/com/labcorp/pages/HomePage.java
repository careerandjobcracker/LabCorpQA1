package com.labcorp.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HomePage — Page Object for https://www.labcorp.com
 *
 * Locator strategy mix (requirement: at least 3 different By types):
 *   By.id         — precise, fastest lookup
 *   By.linkText   — natural for anchor elements with known visible text
 *   By.cssSelector— flexible for composites / attribute selectors
 *   By.xpath      — powerful for complex DOM traversal
 *   By.tagName    — used for general element existence checks
 */
public class HomePage extends BasePage {

    private static final Logger log = LoggerFactory.getLogger(HomePage.class);

    // ── Locators ──────────────────────────────────────────────────────────────

    /** By.cssSelector — LabCorp logo / brand mark in the header. */
    private static final By LABCORP_LOGO = By.cssSelector("img[alt*='Labcorp'], img[alt*='LabCorp'], .site-logo img, header img");

    /**
     * By.linkText — the top-level "Careers" navigation link.
     * Note: LabCorp's nav uses exact visible text "Careers"; adjust if the
     * site's markup changes (e.g., to "Careers & Jobs").
     */
    private static final By CAREERS_NAV_LINK = By.linkText("Careers");

    /**
     * By.xpath fallback for Careers — matches any anchor whose text
     * contains "Careers" (case-insensitive normalization trick).
     */
    private static final By CAREERS_NAV_XPATH =
            By.xpath("//a[contains(translate(text(),'careers','CAREERS'),'CAREERS')]");

    /**
     * By.cssSelector — covers common nav patterns: top-bar or hamburger menu.
     * More resilient than linkText when nav items are wrapped in <span> tags.
     */
    private static final By CAREERS_NAV_CSS =
            By.cssSelector("a[href*='careers'], nav a[href*='career']");

    // Cookie / consent banner dismiss button (optional — handles pop-ups)
    private static final By COOKIE_ACCEPT_BTN =
            By.cssSelector("#onetrust-accept-btn-handler, button[id*='accept'], .accept-cookies");

    // ── Actions ───────────────────────────────────────────────────────────────

    /**
     * Navigates the browser to the LabCorp homepage.
     * Waits for the page to fully load before returning.
     */
    public void openHomePage(String url) {
        log.info("Navigating to: {}", url);
        driver.get(url);
        wait.waitForPageLoad();
        log.info("Page title after load: {}", driver.getTitle());
    }

    /**
     * Dismisses a cookie-consent overlay if one is present.
     * Swallows the exception silently when the element is absent.
     */
    public void dismissCookieBannerIfPresent() {
        try {
            WebElement cookieBtn = driver.findElement(COOKIE_ACCEPT_BTN);
            if (cookieBtn.isDisplayed()) {
                log.info("Cookie banner detected — dismissing.");
                cookieBtn.click();
                wait.hardWait(800);   // brief settle after banner closes
            }
        } catch (Exception ignored) {
            log.debug("No cookie banner found — continuing.");
        }
    }

    /**
     * Verifies that the LabCorp homepage is displayed by checking the page
     * title contains "Labcorp" or "LabCorp".
     */
    public boolean isHomePageDisplayed() {
        String title = driver.getTitle().toLowerCase();
        boolean displayed = title.contains("labcorp");
        log.info("Homepage title: '{}' — matches: {}", driver.getTitle(), displayed);
        return displayed;
    }

    /**
     * Finds and clicks the Careers navigation link.
     *
     * Strategy:
     *  1. Try By.linkText (exact match — fastest)
     *  2. Fall back to By.cssSelector (partial href match)
     *  3. Fall back to By.xpath (text contains match)
     */
    public void clickCareersLink() {
        WebElement careersLink = null;

        // Attempt 1: By.linkText ──────────────────────────────────────────────
        try {
            log.info("Attempting to find Careers link by linkText.");
            careersLink = wait.waitForClickability(CAREERS_NAV_LINK);
        } catch (Exception e1) {
            log.warn("linkText strategy failed: {}", e1.getMessage());

            // Attempt 2: By.cssSelector ───────────────────────────────────────
            try {
                log.info("Falling back to cssSelector for Careers link.");
                careersLink = wait.waitForClickability(CAREERS_NAV_CSS);
            } catch (Exception e2) {
                log.warn("cssSelector strategy failed: {}", e2.getMessage());

                // Attempt 3: By.xpath ─────────────────────────────────────────
                log.info("Falling back to XPath for Careers link.");
                careersLink = wait.waitForClickability(CAREERS_NAV_XPATH);
            }
        }

        log.info("Clicking Careers link.");
        wait.scrollIntoView(careersLink);
        careersLink.click();
        wait.waitForPageLoad();
    }
}
