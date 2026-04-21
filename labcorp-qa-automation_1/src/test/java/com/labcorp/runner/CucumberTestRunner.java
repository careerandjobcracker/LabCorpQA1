package com.labcorp.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * CucumberTestRunner — JUnit 4 runner that bootstraps Cucumber.
 *
 * Run this class as a JUnit test (IDE) or via:
 *   mvn test -Dtest=CucumberTestRunner
 *
 * CucumberOptions:
 *   features   — path to .feature files
 *   glue       — package(s) containing step definitions and hooks
 *   plugin     — report formats (pretty console + HTML + JSON)
 *   tags       — filter by Cucumber tag (override with -Dcucumber.filter.tags)
 *   monochrome — cleaner console output (no ANSI escape codes)
 *   dryRun     — set to true to validate step mappings without running the browser
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features  = "src/test/resources/features",
        glue      = "com.labcorp.stepDefinitions",
        plugin    = {
                "pretty",                                       // Human-readable console output
                "html:target/cucumber-reports/cucumber.html",  // HTML report
                "json:target/cucumber-reports/cucumber.json",  // JSON (for CI dashboards)
                "junit:target/cucumber-reports/cucumber.xml"   // JUnit XML (for Jenkins)
        },
        tags      = "@LabCorpCareers",   // Run only tagged scenarios
        monochrome = true,
        dryRun    = false                 // Set to true for a step-mapping check
)
public class CucumberTestRunner {
    /*
     * This class is intentionally empty.
     * The @RunWith and @CucumberOptions annotations do all the work.
     */
}
