import { CheckCircle2, XCircle, SkipForward, Clock, TrendingUp, Eye } from 'lucide-react'
import type { TestRun, TestScenario } from '../types'
import { StatusBadge } from './StatusBadge'
import { formatDuration, formatTimeAgo } from '../utils'
import './DashboardView.css'

interface DashboardViewProps {
  runs: TestRun[]
  scenarios: TestScenario[]
  loading: boolean
  onViewRun: (runId: string) => void
}

export function DashboardView({ runs, scenarios, loading, onViewRun }: DashboardViewProps) {
  if (loading) {
    return <div className="loading-state">Loading dashboard data...</div>
  }

  const totalPassed = scenarios.filter(s => s.status === 'passed').length
  const totalFailed = scenarios.filter(s => s.status === 'failed').length
  const totalSkipped = scenarios.filter(s => s.status === 'skipped').length
  const passRate = scenarios.length > 0
    ? Math.round((totalPassed / scenarios.length) * 100)
    : 0

  const recentRuns = runs.slice(0, 5)

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h2>Overview</h2>
        <p className="dashboard-header-sub">LabCorp Careers Portal - QA Test Automation Status</p>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon stat-icon-success">
            <CheckCircle2 size={20} />
          </div>
          <div className="stat-content">
            <p className="stat-value">{totalPassed}</p>
            <p className="stat-label">Passed</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon stat-icon-error">
            <XCircle size={20} />
          </div>
          <div className="stat-content">
            <p className="stat-value">{totalFailed}</p>
            <p className="stat-label">Failed</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon stat-icon-warning">
            <SkipForward size={20} />
          </div>
          <div className="stat-content">
            <p className="stat-value">{totalSkipped}</p>
            <p className="stat-label">Skipped</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon stat-icon-primary">
            <TrendingUp size={20} />
          </div>
          <div className="stat-content">
            <p className="stat-value">{passRate}%</p>
            <p className="stat-label">Pass Rate</p>
          </div>
        </div>
      </div>

      <div className="dashboard-grid">
        <div className="card">
          <div className="card-header">
            <h3>Recent Test Runs</h3>
            <span className="card-count">{runs.length} total</span>
          </div>
          <div className="card-body">
            {recentRuns.length === 0 ? (
              <div className="empty-state">
                <Clock size={32} />
                <p>No test runs yet</p>
              </div>
            ) : (
              <div className="run-list">
                {recentRuns.map(run => (
                  <div key={run.id} className="run-list-item">
                    <div className="run-list-info">
                      <p className="run-list-name">{run.run_name}</p>
                      <div className="run-list-meta">
                        <span>{run.browser}</span>
                        <span className="meta-dot">-</span>
                        <span>{run.environment}</span>
                        <span className="meta-dot">-</span>
                        <span>{formatTimeAgo(run.created_at)}</span>
                      </div>
                    </div>
                    <div className="run-list-right">
                      <StatusBadge status={run.status} size="sm" />
                      <button className="view-btn" onClick={() => onViewRun(run.id)}>
                        <Eye size={14} />
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        <div className="card">
          <div className="card-header">
            <h3>Pass Rate Breakdown</h3>
          </div>
          <div className="card-body">
            <div className="pass-rate-visual">
              <div className="pass-rate-bar">
                <div
                  className="pass-rate-fill pass-rate-passed"
                  style={{ width: `${scenarios.length ? (totalPassed / scenarios.length) * 100 : 0}%` }}
                />
                <div
                  className="pass-rate-fill pass-rate-failed"
                  style={{ width: `${scenarios.length ? (totalFailed / scenarios.length) * 100 : 0}%` }}
                />
                <div
                  className="pass-rate-fill pass-rate-skipped"
                  style={{ width: `${scenarios.length ? (totalSkipped / scenarios.length) * 100 : 0}%` }}
                />
              </div>
              <div className="pass-rate-legend">
                <div className="legend-item">
                  <span className="legend-dot legend-dot-passed" />
                  <span>Passed ({totalPassed})</span>
                </div>
                <div className="legend-item">
                  <span className="legend-dot legend-dot-failed" />
                  <span>Failed ({totalFailed})</span>
                </div>
                <div className="legend-item">
                  <span className="legend-dot legend-dot-skipped" />
                  <span>Skipped ({totalSkipped})</span>
                </div>
              </div>
            </div>

            <div className="run-duration-list">
              <p className="section-label">Run Durations</p>
              {recentRuns.map(run => (
                <div key={run.id} className="duration-row">
                  <span className="duration-name">{run.run_name}</span>
                  <span className="duration-value">{formatDuration(run.duration_ms)}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
