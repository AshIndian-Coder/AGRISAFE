import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, ApiError } from '../../core/api';
import { useAuthStore } from '../../core/auth-store';
import { PageHeader, StatusBadge, EmptyState, ErrorBanner, Button } from '../../components/common/UI';
import { SkeletonStatCard, SkeletonList } from '../../components/common/Skeleton';
import { colors, fonts } from '../../core/theme';
import type { Lot, PagedResponse } from '../../types';

export function FarmerDashboard() {
  const navigate = useNavigate();
  const userName = useAuthStore((s) => s.userName);
  const [lots, setLots] = useState<Lot[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchLots = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.get<PagedResponse<Lot>>('/farmer/lots', { query: { page: '0', size: '20' } });
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
      <PageHeader title={`Welcome, ${userName?.split(' ')[0] || 'Farmer'}`} subtitle="Your agricultural lots and traceability records" />

      
      <div className="agri-stagger" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(140px, 1fr))', gap: 12, marginBottom: 20 }}>
        <StatCard label="Total Lots" value={lots.length} />
        <StatCard label="Created" value={lots.filter((l) => l.status === 'CREATED').length} />
        <StatCard label="Accepted" value={lots.filter((l) => l.status === 'ACCEPTED').length} />
      </div>

      
      <div style={{ display: 'flex', gap: 8, marginBottom: 20 }}>
        <Button onClick={() => navigate('/farmer/lots/new')}>Create Lot</Button>
        <Button variant="secondary" onClick={() => navigate('/farmer/lots')}>View All Lots</Button>
      </div>

      {error && <ErrorBanner message={error} onRetry={fetchLots} />}
      {loading && (
        <>
          <div className="agri-stagger" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(140px, 1fr))', gap: 12, marginBottom: 20 }}>
            <SkeletonStatCard /><SkeletonStatCard /><SkeletonStatCard />
          </div>
          <SkeletonList count={5} />
        </>
      )}

      {!loading && !error && lots.length === 0 && (
        <EmptyState title="No lots yet" description="Create your first agricultural lot to begin traceability." />
      )}

      {!loading && lots.length > 0 && (
        <div>
          <div style={{
            fontSize: 13, fontWeight: 600, color: colors.graphite, textTransform: 'uppercase',
            letterSpacing: '0.05em', marginBottom: 8,
          }}>
            Recent Lots
          </div>
          <div className="agri-stagger">
            {lots.slice(0, 5).map((lot) => (
              <div
                key={lot.lotId}
                onClick={() => navigate(`/farmer/lots/${lot.lotId}`)}
                className="agri-card"
                style={{
                  display: 'flex', alignItems: 'center', justifyContent: 'space-between',
                  padding: '14px 16px', marginBottom: 6,
                }}
              >
                <div>
                  <div style={{ fontSize: 14, fontWeight: 600, color: colors.ink, fontFamily: fonts.mono }}>{lot.lotId}</div>
                  <div style={{ fontSize: 12, color: colors.graphite, marginTop: 2 }}>
                    {lot.quantity} {lot.unit || 'units'} • {lot.createdAt ? new Date(lot.createdAt).toLocaleDateString() : '—'}
                  </div>
                </div>
                <StatusBadge status={lot.status} />
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

function StatCard({ label, value }: { label: string; value: number }) {
  return (
    <div className="agri-stat-card">
      <div style={{
        fontSize: 26, fontWeight: 700, color: colors.forest, fontFamily: fonts.heading,
        lineHeight: 1,
      }}>
        {value}
      </div>
      <div style={{ fontSize: 12, color: colors.graphite, marginTop: 6, fontWeight: 500 }}>{label}</div>
    </div>
  );
}
