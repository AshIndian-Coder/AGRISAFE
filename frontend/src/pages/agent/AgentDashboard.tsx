import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, ApiError } from '../../core/api';
import { PageHeader, StatusBadge, EmptyState, ErrorBanner } from '../../components/common/UI';
import { SkeletonList } from '../../components/common/Skeleton';
import { colors, fonts } from '../../core/theme';
import type { Lot, PagedResponse } from '../../types';

export function AgentDashboard() {
  const navigate = useNavigate();
  const [lots, setLots] = useState<Lot[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchLots = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.get<PagedResponse<Lot>>('/agents/lots/available', { query: { page: '0', size: '20' } });
      setLots(data.content || []);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed to load lots');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchLots(); }, []);

  return (
    <div>
      <PageHeader title="Collection Agent" subtitle="Lots assigned for collection" />

      {error && <ErrorBanner message={error} onRetry={fetchLots} />}
      {loading && <SkeletonList count={5} />}

      {!loading && !error && lots.length === 0 && (
        <EmptyState title="No lots assigned" description="No lots are currently assigned for collection." />
      )}

      {!loading && lots.length > 0 && (
        <div>
          <div style={{
            fontSize: 13, fontWeight: 600, color: colors.graphite, textTransform: 'uppercase',
            letterSpacing: '0.05em', marginBottom: 8,
          }}>
            Available Lots ({lots.length})
          </div>
          <div className="agri-stagger">
            {lots.map((lot) => (
              <div
                key={lot.lotId}
                onClick={() => navigate(`/agent/lots/${lot.lotId}`)}
                className="agri-card"
                style={{ padding: '14px 16px', marginBottom: 6 }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
                  <div>
                    <div style={{ fontSize: 14, fontWeight: 600, color: colors.ink, fontFamily: fonts.mono }}>{lot.lotId}</div>
                    <div style={{ fontSize: 12, color: colors.graphite, marginTop: 2 }}>
                      {lot.quantity} {lot.unit || 'units'} • {lot.createdAt ? new Date(lot.createdAt).toLocaleDateString() : '—'}
                    </div>
                  </div>
                  <StatusBadge status={lot.status} />
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
