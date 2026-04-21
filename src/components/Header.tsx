import { Menu, RefreshCw, FlaskConical } from 'lucide-react'
import './Header.css'

interface HeaderProps {
  onToggleSidebar: () => void
  onRefresh: () => void
  loading: boolean
}

export function Header({ onToggleSidebar, onRefresh, loading }: HeaderProps) {
  return (
    <header className="header">
      <div className="header-left">
        <button className="header-menu-btn" onClick={onToggleSidebar} aria-label="Toggle sidebar">
          <Menu size={20} />
        </button>
        <div className="header-brand">
          <FlaskConical size={22} className="header-brand-icon" />
          <div>
            <h1 className="header-title">LabCorp QA Automation</h1>
            <p className="header-subtitle">Selenium + Cucumber BDD Dashboard</p>
          </div>
        </div>
      </div>
      <div className="header-right">
        <span className="header-env-badge">QA Environment</span>
        <button
          className={`header-refresh-btn ${loading ? 'spinning' : ''}`}
          onClick={onRefresh}
          aria-label="Refresh data"
          disabled={loading}
        >
          <RefreshCw size={16} />
          <span>Refresh</span>
        </button>
      </div>
    </header>
  )
}
