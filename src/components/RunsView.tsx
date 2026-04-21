import { Eye, Clock, Monitor, Globe, User } from 'lucide-react'
import type { TestRun } from '../types'
import { StatusBadge } from './StatusBadge'
import { formatDuration, formatTimeAgo } from '../utils'
import './RunsView.css'

interface RunsViewProps {
  runs: TestRun[]
  loading: boolean
  onViewRun: (runId: string) => void
}

export function RunsView({ runs, loading, onViewRun }: RunsViewProps) {
  if (loading) {
    return <div className="loading-state">Loading test runs...</div>
  }

  return (
    <div className="runs-view">
      <div className="view-header">
        <h2>Test Runs</h2>
        <p className="view-header-sub">{runs.length} runs recorded</p>
      </div>

      <div className="runs-table-wrapper">
        <table className="runs-table">
          <thead>
            <tr>
              <th>Run Name</th>
              <th>Status</th>
              <th>Scenarios</th>
              <th>Duration</th>
              <th>Browser</th>
              <th>Environment</th>
              <th>Triggered By</th>
              <th>Time</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {runs.map(run => (
              <tr key={run.id}>
                <td>
                  <span className="run-name-cell">{run.run_name}</span>
                </td>
                <td>
                  <StatusBadge status={run.status} size="sm" />
                </td>
                <td>
                  <div className="scenario-counts">
                    <span className="count-passed">{run.passed_scenarios}</span>
                    <span className="count-separator">/</span>
                    {run.failed_scenarios > 0 && (
                      <>
                        <span className="count-failed">{run.failed_scenarios}</span>
                        <span className="count-separator">/</span>
                      </>
                    )}
                    <span className="count-total">{run.total_scenarios}</span>
                  </div>
                </td>
                <td>
                  <span className="cell-with-icon">
                    <Clock size={13} />
                    {formatDuration(run.duration_ms)}
                  </span>
                </td>
                <td>
                  <span className="cell-with-icon">
                    <Monitor size={13} />
                    {run.browser}
                  </span>
                </td>
                <td>
                  <span className="cell-with-icon">
                    <Globe size={13} />
                    {run.environment}
                  </span>
                </td>
                <td>
                  <span className="cell-with-icon">
                    <User size={13} />
                    {run.triggered_by}
                  </span>
                </td>
                <td>
                  <span className="time-cell">{formatTimeAgo(run.created_at)}</span>
                </td>
                <td>
                  <button className="table-action-btn" onClick={() => onViewRun(run.id)}>
                    <Eye size={14} />
                    <span>View</span>
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
