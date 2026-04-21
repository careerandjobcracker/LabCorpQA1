import { LayoutDashboard, Play, ListChecks, FolderTree, FileText } from 'lucide-react'
import type { TabId } from '../types'
import './Sidebar.css'

interface SidebarProps {
  activeTab: TabId
  onTabChange: (tab: TabId) => void
  isOpen: boolean
}

const navItems: { id: TabId; label: string; icon: typeof LayoutDashboard }[] = [
  { id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { id: 'runs', label: 'Test Runs', icon: Play },
  { id: 'scenarios', label: 'Scenarios', icon: ListChecks },
  { id: 'structure', label: 'Project Structure', icon: FolderTree },
  { id: 'feature', label: 'Feature File', icon: FileText },
]

export function Sidebar({ activeTab, onTabChange, isOpen }: SidebarProps) {
  return (
    <aside className={`sidebar ${isOpen ? 'open' : 'closed'}`}>
      <div className="sidebar-logo">
        <div className="sidebar-logo-mark">LC</div>
        <span className="sidebar-logo-text">QA Dashboard</span>
      </div>
      <nav className="sidebar-nav">
        {navItems.map(item => (
          <button
            key={item.id}
            className={`sidebar-nav-item ${activeTab === item.id ? 'active' : ''}`}
            onClick={() => onTabChange(item.id)}
          >
            <item.icon size={18} />
            <span>{item.label}</span>
          </button>
        ))}
      </nav>
      <div className="sidebar-footer">
        <div className="sidebar-tech-stack">
          <p className="sidebar-footer-label">Technology Stack</p>
          <div className="sidebar-tech-tags">
            <span className="tech-tag">Java 11</span>
            <span className="tech-tag">Selenium 4.18</span>
            <span className="tech-tag">Cucumber 7.15</span>
            <span className="tech-tag">JUnit</span>
            <span className="tech-tag">Maven</span>
            <span className="tech-tag">Chrome</span>
          </div>
        </div>
      </div>
    </aside>
  )
}
