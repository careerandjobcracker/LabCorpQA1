import './FeatureView.css'

const featureContent = `@LabCorpCareers
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
    Then I should be back on the job search results page`

function highlightLine(line: string) {
  if (line.startsWith('@')) return 'gherkin-tag'
  if (line.startsWith('Feature:')) return 'gherkin-feature'
  if (line.startsWith('  Scenario:') || line.trimStart().startsWith('Scenario:')) return 'gherkin-scenario'
  if (line.trimStart().startsWith('Background:')) return 'gherkin-background'
  if (line.trimStart().startsWith('#')) return 'gherkin-comment'
  if (line.trimStart().startsWith('Given ')) return 'gherkin-keyword'
  if (line.trimStart().startsWith('When ')) return 'gherkin-keyword'
  if (line.trimStart().startsWith('Then ')) return 'gherkin-keyword'
  if (line.trimStart().startsWith('And ')) return 'gherkin-keyword'
  if (line.trimStart().startsWith('As a ') || line.trimStart().startsWith('I want') || line.trimStart().startsWith('So that')) return 'gherkin-narrative'
  return ''
}

function renderLine(line: string) {
  const trimmed = line.trimStart()
  const indent = line.length - trimmed.length

  const stringParts = trimmed.split(/(".*?")/g)
  return (
    <>
      {'  '.repeat(Math.floor(indent / 2))}
      {stringParts.map((part, i) =>
        part.startsWith('"') ? (
          <span key={i} className="gherkin-string">{part}</span>
        ) : (
          <span key={i}>{part}</span>
        )
      )}
    </>
  )
}

export function FeatureView() {
  const lines = featureContent.split('\n')

  return (
    <div className="feature-view">
      <div className="view-header">
        <h2>Feature File</h2>
        <p className="view-header-sub">LabCorpCareers.feature -- BDD Gherkin test specification</p>
      </div>

      <div className="feature-meta-grid">
        <div className="feature-meta-card">
          <span className="meta-label">Author</span>
          <span className="meta-value">Raj. Seelam</span>
        </div>
        <div className="feature-meta-card">
          <span className="meta-label">Tags</span>
          <div className="meta-tags">
            <span className="meta-tag">@LabCorpCareers</span>
            <span className="meta-tag">@SmokeTest</span>
            <span className="meta-tag">@JobSearch</span>
          </div>
        </div>
        <div className="feature-meta-card">
          <span className="meta-label">Coverage</span>
          <span className="meta-value">Homepage, Careers, Job Search, Job Details, Apply, Return</span>
        </div>
      </div>

      <div className="feature-code-container">
        <div className="feature-code-header">
          <span className="feature-filename">LabCorpCareers.feature</span>
          <span className="feature-lines">{lines.length} lines</span>
        </div>
        <pre className="feature-code">
          {lines.map((line, i) => (
            <div key={i} className={`code-line ${highlightLine(line.trimStart())}`}>
              <span className="line-number">{i + 1}</span>
              <span className="line-content">{renderLine(line)}</span>
            </div>
          ))}
        </pre>
      </div>
    </div>
  )
}
