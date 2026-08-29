import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, ApiError } from '../../core/api';
import { PageHeader, StatusBadge, EmptyState, Spinner, ErrorBanner } from '../../components/common/UI';
import { colors, fonts } from '../../core/theme';
import type { Lot, PagedResponse } from '../../types';

export function FarmerLotsPage() {
  const navigate = useNavigate();
  const [lots, setLots] = useState<Lot[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchLots = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.get<PagedResponse<Lot>>('/farmer/lots', { query: { page: '0', size: '50' } });
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
      <PageHeader title="My Lots" subtitle={`${lots.length} lot(s) registered`} back={() => navigate('/farmer')} />

      {error && <ErrorBanner message={error} onRetry={fetchLots} />}
      {loading && <Spinner text="Loading lots…" />}
      {!loading && !error && lots.length === 0 && (
        <EmptyState title="No lots" description="Create your first lot to begin." />
      )}

      {!loading && (
        <div className="agri-stagger">
          {lots.map((lot) => (
            <div
              key={lot.lotId}
              onClick={() => navigate(`/farmer/lots/${lot.lotId}`)}
              className="agri-card"
              style={{ padding: '14px 16px', marginBottom: 6 }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
                <div>
                  <div style={{ fontSize: 14, fontWeight: 600, color: colors.ink, fontFamily: fonts.mono }}>{lot.lotId}</div>
                  <div style={{ fontSize: 12, color: colors.graphite, marginTop: 2 }}>
                    {lot.quantity} {lot.unit || 'units'}
                    {lot.originAddress && ` • ${lot.originAddress}`}
                  </div>
                  <div style={{ fontSize: 12, color: colors.graphite, marginTop: 2 }}>
                    {lot.createdAt ? new Date(lot.createdAt).toLocaleDateString() : '—'}
                  </div>
                </div>
                <StatusBadge status={lot.status} />
              </div>
              {lot.recalled && (
                <div style={{ fontSize: 11, color: colors.danger, marginTop: 6, fontWeight: 600 }}>RECALLED</div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
