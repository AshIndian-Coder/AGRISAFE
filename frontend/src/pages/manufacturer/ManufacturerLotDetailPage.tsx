import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { api, ApiError } from '../../core/api';
import { PageHeader, StatusBadge, DataRow, Spinner, ErrorBanner, Button } from '../../components/common/UI';
import { PinConfirm } from '../../components/pin/PinConfirm';
import { RotatingCodeDisplay } from '../../components/qr/RotatingCodeDisplay';
import { colors, fonts } from '../../core/theme';
import type { ManufacturerLot } from '../../types';

export function ManufacturerLotDetailPage() {
  const navigate = useNavigate();
  const { lotId } = useParams<{ lotId: string }>();
  const [lot, setLot] = useState<ManufacturerLot | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showBundle, setShowBundle] = useState(false);
  const [bundleType, setBundleType] = useState('CARTON');
  const [bundleCount, setBundleCount] = useState('10');
  const [creating, setCreating] = useState(false);

  useEffect(() => {
    if (!lotId) return;
    setLoading(true);
    api.get<ManufacturerLot>(`/manufacturers/lots/${lotId}`)
      .then(setLot)
      .catch((err) => setError(err instanceof ApiError ? err.message : 'Failed'))
      .finally(() => setLoading(false));
  }, [lotId]);

  const handleCreateBundle = async (_pin: string) => {
    if (!lotId) return;
    setCreating(true);
    try {
      await api.post(`/manufacturers/lots/${lotId}/bundles`, {
        query: { bundleType, bundleCount },
      });
      setShowBundle(false);
      const updated = await api.get<ManufacturerLot>(`/manufacturers/lots/${lotId}`);
      setLot(updated);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Bundle creation failed');
    } finally {
      setCreating(false);
    }
  };

  if (loading) return <Spinner text="Loading…" />;
  if (error) return <div><PageHeader title="Manufacturing Lot" back={() => navigate(-1)} /><ErrorBanner message={error} /></div>;
  if (!lot) return null;

  return (
    <div>
      <PageHeader title="Manufacturing Lot" subtitle={lot.manufacturerLotId} back={() => navigate(-1)} />

      <div style={{
        backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`, borderRadius: 8,
        padding: 16, marginBottom: 16, animation: 'fadeInUp 0.3s ease-out both',
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
          <span style={{ fontSize: 18, fontWeight: 700, fontFamily: fonts.mono, color: colors.ink }}>{lot.manufacturerLotId}</span>
          <StatusBadge status={lot.status} />
        </div>
        <DataRow label="Quantity" value={`${lot.productionQuantity} ${lot.unit || 'units'}`} />
        <DataRow label="Facility" value={lot.facilityName || '—'} />
        <DataRow label="Input Lots" value={lot.inputLotIds?.join(', ') || '—'} mono />
        {lot.qrId && <DataRow label="QR ID" value={lot.qrId} mono />}
        <DataRow label="Created" value={lot.createdAt ? new Date(lot.createdAt).toLocaleString() : '—'} />
      </div>

      {lot.qrId && <RotatingCodeDisplay qrId={lot.qrId} compact />}

      
      {lot.status !== 'BUNDLED' && (
        <div style={{
          backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`, borderRadius: 8,
          padding: 16, marginBottom: 16, animation: 'fadeInUp 0.3s ease-out 0.1s both',
        }}>
          <div style={{ fontSize: 13, fontWeight: 600, color: colors.ink, marginBottom: 8 }}>Create Bundles</div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12, marginBottom: 12 }}>
            <div>
              <label style={{ fontSize: 13, fontWeight: 500, color: colors.ink, marginBottom: 4, display: 'block' }}>Bundle Type</label>
              <select value={bundleType} onChange={(e) => setBundleType(e.target.value)} className="agri-input"
                style={{ width: '100%', padding: '10px 12px', fontSize: 14, borderRadius: 8, border: `1px solid ${colors.stoneBorder}`, boxSizing: 'border-box' }}>
                <option value="CARTON">Carton</option>
                <option value="BOX">Box</option>
                <option value="PALLET">Pallet</option>
              </select>
            </div>
            <div>
              <label style={{ fontSize: 13, fontWeight: 500, color: colors.ink, marginBottom: 4, display: 'block' }}>Count</label>
              <input type="number" value={bundleCount} onChange={(e) => setBundleCount(e.target.value)} className="agri-input"
                style={{ width: '100%', padding: '10px 12px', fontSize: 14, borderRadius: 8, border: `1px solid ${colors.stoneBorder}`, boxSizing: 'border-box' }} />
            </div>
          </div>
          <Button onClick={() => setShowBundle(true)} fullWidth>Create Bundles (PIN required)</Button>
        </div>
      )}

      
      {lot.bundles && lot.bundles.length > 0 && (
        <div style={{
          backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`, borderRadius: 8, padding: 16,
          animation: 'fadeInUp 0.3s ease-out 0.15s both',
        }}>
          <div style={{ fontSize: 13, fontWeight: 600, color: colors.ink, marginBottom: 8 }}>Bundles ({lot.bundles.length})</div>
          <div className="agri-stagger">
            {lot.bundles.map((b) => (
              <div key={b.bundleId} style={{ padding: '10px 0', borderBottom: `1px solid ${colors.softPaper}` }}>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <div>
                    <span style={{ fontSize: 13, fontWeight: 600, fontFamily: fonts.mono, color: colors.ink }}>{b.bundleId}</span>
                    <span style={{ fontSize: 12, color: colors.graphite, marginLeft: 8 }}>{b.bundleType} • {b.quantity} {b.unit}</span>
                  </div>
                  <StatusBadge status={b.status} />
                </div>
                {b.qrId && <div style={{ fontSize: 11, color: colors.graphite, fontFamily: fonts.mono, marginTop: 4 }}>QR: {b.qrId}</div>}
              </div>
            ))}
          </div>
        </div>
      )}

      <PinConfirm
        open={showBundle}
        title="Create Bundles"
        subtitle={`Create ${bundleCount} ${bundleType} bundles. This generates QR codes for each bundle.`}
        onConfirm={handleCreateBundle}
        onCancel={() => setShowBundle(false)}
        loading={creating}
      />
    </div>
  );
}
