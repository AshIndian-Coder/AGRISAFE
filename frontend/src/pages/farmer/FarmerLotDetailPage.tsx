import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { api, ApiError } from '../../core/api';
import { useAuthStore } from '../../core/auth-store';
import { PageHeader, StatusBadge, DataRow, Timeline, Spinner, ErrorBanner } from '../../components/common/UI';
import { RotatingCodeDisplay } from '../../components/qr/RotatingCodeDisplay';
import { colors, fonts } from '../../core/theme';
import type { Lot, TraceEvent } from '../../types';

const ACTION_LABELS: Record<string, string> = {
  LOT_CREATED: 'Lot created by farmer',
  LOT_ACCEPTED: 'Lot accepted by collecting agent',
  LOT_DELETED: 'Lot deleted',
  CUSTODY_TRANSFERRED: 'Custody transferred',
  QR_SCANNED: 'QR code scanned',
  QR_CONSUMED: 'QR code consumed',
  PACKAGE_SPLIT: 'Lot split into packages',
  PACKAGE_VERIFIED: 'Package verified at nodal center',
  TEST_PERFORMED: 'Lab test performed',
  TEST_PASSED: 'Lab test passed',
  TEST_FAILED: 'Lab test failed',
  MANUFACTURER_LOT_CREATED: 'Manufacturing lot created',
  BUNDLE_CREATED: 'Product bundles created',
  DISTRIBUTOR_RECEIVED: 'Distributor received bundle',
  RETAILER_RECEIVED: 'Retailer received bundle',
  CONSUMER_VERIFIED: 'Consumer verified product',
};

const ROLE_LABELS: Record<string, string> = {
  FARMER: 'Farmer',
  COLLECTING_AGENT: 'Collecting Agent',
  SUPPLIER: 'Supplier',
  NODAL_CENTER_AGENT: 'Nodal Center Agent',
  TESTING_AGENT: 'Testing Agent',
  MANUFACTURER_EMPLOYEE: 'Manufacturer',
  DISTRIBUTOR_EMPLOYEE: 'Distributor',
  RETAILER: 'Retailer',
  CONSUMER: 'Consumer',
};

export function FarmerLotDetailPage() {
  const navigate = useNavigate();
  const { lotId } = useParams<{ lotId: string }>();
  const [lot, setLot] = useState<Lot | null>(null);
  const [trace, setTrace] = useState<TraceEvent[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!lotId) return;
    setLoading(true);
    const viewerUuid = useAuthStore.getState().userUuid;
    Promise.all([
      api.get<Lot>(`/lots/${lotId}`),
      api.get<TraceEvent[]>(`/lots/${lotId}/trace`, { query: viewerUuid ? { viewerUuid } : {} }).catch(() => []),
    ])
      .then(([l, t]) => { setLot(l); setTrace(t || []); })
      .catch((err) => setError(err instanceof ApiError ? err.message : 'Failed to load lot'))
      .finally(() => setLoading(false));
  }, [lotId]);

  if (loading) return <Spinner text="Loading lot…" />;
  if (error) return <div><PageHeader title="Lot" back={() => navigate(-1)} /><ErrorBanner message={error} /></div>;
  if (!lot) return null;

  const steps = trace.map((t) => {
    const action = t.action || t.eventType || t.event || t.type || '';
    const label = ACTION_LABELS[action] || action.replace(/_/g, ' ');
    const roleLabel = ROLE_LABELS[t.actorRole || t.role || ''] || t.actorRole || '';
    const stateChange = t.previousState && t.newState
      ? `${t.previousState.replace(/_/g, ' ')} → ${t.newState.replace(/_/g, ' ')}`
      : '';
    const location = t.latitude && t.longitude
      ? `📍 ${Number(t.latitude).toFixed(4)}, ${Number(t.longitude).toFixed(4)}`
      : '';
    const timestamp = t.eventTimestamp || t.createdAt || t.occurredAt;
    const timeStr = timestamp ? new Date(timestamp).toLocaleString() : '';
    const detail = [roleLabel, stateChange, location, timeStr].filter(Boolean).join(' • ');
    return { label, status: 'completed' as const, detail: detail || t.details };
  });

  return (
    <div>
      <PageHeader title="Lot Details" subtitle={lot.lotId} back={() => navigate(-1)} />

      <div style={{ backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`, borderRadius: 8, padding: 16, marginBottom: 16 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', marginBottom: 12 }}>
          <div style={{ fontSize: 18, fontWeight: 700, color: colors.ink, fontFamily: fonts.mono }}>{lot.lotId}</div>
          <StatusBadge status={lot.status} />
        </div>
        <DataRow label="Quantity" value={`${lot.quantity} ${lot.unit || 'units'}`} />
        <DataRow label="Status" value={lot.status.replace(/_/g, ' ')} />
        <DataRow label="Created" value={lot.createdAt ? new Date(lot.createdAt).toLocaleString() : '—'} />
        {lot.acceptedAt && <DataRow label="Accepted" value={new Date(lot.acceptedAt).toLocaleString()} />}
        {lot.originAddress && <DataRow label="Origin" value={lot.originAddress} />}
        {lot.estimatedValue && <DataRow label="Est. Value" value={`₹${lot.estimatedValue}`} />}
        {lot.qrId && <DataRow label="QR ID" value={lot.qrId} mono />}
        {lot.recalled && <DataRow label="Status" value={<span style={{ color: colors.danger, fontWeight: 600 }}>RECALLED</span>} />}
      </div>

      
      {lot.qrId && (
        <div style={{ backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`, borderRadius: 8, padding: 16, marginBottom: 16, textAlign: 'center' }}>
          <div style={{ fontSize: 13, fontWeight: 600, color: colors.ink, marginBottom: 8 }}>Origin QR Code</div>
          <img
            src={`/api/v1/qr/${lot.qrId}/image`}
            alt={`QR for ${lot.lotId}`}
            style={{ width: 180, height: 180, border: `1px solid ${colors.stoneBorder}`, borderRadius: 4 }}
            onError={(e) => { (e.target as HTMLImageElement).style.display = 'none'; }}
          />
          <div style={{ fontSize: 11, color: colors.graphite, marginTop: 6, fontFamily: fonts.mono }}>{lot.qrId}</div>
        </div>
      )}

      {lot.qrId && <RotatingCodeDisplay qrId={lot.qrId} />}

      
      {steps.length > 0 && (
        <div style={{ backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`, borderRadius: 8, padding: 16 }}>
          <div style={{ fontSize: 13, fontWeight: 600, color: colors.ink, marginBottom: 8 }}>Traceability</div>
          <Timeline steps={steps} />
        </div>
      )}
    </div>
  );
}
