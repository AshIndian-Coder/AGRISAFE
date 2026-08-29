import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, ApiError } from '../../core/api';
import { PageHeader, StatusBadge, EmptyState, ErrorBanner, Button } from '../../components/common/UI';
import { SkeletonList } from '../../components/common/Skeleton';
import { colors, fonts } from '../../core/theme';
import type { ManufacturerLot } from '../../types';

export function ManufacturerDashboard() {
  const navigate = useNavigate();
  const [lots, setLots] = useState<ManufacturerLot[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchLots = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.get<ManufacturerLot[]>('/manufacturers/lots');
      setLots(data || []);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed to load lots');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchLots(); }, []);

  return (
    <div>
      <PageHeader title="Manufacturing" subtitle="Manufacturing lots and bundles" />

      <div style={{ display: 'flex', gap: 8, marginBottom: 16 }}>
        <Button onClick={() => navigate('/manufacturer/lots/new')}>Create Manufacturing Lot</Button>
      </div>

      {error && <ErrorBanner message={error} onRetry={fetchLots} />}
      {loading && <SkeletonList count={5} />}

      {!loading && !error && lots.length === 0 && (
        <EmptyState title="No manufacturing lots" description="Create a manufacturing lot from received packages." />
      )}

      {!loading && (
        <div className="agri-stagger">
          {lots.map((lot) => (
            <div
              key={lot.manufacturerLotId}
              onClick={() => navigate(`/manufacturer/lots/${lot.manufacturerLotId}`)}
              className="agri-card"
              style={{ padding: '14px 16px', marginBottom: 6 }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
                <div>
                  <div style={{ fontSize: 14, fontWeight: 600, fontFamily: fonts.mono, color: colors.ink }}>{lot.manufacturerLotId}</div>
                  <div style={{ fontSize: 12, color: colors.graphite, marginTop: 2 }}>
                    {lot.productionQuantity} {lot.unit || 'units'}
                    {lot.facilityName && ` • ${lot.facilityName}`}
                  </div>
                  <div style={{ fontSize: 12, color: colors.graphite, marginTop: 2 }}>
                    Bundles: {lot.bundles?.length || 0}
                  </div>
                </div>
                <StatusBadge status={lot.status} />
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
