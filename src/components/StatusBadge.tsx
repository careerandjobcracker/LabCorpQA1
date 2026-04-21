import { CheckCircle2, XCircle, SkipForward, Clock, Loader2 } from 'lucide-react'
import './StatusBadge.css'

interface StatusBadgeProps {
  status: string
  size?: 'sm' | 'md'
}

const config: Record<string, { label: string; className: string; icon: typeof CheckCircle2 }> = {
  passed: { label: 'Passed', className: 'status-passed', icon: CheckCircle2 },
  failed: { label: 'Failed', className: 'status-failed', icon: XCircle },
  skipped: { label: 'Skipped', className: 'status-skipped', icon: SkipForward },
  running: { label: 'Running', className: 'status-running', icon: Loader2 },
  pending: { label: 'Pending', className: 'status-pending', icon: Clock },
}

export function StatusBadge({ status, size = 'md' }: StatusBadgeProps) {
  const c = config[status] ?? config.pending
  const Icon = c.icon
  return (
    <span className={`status-badge ${c.className} status-${size}`}>
      <Icon size={size === 'sm' ? 12 : 14} />
      <span>{c.label}</span>
    </span>
  )
}
