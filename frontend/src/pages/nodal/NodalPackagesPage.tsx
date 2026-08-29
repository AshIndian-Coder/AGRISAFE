import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, ApiError } from '../../core/api';
import { PageHeader, StatusBadge, EmptyState, Spinner, ErrorBanner, Button } from '../../components/common/UI';
import { colors, fonts } from '../../core/theme';
import type { Package } from '../../types';

export function NodalPackagesPage() {
  const navigate = useNavigate();
  const [lotId, setLotId] = useState('');
  const [packages, setPackages] = useState<Package[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const lookup = async () => {
    if (!lotId.trim()) return;
    setLoading(true);
    try {
      const data = await api.get<Package[]>(`/nodal-centers/lots/${lotId.trim()}/packages`);
      setPackages(data || []);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <PageHeader title="Packages" back={() => navigate('/nodal')} />
      <div style={{
        backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`, borderRadius: 8,
        padding: 16, marginBottom: 16, animation: 'fadeInUp 0.25s ease-out both',
      }}>
        <label style={{ fontSize: 13, fontWeight: 500, display: 'block', marginBottom: 4 }}>Lot ID</label>
        <div style={{ display: 'flex', gap: 8 }}>
          <input value={lotId} onChange={(e) => setLotId(e.target.value)} placeholder="LOT-XXXXXXXX" className="agri-input"
            style={{ flex: 1, padding: '10px 12px', fontSize: 14, borderRadius: 8, border: `1px solid ${colors.stoneBorder}`, fontFamily: fonts.mono, boxSizing: 'border-box' }} />
          <Button onClick={lookup} disabled={!lotId.trim() || loading}>Look Up</Button>
        </div>
      </div>
      {error && <ErrorBanner message={error} />}
      {loading && <Spinner />}
      {!loading && lotId && packages.length === 0 && <EmptyState title="No packages" description="No packages found for this lot." />}
      {!loading && (
        <div className="agri-stagger">
          {packages.map((pkg) => (
            <div key={pkg.packageId} className="agri-card" style={{ padding: '12px 16px', marginBottom: 6, cursor: 'default' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ fontSize: 14, fontWeight: 600, fontFamily: fonts.mono, color: colors.ink }}>{pkg.packageId}</span>
                <StatusBadge status={pkg.status} />
              </div>
              <div style={{ fontSize: 12, color: colors.graphite, marginTop: 2 }}>{pkg.quantity} {pkg.unit || 'units'} • {pkg.packageType || 'Standard'}</div>
              {pkg.qrId && <div style={{ fontSize: 11, color: colors.graphite, fontFamily: fonts.mono, marginTop: 2 }}>QR: {pkg.qrId}</div>}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
