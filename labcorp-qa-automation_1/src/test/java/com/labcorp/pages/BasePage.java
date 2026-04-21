package com.labcorp.pages;

import com.labcorp.utils.DriverFactory;
import com.labcorp.utils.WaitHelper;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * BasePage — parent class for all Page Object classes.
 *
 * Provides shared access to:
 *  - WebDriver  (from DriverFactory)
 *  - WaitHelper (wraps explicit waits)
 *  - JavascriptExecutor helpers
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WaitHelper wait;

    public BasePage() {
        this.driver = DriverFactory.getDriver();
        this.wait   = new WaitHelper(driver);
    }

    // ── Common helpers ────────────────────────────────────────────────────────

    /** Returns the current browser page title. */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /** Returns the current URL. */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /** Navigates back one page in browser history. */
    public void navigateBack() {
        driver.navigate().back();
        wait.waitForPageLoad();
    }

    /** Executes arbitrary JavaScript in the browser context. */
    protected Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    /** Scrolls the page by the given pixel offset. */
    protected void scrollBy(int x, int y) {
        executeScript("window.scrollBy(arguments[0], arguments[1]);", x, y);
    }

    /** Scrolls the page to the very top. */
    protected void scrollToTop() {
        executeScript("window.scrollTo(0, 0);");
    }
}
