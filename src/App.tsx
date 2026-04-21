import { useState, useEffect } from 'react'
import { supabase } from './lib/supabase'
import type { TestRun, TestScenario, TabId } from './types'
import { Header } from './components/Header'
import { Sidebar } from './components/Sidebar'
import { DashboardView } from './components/DashboardView'
import { RunsView } from './components/RunsView'
import { ScenariosView } from './components/ScenariosView'
import { StructureView } from './components/StructureView'
import { FeatureView } from './components/FeatureView'
import './App.css'

export default function App() {
  const [activeTab, setActiveTab] = useState<TabId>('dashboard')
  const [runs, setRuns] = useState<TestRun[]>([])
  const [scenarios, setScenarios] = useState<TestScenario[]>([])
  const [selectedRunId, setSelectedRunId] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)
  const [sidebarOpen, setSidebarOpen] = useState(true)

  useEffect(() => {
    fetchData()
  }, [])

  async function fetchData() {
    setLoading(true)
    const [runsRes, scenariosRes] = await Promise.all([
      supabase.from('test_runs').select('*').order('created_at', { ascending: false }),
      supabase.from('test_scenarios').select('*').order('created_at', { ascending: false }),
    ])
    if (runsRes.data) setRuns(runsRes.data)
    if (scenariosRes.data) setScenarios(scenariosRes.data)
    setLoading(false)
  }

  function handleViewRunDetails(runId: string) {
    setSelectedRunId(runId)
    setActiveTab('scenarios')
  }

  const filteredScenarios = selectedRunId
    ? scenarios.filter(s => s.test_run_id === selectedRunId)
    : scenarios

  const selectedRun = selectedRunId
    ? runs.find(r => r.id === selectedRunId) ?? null
    : null

  return (
    <div className="app-layout">
      <Sidebar
        activeTab={activeTab}
        onTabChange={(tab) => {
          setActiveTab(tab)
          if (tab !== 'scenarios') setSelectedRunId(null)
        }}
        isOpen={sidebarOpen}
      />
      <div className={`main-content ${sidebarOpen ? '' : 'sidebar-collapsed'}`}>
        <Header
          onToggleSidebar={() => setSidebarOpen(!sidebarOpen)}
          onRefresh={fetchData}
          loading={loading}
        />
        <main className="content-area">
          {activeTab === 'dashboard' && (
            <DashboardView
              runs={runs}
              scenarios={scenarios}
              loading={loading}
              onViewRun={handleViewRunDetails}
            />
          )}
          {activeTab === 'runs' && (
            <RunsView
              runs={runs}
              loading={loading}
              onViewRun={handleViewRunDetails}
            />
          )}
          {activeTab === 'scenarios' && (
            <ScenariosView
              scenarios={filteredScenarios}
              selectedRun={selectedRun}
              loading={loading}
              onClearFilter={() => setSelectedRunId(null)}
            />
          )}
          {activeTab === 'structure' && <StructureView />}
          {activeTab === 'feature' && <FeatureView />}
        </main>
      </div>
    </div>
  )
}
