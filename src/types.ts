export interface TestRun {
  id: string
  run_name: string
  status: 'passed' | 'failed' | 'running' | 'pending'
  total_scenarios: number
  passed_scenarios: number
  failed_scenarios: number
  skipped_scenarios: number
  duration_ms: number
  browser: string
  environment: string
  triggered_by: string
  created_at: string
}

export interface TestScenario {
  id: string
  test_run_id: string
  scenario_name: string
  feature_name: string
  status: 'passed' | 'failed' | 'skipped'
  duration_ms: number
  error_message: string
  tags: string[]
  created_at: string
}

export type TabId = 'dashboard' | 'runs' | 'scenarios' | 'structure' | 'feature'
