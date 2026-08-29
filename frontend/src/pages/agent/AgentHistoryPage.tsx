import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, ApiError } from '../../core/api';
import { PageHeader, StatusBadge, EmptyState, Spinner, ErrorBanner } from '../../components/common/UI';
import { colors, fonts } from '../../core/theme';
import type { Lot, PagedResponse } from '../../types';

export function AgentHistoryPage() {
  const navigate = useNavigate();
  const [lots, setLots] = useState<Lot[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    api.get<PagedResponse<Lot>>('/agents/lots/available', { query: { page: '0', size: '50' } })
      .then((data) => setLots((data.content || []).filter((l) => l.status !== 'CREATED')))
      .catch((err) => setError(err instanceof ApiError ? err.message : 'Failed'))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div>
      <PageHeader title="Collection History" back={() => navigate('/agent')} />
      {error && <ErrorBanner message={error} />}
      {loading && <Spinner />}
      {!loading && lots.length === 0 && <EmptyState title="No history" description="No collection records yet." />}
      {!loading && (
        <div className="agri-stagger">
          {lots.map((lot) => (
            <div
              key={lot.lotId}
              onClick={() => navigate(`/agent/lots/${lot.lotId}`)}
              className="agri-card"
              style={{ padding: '12px 16px', marginBottom: 6 }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ fontSize: 14, fontWeight: 600, fontFamily: fonts.mono, color: colors.ink }}>{lot.lotId}</span>
                <StatusBadge status={lot.status} />
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
