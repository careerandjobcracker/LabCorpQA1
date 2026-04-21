package com.labcorp.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

/**
 * DriverFactory — centralises WebDriver creation and teardown.
 *
 * Usage:
 *   DriverFactory.initDriver();
 *   WebDriver driver = DriverFactory.getDriver();
 *   DriverFactory.quitDriver();
 */
public class DriverFactory {

    // ThreadLocal supports parallel test execution without driver collisions
    private static final ThreadLocal<WebDriver> driverThread = new ThreadLocal<>();

    private DriverFactory() {
        // Utility class – no instantiation
    }

    /**
     * Initialises a ChromeDriver and stores it in ThreadLocal.
     * WebDriverManager automatically downloads the correct ChromeDriver binary.
     */
    public static void initDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        // ── Common Chrome options ──────────────────────────────────────────────
        options.addArguments("--start-maximized");          // Maximise on launch
        options.addArguments("--disable-notifications");    // Suppress pop-ups
        options.addArguments("--disable-infobars");         // Hide "Chrome is being controlled" bar
        options.addArguments("--disable-extensions");
        options.addArguments("--no-sandbox");               // Required in CI/Docker
        options.addArguments("--disable-dev-shm-usage");   // Prevent OOM in containers

        // Uncomment the next line to run headless in CI environments
        // options.addArguments("--headless=new");

        WebDriver driver = new ChromeDriver(options);

        // ── Implicit wait (baseline fallback) ─────────────────────────────────
        // Explicit waits (WebDriverWait) are preferred throughout this framework,
        // but a small implicit wait acts as a safety net for simple lookups.
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(15));

        driverThread.set(driver);
    }

    /**
     * Returns the WebDriver for the current thread.
     * Throws IllegalStateException if the driver has not been initialised.
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThread.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver has not been initialised. Call DriverFactory.initDriver() first.");
        }
        return driver;
    }

    /**
     * Quits the browser and removes the driver from ThreadLocal storage.
     * Safe to call even if the driver is already null.
     */
    public static void quitDriver() {
        WebDriver driver = driverThread.get();
        if (driver != null) {
            driver.quit();
            driverThread.remove();
        }
    }
}
