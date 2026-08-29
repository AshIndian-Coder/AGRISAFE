import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, ApiError } from '../../core/api';
import { PageHeader, StatusBadge, EmptyState, Spinner, ErrorBanner } from '../../components/common/UI';
import { colors, fonts } from '../../core/theme';
import type { ManufacturerLot } from '../../types';

export function ManufacturerLotsPage() {
  const navigate = useNavigate();
  const [lots, setLots] = useState<ManufacturerLot[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    api.get<ManufacturerLot[]>('/manufacturers/lots')
      .then(setLots)
      .catch((err) => setError(err instanceof ApiError ? err.message : 'Failed'))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div>
      <PageHeader title="Manufacturing Lots" back={() => navigate('/manufacturer')} />
      {error && <ErrorBanner message={error} />}
      {loading && <Spinner />}
      {!loading && lots.length === 0 && <EmptyState title="No lots" description="No manufacturing lots yet." />}
      {!loading && (
        <div className="agri-stagger">
          {lots.map((lot) => (
            <div key={lot.manufacturerLotId} onClick={() => navigate(`/manufacturer/lots/${lot.manufacturerLotId}`)} className="agri-card" style={{ padding: '14px 16px', marginBottom: 6 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <div>
                  <div style={{ fontSize: 14, fontWeight: 600, fontFamily: fonts.mono, color: colors.ink }}>{lot.manufacturerLotId}</div>
                  <div style={{ fontSize: 12, color: colors.graphite, marginTop: 2 }}>{lot.productionQuantity} {lot.unit || 'units'} • Bundles: {lot.bundles?.length || 0}</div>
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
