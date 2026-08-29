import { useState, useEffect } from 'react';
import { api, ApiError } from '../../core/api';
import { PageHeader, StatusBadge, EmptyState, Spinner, ErrorBanner } from '../../components/common/UI';
import { colors, fonts } from '../../core/theme';
import type { Bundle } from '../../types';

export function RetailerInventoryPage() {
  const [bundles, setBundles] = useState<Bundle[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    api.get<Bundle[]>('/retailers/bundles')
      .then(setBundles)
      .catch((err) => setError(err instanceof ApiError ? err.message : 'Failed'))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div>
      <PageHeader title="Inventory" subtitle={`${bundles.length} bundle(s) in inventory`} />
      {error && <ErrorBanner message={error} />}
      {loading && <Spinner />}
      {!loading && bundles.length === 0 && <EmptyState title="No inventory" description="No bundles in inventory." />}
      {!loading && (
        <div className="agri-stagger">
          {bundles.map((b) => (
            <div key={b.bundleId} className="agri-card" style={{ padding: '14px 16px', marginBottom: 6 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <div>
                  <div style={{ fontSize: 14, fontWeight: 600, fontFamily: fonts.mono, color: colors.ink }}>{b.bundleId}</div>
                  <div style={{ fontSize: 12, color: colors.graphite, marginTop: 2 }}>{b.bundleType} • {b.quantity} {b.unit}</div>
                </div>
                <StatusBadge status={b.retailerReceived ? 'RECEIVED' : b.status} />
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
