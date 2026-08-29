import { useState, useEffect } from 'react';
import { api, ApiError } from '../../core/api';
import { PageHeader, StatusBadge, EmptyState, ErrorBanner, Button } from '../../components/common/UI';
import { SkeletonList } from '../../components/common/Skeleton';
import { PinConfirm } from '../../components/pin/PinConfirm';
import { useGps } from '../../hooks/useGps';
import { colors, fonts } from '../../core/theme';
import type { Bundle } from '../../types';

export function RetailerDashboard() {
  const gps = useGps();
  const [bundles, setBundles] = useState<Bundle[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [receiveBundleId, setReceiveBundleId] = useState<string | null>(null);
  const [showPin, setShowPin] = useState(false);
  const [receiving, setReceiving] = useState(false);

  const fetchBundles = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.get<Bundle[]>('/retailers/bundles');
      setBundles(data || []);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed to load bundles');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchBundles(); }, []);

  const startReceive = (bundleId: string) => {
    setReceiveBundleId(bundleId);
    gps.request();
    setShowPin(true);
  };

  const handlePinConfirm = async (_pin: string) => {
    if (!receiveBundleId) return;
    setReceiving(true);
    try {
      await api.post(`/retailers/bundles/${receiveBundleId}/receive`, {
        query: {
          ...(gps.latitude ? { latitude: String(gps.latitude) } : {}),
          ...(gps.longitude ? { longitude: String(gps.longitude) } : {}),
        },
      });
      setShowPin(false);
      fetchBundles();
    } catch (err) {
      setShowPin(false);
      setError(err instanceof ApiError ? err.message : 'Receive failed');
    } finally {
      setReceiving(false);
    }
  };

  return (
    <div>
      <PageHeader title="Retail" subtitle="Incoming inventory and bundle management" />

      <div style={{
        padding: '8px 14px', marginBottom: 12, borderRadius: 8, fontSize: 12,
        backgroundColor: gps.status === 'ready' ? '#E8F5ED' : '#FFF4E5',
        color: gps.status === 'ready' ? colors.success : colors.warning,
        animation: 'fadeInUp 0.2s ease-out both',
      }}>
        GPS: {gps.status === 'ready' ? `Ready (${gps.accuracy?.toFixed(0)}m)` : gps.status}
      </div>

      {error && <ErrorBanner message={error} onRetry={fetchBundles} />}
      {loading && <SkeletonList count={5} />}

      {!loading && !error && bundles.length === 0 && (
        <EmptyState title="No bundles" description="No bundles are currently assigned to your retail outlet." />
      )}

      {!loading && (
        <div className="agri-stagger">
          {bundles.map((bundle) => (
            <div
              key={bundle.bundleId}
              className="agri-card"
              style={{ padding: '14px 16px', marginBottom: 6 }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', marginBottom: 8 }}>
                <div>
                  <div style={{ fontSize: 14, fontWeight: 600, fontFamily: fonts.mono, color: colors.ink }}>{bundle.bundleId}</div>
                  <div style={{ fontSize: 12, color: colors.graphite, marginTop: 2 }}>
                    {bundle.bundleType} • {bundle.quantity} {bundle.unit}
                  </div>
                </div>
                <StatusBadge status={bundle.retailerReceived ? 'RECEIVED' : bundle.status} />
              </div>

              {!bundle.retailerReceived && (
                <Button size="sm" onClick={() => startReceive(bundle.bundleId)} disabled={gps.status !== 'ready'}>
                  Receive (GPS + PIN)
                </Button>
              )}
            </div>
          ))}
        </div>
      )}

      <PinConfirm
        open={showPin}
        title="Receive Bundle"
        subtitle={`Confirm receipt of bundle ${receiveBundleId}. GPS + PIN required.`}
        onConfirm={handlePinConfirm}
        onCancel={() => setShowPin(false)}
        loading={receiving}
      />
    </div>
  );
}
