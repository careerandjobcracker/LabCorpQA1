package com.labcorp.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * WaitHelper — reusable explicit-wait methods.
 *
 * All methods use WebDriverWait (explicit waits), which are
 * far more reliable than Thread.sleep() or implicit waits alone.
 *
 * Default timeout : 20 seconds
 * Default polling : 500 ms (WebDriverWait default)
 */
public class WaitHelper {

    private static final int DEFAULT_TIMEOUT_SECONDS = 20;
    private static final int EXTENDED_TIMEOUT_SECONDS = 40;

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final WebDriverWait extendedWait;

    public WaitHelper(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
        this.extendedWait = new WebDriverWait(driver, Duration.ofSeconds(EXTENDED_TIMEOUT_SECONDS));
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Visibility waits
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Waits until the element located by the given By is visible on the page.
     * Uses ExpectedConditions.visibilityOfElementLocated — covers both rendering
     * and non-zero dimensions.
     */
    public WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Same as waitForVisibility but uses the extended (40 s) timeout. */
    public WebElement waitForVisibilityExtended(By locator) {
        return extendedWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Waits until a WebElement that is already in hand becomes visible. */
    public WebElement waitForVisibilityOfElement(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Clickability waits
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Waits until the element is both visible AND enabled (clickable).
     * Preferred over waitForVisibility when you intend to click next.
     */
    public WebElement waitForClickability(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement waitForClickabilityOfElement(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Presence waits (element in DOM, not necessarily visible)
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Waits until the element is present in the DOM.
     * Use this for hidden elements you need to read (e.g., hidden input with job ID).
     */
    public WebElement waitForPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Page / URL waits
    // ──────────────────────────────────────────────────────────────────────────

    /** Waits until the page URL contains the given substring. */
    public boolean waitForUrlContains(String urlFragment) {
        return wait.until(ExpectedConditions.urlContains(urlFragment));
    }

    /** Waits until the document.readyState equals "complete". */
    public void waitForPageLoad() {
        wait.until(driver -> {
            String state = ((JavascriptExecutor) driver)
                    .executeScript("return document.readyState").toString();
            return "complete".equals(state);
        });
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Text / attribute waits
    // ──────────────────────────────────────────────────────────────────────────

    /** Waits until the element's text is not blank. */
    public WebElement waitForNonEmptyText(By locator) {
        return wait.until(driver -> {
            WebElement el = driver.findElement(locator);
            return (el.getText() != null && !el.getText().trim().isEmpty()) ? el : null;
        });
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Staleness / disappearance waits
    // ──────────────────────────────────────────────────────────────────────────

    /** Waits until the element is no longer attached to the DOM (useful after navigation). */
    public boolean waitForStaleness(WebElement element) {
        return wait.until(ExpectedConditions.stalenessOf(element));
    }

    /** Waits until the element identified by the locator is invisible / gone. */
    public boolean waitForInvisibility(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Frame waits
    // ──────────────────────────────────────────────────────────────────────────

    /** Waits for a frame to be available and switches to it. */
    public WebDriver waitForFrameAndSwitch(By frameLocator) {
        return wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameLocator));
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Utility
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Scrolls the element into the viewport using JavaScript.
     * Helpful for elements below the fold that cannot be clicked otherwise.
     */
    public void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", element);
    }

    /**
     * Clicks an element via JavaScript — use as a fallback when a normal
     * Selenium click is intercepted by overlays or animations.
     */
    public void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /**
     * A short, unconditional pause — use sparingly and only where an explicit
     * condition is genuinely unavailable (e.g., CSS animation settling).
     */
    public void hardWait(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
