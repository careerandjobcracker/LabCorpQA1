import { X, TriangleAlert as AlertTriangle, Tag } from 'lucide-react'
import type { TestRun, TestScenario } from '../types'
import { StatusBadge } from './StatusBadge'
import { formatDuration } from '../utils'
import './ScenariosView.css'

interface ScenariosViewProps {
  scenarios: TestScenario[]
  selectedRun: TestRun | null
  loading: boolean
  onClearFilter: () => void
}

export function ScenariosView({ scenarios, selectedRun, loading, onClearFilter }: ScenariosViewProps) {
  if (loading) {
    return <div className="loading-state">Loading scenarios...</div>
  }

  return (
    <div className="scenarios-view">
      <div className="view-header">
        <div>
          <h2>Test Scenarios</h2>
          <p className="view-header-sub">
            {selectedRun
              ? `Showing scenarios for: ${selectedRun.run_name}`
              : `${scenarios.length} scenarios across all runs`}
          </p>
        </div>
        {selectedRun && (
          <button className="clear-filter-btn" onClick={onClearFilter}>
            <X size={14} />
            <span>Clear filter</span>
          </button>
        )}
      </div>

      <div className="scenarios-list">
        {scenarios.length === 0 ? (
          <div className="empty-state">
            <p>No scenarios found</p>
          </div>
        ) : (
          scenarios.map(scenario => (
            <div key={scenario.id} className={`scenario-card scenario-${scenario.status}`}>
              <div className="scenario-card-top">
                <div className="scenario-card-info">
                  <p className="scenario-name">{scenario.scenario_name}</p>
                  <p className="scenario-feature">{scenario.feature_name}</p>
                </div>
                <div className="scenario-card-right">
                  <span className="scenario-duration">{formatDuration(scenario.duration_ms)}</span>
                  <StatusBadge status={scenario.status} size="sm" />
                </div>
              </div>

              {scenario.tags.length > 0 && (
                <div className="scenario-tags">
                  <Tag size={12} />
                  {scenario.tags.map(tag => (
                    <span key={tag} className="scenario-tag">@{tag}</span>
                  ))}
                </div>
              )}

              {scenario.error_message && (
                <div className="scenario-error">
                  <AlertTriangle size={14} />
                  <pre>{scenario.error_message}</pre>
                </div>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  )
}
