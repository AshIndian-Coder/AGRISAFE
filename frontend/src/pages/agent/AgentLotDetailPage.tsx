import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { api, ApiError } from '../../core/api';
import { useAuthStore } from '../../core/auth-store';
import { connectInAppWallet, farmerToSupplier } from '../../core/blockchain';
import { getRequestId, mapLotToRawBatch } from '../../core/blockchain-mapping';
import { PageHeader, StatusBadge, DataRow, Timeline, Spinner, ErrorBanner, Button } from '../../components/common/UI';
import { PinConfirm } from '../../components/pin/PinConfirm';
import { RotatingCodeDisplay } from '../../components/qr/RotatingCodeDisplay';
import { useGps } from '../../hooks/useGps';
import { colors } from '../../core/theme';
import type { Lot, TraceEvent } from '../../types';

export function AgentLotDetailPage() {
  const navigate = useNavigate();
  const { lotId } = useParams<{ lotId: string }>();
  const gps = useGps();
  const [lot, setLot] = useState<Lot | null>(null);
  const [trace, setTrace] = useState<TraceEvent[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showPin, setShowPin] = useState(false);
  const [accepting, setAccepting] = useState(false);
  const [acceptError, setAcceptError] = useState<string | null>(null);
  const [delivering, setDelivering] = useState(false);
  const [deliverError, setDeliverError] = useState<string | null>(null);

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

  const handleAccept = () => {
    gps.request();
    setShowPin(true);
  };

  const handlePinConfirm = async (_pin: string) => {
    if (!lotId) return;
    setAccepting(true);
    setAcceptError(null);
    try {
      await api.post(`/agents/lots/${lotId}/accept`, {
        query: {
          ...(gps.latitude ? { latitude: String(gps.latitude) } : {}),
          ...(gps.longitude ? { longitude: String(gps.longitude) } : {}),
        },
      });

      try {
        const walletAddr = useAuthStore.getState().walletAddress;
        if (walletAddr) {
          const account = await connectInAppWallet(useAuthStore.getState().walletEmail || '');
          if (account) {
            const requestId = getRequestId(lotId) ?? 0;
            const txResult = await farmerToSupplier(account, requestId, account.address);
            const chainRawBatchId = Math.abs(parseInt(txResult.transactionHash.slice(-8), 16)) % 100000;
            mapLotToRawBatch(lotId, chainRawBatchId);
          }
        }
      } catch {
        console.warn('Blockchain transfer failed — lot accepted on backend only');
      }

      setShowPin(false);
      navigate('/agent');
    } catch (err) {
      setShowPin(false);
      setAcceptError(err instanceof ApiError ? err.message : 'Accept failed');
    } finally {
      setAccepting(false);
    }
  };

  if (loading) return <Spinner text="Loading lot…" />;
  if (error) return <div><PageHeader title="Lot" back={() => navigate(-1)} /><ErrorBanner message={error} /></div>;
  if (!lot) return null;

  const steps = trace.map((t) => {
    const action = t.action || t.eventType || t.event || t.type || '';
    const label = action.replace(/_/g, ' ');
    const roleLabel = t.actorRole || t.role || '';
    const stateChange = t.previousState && t.newState
      ? `${t.previousState.replace(/_/g, ' ')} → ${t.newState.replace(/_/g, ' ')}` : '';
    const timestamp = t.eventTimestamp || t.createdAt || t.occurredAt;
    const timeStr = timestamp ? new Date(timestamp).toLocaleString() : '';
    const detail = [roleLabel, stateChange, timeStr].filter(Boolean).join(' • ');
    return { label, status: 'completed' as const, detail: detail || t.details };
  });

  const canAccept = lot.status === 'CREATED';
  const canDeliver = lot.status === 'ACCEPTED';

  return (
    <div>
      <PageHeader title="Lot Details" subtitle={lot.lotId} back={() => navigate(-1)} />

      <div style={{ backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`, borderRadius: 8, padding: 16, marginBottom: 16 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', marginBottom: 12 }}>
          <div style={{ fontSize: 18, fontWeight: 700, color: colors.ink, fontFamily: 'monospace' }}>{lot.lotId}</div>
          <StatusBadge status={lot.status} />
        </div>
        <DataRow label="Quantity" value={`${lot.quantity} ${lot.unit || 'units'}`} />
        <DataRow label="Created" value={lot.createdAt ? new Date(lot.createdAt).toLocaleString() : '—'} />
        {lot.qrId && <DataRow label="QR ID" value={lot.qrId} mono />}
      </div>

      {lot.qrId && <RotatingCodeDisplay qrId={lot.qrId} compact />}

      
      <div style={{
        padding: '10px 14px', marginBottom: 12, borderRadius: 8, fontSize: 13,
        backgroundColor: gps.status === 'ready' ? '#E8F5ED' : '#FFF4E5',
        color: gps.status === 'ready' ? colors.success : colors.warning,
      }}>
        GPS: {gps.status === 'ready' ? `Ready (${gps.accuracy?.toFixed(0)}m accuracy)` : gps.status === 'requesting' ? 'Requesting…' : gps.status === 'denied' ? 'Permission denied' : gps.status === 'unavailable' ? 'Unavailable' : gps.error || 'Not available'}
        {gps.status !== 'ready' && gps.status !== 'idle' && (
          <div style={{ fontSize: 12, marginTop: 2 }}>GPS is required for collection acceptance.</div>
        )}
      </div>

      {acceptError && <ErrorBanner message={acceptError} />}

      {canAccept && (
        <Button onClick={handleAccept} disabled={accepting} fullWidth style={{ marginBottom: 16 }}>
          Accept Lot (GPS + PIN required)
        </Button>
      )}

      {canDeliver && (
        <Button
          onClick={async () => {
            if (!lotId) return;
            setDelivering(true);
            setDeliverError(null);
            try {
              await api.post(`/agents/lots/${lotId}/deliver`, {
                query: {
                  ...(gps.latitude ? { latitude: String(gps.latitude) } : {}),
                  ...(gps.longitude ? { longitude: String(gps.longitude) } : {}),
                },
              });
              navigate('/agent');
            } catch (err) {
              setDeliverError(err instanceof ApiError ? err.message : 'Deliver failed');
            } finally {
              setDelivering(false);
            }
          }}
          disabled={delivering}
          fullWidth
          style={{ marginBottom: 16, backgroundColor: colors.info, borderColor: colors.info }}
        >
          {delivering ? 'Delivering…' : 'Deliver to Supplier'}
        </Button>
      )}

      {deliverError && <ErrorBanner message={deliverError} />}

      {steps.length > 0 && (
        <div style={{ backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`, borderRadius: 8, padding: 16 }}>
          <div style={{ fontSize: 13, fontWeight: 600, color: colors.ink, marginBottom: 8 }}>Traceability</div>
          <Timeline steps={steps} />
        </div>
      )}

      <PinConfirm
        open={showPin}
        title="Accept Lot"
        subtitle={`Accept lot ${lotId}. This requires GPS verification and your 6-digit PIN.`}
        onConfirm={handlePinConfirm}
        onCancel={() => setShowPin(false)}
        loading={accepting}
        error={acceptError}
      />
    </div>
  );
}
