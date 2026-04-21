# ============================================================
# Feature: LabCorp Careers - Job Listing Verification
# Author:  Raj. Seelam
# Description: Verifies navigation through the LabCorp Careers
#              portal to a specific job listing, confirms all
#              job detail fields, and validates the Apply page.
# ============================================================

@LabCorpCareers
Feature: LabCorp Careers Job Listing Verification

  As a job seeker visiting the LabCorp website
  I want to navigate to the Careers section and find a specific job posting
  So that I can verify the job details are accurately displayed

  Background:
    Given the Chrome browser is open and maximized
    And I navigate to "https://www.labcorp.com"

  @SmokeTest @JobSearch
  Scenario: Navigate to Careers and verify a QA Automation Developer job listing

    # --- Step 1: Land on LabCorp homepage ---
    Then the LabCorp homepage should be displayed

    # --- Step 2: Click Careers link ---
    When I click on the "Careers" navigation link
    Then I should be on the Careers landing page

    # --- Step 3: Search for a position ---
    When I search for the position "QA Test Automation Developer"
    Then the search results should be displayed

    # --- Step 4: Select the position from results ---
    When I click on the first matching job result for "QA Test Automation Developer"
    Then I should be on the job details page

    # --- Step 5a: Assert Job Title ---
    Then the job title should be "QA Test Automation Developer"

    # --- Step 5b: Assert Job Location ---
    And the job location should contain "Burlington"

    # --- Step 5c: Assert Job ID is present ---
    And the job ID should be displayed and not empty

    # --- Step 5d-f: Additional assertions on job description content ---
    And the job description section should contain "automation"
    And the requirements section should contain "Selenium"
    And the page should contain the text "quality assurance"

    # --- Step 6: Click Apply Now and verify details carry over ---
    When I click the "Apply Now" button
    Then the apply page job title should match "QA Test Automation Developer"
    And the apply page job location should contain "Burlington"
    And the apply page job ID should match the listing page job ID
    And the apply page should contain relevant position information

    # --- Return to job search ---
    When I click the "Return to Job Search" link
    Then I should be back on the job search results page
