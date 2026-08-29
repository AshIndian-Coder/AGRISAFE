import { useState, useEffect } from 'react';
import { api, ApiError } from '../../core/api';
import { PageHeader, StatusBadge, EmptyState, Spinner, ErrorBanner } from '../../components/common/UI';
import { colors, fonts } from '../../core/theme';
import type { Flag, PagedResponse } from '../../types';

export function GovernmentFlagsPage() {
  const [flags, setFlags] = useState<Flag[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    api.get<PagedResponse<Flag>>('/government/flags', { query: { page: '0', size: '100' } })
      .then((data) => setFlags(data.content || []))
      .catch((err) => setError(err instanceof ApiError ? err.message : 'Failed'))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div>
      <PageHeader title="Fraud Flags" subtitle={`${flags.length} flag(s)`} />
      {error && <ErrorBanner message={error} />}
      {loading && <Spinner />}
      {!loading && flags.length === 0 && <EmptyState title="No flags" description="No fraud flags have been raised." />}
      {!loading && (
        <div className="agri-stagger">
          {flags.map((f) => (
            <div key={f.id} className="agri-card" style={{ padding: '12px 16px', marginBottom: 6, cursor: 'default' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
                <div>
                  <div style={{ fontSize: 14, fontWeight: 600, color: colors.ink }}>Flag #{f.id} <span style={{ fontSize: 12, fontFamily: fonts.mono, color: colors.graphite }}>{f.flagType || f.type}</span></div>
                  {f.description && <div style={{ fontSize: 13, color: colors.graphite, marginTop: 4 }}>{f.description}</div>}
                  <div style={{ fontSize: 12, color: colors.graphite, marginTop: 2 }}>{f.entityType}:{f.entityId} • {f.createdAt ? new Date(f.createdAt).toLocaleString() : '—'}</div>
                </div>
                <div style={{ display: 'flex', gap: 4 }}>
                  {f.severity && <StatusBadge status={f.severity} />}
                  <StatusBadge status={f.status} />
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
