import { useState } from 'react'
import { Folder, FolderOpen, FileText, FileCode, ChevronRight, ChevronDown } from 'lucide-react'
import './StructureView.css'

interface TreeNode {
  name: string
  type: 'folder' | 'file'
  description?: string
  children?: TreeNode[]
}

const projectTree: TreeNode = {
  name: 'labcorp-qa-automation',
  type: 'folder',
  children: [
    { name: 'pom.xml', type: 'file', description: 'Maven project configuration with Selenium, Cucumber, JUnit dependencies' },
    { name: 'README.md', type: 'file', description: 'Project documentation and setup instructions' },
    {
      name: 'src',
      type: 'folder',
      children: [{
        name: 'test',
        type: 'folder',
        children: [
          {
            name: 'java/com/labcorp',
            type: 'folder',
            children: [
              {
                name: 'pages',
                type: 'folder',
                description: 'Page Object Model (POM) classes',
                children: [
                  { name: 'BasePage.java', type: 'file', description: 'Abstract base class providing shared WebDriver and WaitHelper access' },
                  { name: 'HomePage.java', type: 'file', description: 'Page Object for www.labcorp.com -- handles navigation, cookie banner, Careers link' },
                  { name: 'CareersPage.java', type: 'file', description: 'Page Object for Careers landing and search results using By.id, By.name, By.cssSelector' },
                  { name: 'JobDetailsPage.java', type: 'file', description: 'Page Object for individual job listing -- title, location, ID, description assertions' },
                  { name: 'ApplyPage.java', type: 'file', description: 'Page Object for Apply Now page -- cross-validates data from job details' },
                ],
              },
              {
                name: 'stepDefinitions',
                type: 'folder',
                description: 'Cucumber step definition glue code',
                children: [
                  { name: 'LabCorpStepDefinitions.java', type: 'file', description: 'Maps Gherkin steps to Page Object methods with JUnit assertions' },
                ],
              },
              {
                name: 'runner',
                type: 'folder',
                description: 'Cucumber test runner configuration',
                children: [
                  { name: 'CucumberTestRunner.java', type: 'file', description: 'JUnit 4 runner with @CucumberOptions for features, glue, plugins, and tags' },
                ],
              },
              {
                name: 'utils',
                type: 'folder',
                description: 'Utility classes',
                children: [
                  { name: 'DriverFactory.java', type: 'file', description: 'ThreadLocal ChromeDriver factory with WebDriverManager auto-setup' },
                  { name: 'WaitHelper.java', type: 'file', description: 'Explicit wait methods: visibility, clickability, presence, URL, page load, staleness' },
                ],
              },
            ],
          },
          {
            name: 'resources',
            type: 'folder',
            children: [
              { name: 'cucumber.properties', type: 'file', description: 'Cucumber runtime configuration' },
              {
                name: 'features',
                type: 'folder',
                description: 'BDD Gherkin feature files',
                children: [
                  { name: 'LabCorpCareers.feature', type: 'file', description: 'Full end-to-end scenario: homepage -> Careers -> search -> job details -> Apply -> Return' },
                ],
              },
            ],
          },
        ],
      }],
    },
  ],
}

function TreeItem({ node, depth = 0 }: { node: TreeNode; depth?: number }) {
  const [expanded, setExpanded] = useState(depth < 3)

  if (node.type === 'file') {
    const isJava = node.name.endsWith('.java')
    const Icon = isJava ? FileCode : FileText
    return (
      <div className="tree-item tree-file" style={{ paddingLeft: `${depth * 20 + 12}px` }}>
        <Icon size={15} className={`tree-icon ${isJava ? 'tree-icon-java' : 'tree-icon-file'}`} />
        <div className="tree-file-info">
          <span className="tree-file-name">{node.name}</span>
          {node.description && (
            <span className="tree-file-desc">{node.description}</span>
          )}
        </div>
      </div>
    )
  }

  return (
    <div className="tree-folder-group">
      <button
        className="tree-item tree-folder"
        style={{ paddingLeft: `${depth * 20 + 12}px` }}
        onClick={() => setExpanded(!expanded)}
      >
        {expanded ? <ChevronDown size={14} /> : <ChevronRight size={14} />}
        {expanded ? <FolderOpen size={15} className="tree-icon-folder" /> : <Folder size={15} className="tree-icon-folder" />}
        <span className="tree-folder-name">{node.name}</span>
        {node.description && <span className="tree-folder-desc">{node.description}</span>}
      </button>
      {expanded && node.children && (
        <div className="tree-children">
          {node.children.map(child => (
            <TreeItem key={child.name} node={child} depth={depth + 1} />
          ))}
        </div>
      )}
    </div>
  )
}

export function StructureView() {
  return (
    <div className="structure-view">
      <div className="view-header">
        <h2>Project Structure</h2>
        <p className="view-header-sub">Selenium + Cucumber BDD automation framework layout</p>
      </div>

      <div className="structure-info-grid">
        <div className="info-card">
          <h4>Architecture Pattern</h4>
          <p>Page Object Model (POM) with cascading locator strategies using 7+ By types</p>
        </div>
        <div className="info-card">
          <h4>Wait Strategy</h4>
          <p>100% explicit waits via WebDriverWait -- no Thread.sleep() for test logic</p>
        </div>
        <div className="info-card">
          <h4>Locator Types Used</h4>
          <p>By.id, By.name, By.linkText, By.cssSelector, By.xpath, By.tagName, By.className</p>
        </div>
      </div>

      <div className="tree-container">
        <TreeItem node={projectTree} />
      </div>
    </div>
  )
}
