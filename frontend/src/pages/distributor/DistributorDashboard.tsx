import { useState, useEffect } from 'react';

import { api, ApiError } from '../../core/api';
import { useAuthStore } from '../../core/auth-store';
import { connectInAppWallet, distributorToRetailer } from '../../core/blockchain';
import { getMfgBatchId } from '../../core/blockchain-mapping';
import { PageHeader, StatusBadge, EmptyState, ErrorBanner, Button } from '../../components/common/UI';
import { SkeletonList } from '../../components/common/Skeleton';
import { PinConfirm } from '../../components/pin/PinConfirm';
import { useGps } from '../../hooks/useGps';
import { colors, fonts } from '../../core/theme';
import type { Bundle } from '../../types';

export function DistributorDashboard() {
  const gps = useGps();
  const [bundles, setBundles] = useState<Bundle[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [actionBundleId, setActionBundleId] = useState<string | null>(null);
  const [actionType, setActionType] = useState<'receive' | 'verify'>('receive');
  const [showPin, setShowPin] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);

  const fetchBundles = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.get<Bundle[]>('/distributors/bundles/available');
      setBundles(data || []);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed to load bundles');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchBundles(); }, []);

  const startAction = (bundleId: string, type: 'receive' | 'verify') => {
    setActionBundleId(bundleId);
    setActionType(type);
    gps.request();
    setShowPin(true);
  };

  const handlePinConfirm = async (_pin: string) => {
    if (!actionBundleId) return;
    setActionLoading(true);
    try {
      if (actionType === 'receive') {
        await api.post(`/distributors/bundles/${actionBundleId}/receive`, {
          query: {
            ...(gps.latitude ? { latitude: String(gps.latitude) } : {}),
            ...(gps.longitude ? { longitude: String(gps.longitude) } : {}),
          },
        });
      } else {
        await api.post(`/distributors/bundles/${actionBundleId}/verify`);
      }
      try {
        const walletAddr = useAuthStore.getState().walletAddress;
        if (walletAddr) {
          const account = await connectInAppWallet(useAuthStore.getState().walletEmail || '');
          if (account && actionType === 'verify') {
            const mfgBatchId = getMfgBatchId(actionBundleId) ?? 0;
            await distributorToRetailer(account, mfgBatchId, account.address, 1);
          }
        }
      } catch {
        console.warn('Blockchain distributor transfer failed');
      }

      setShowPin(false);
      fetchBundles();
    } catch (err) {
      setShowPin(false);
      setError(err instanceof ApiError ? err.message : 'Action failed');
    } finally {
      setActionLoading(false);
    }
  };

  return (
    <div>
      <PageHeader title="Distribution" subtitle="Receive, verify, and dispatch bundles" />

      
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
        <EmptyState title="No available bundles" description="No bundles are currently available for distribution." />
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
                <StatusBadge status={bundle.status} />
              </div>
              {bundle.qrId && <div style={{ fontSize: 11, color: colors.graphite, fontFamily: fonts.mono, marginBottom: 8 }}>QR: {bundle.qrId}</div>}

              <div style={{ display: 'flex', gap: 8 }}>
                {!bundle.distributorVerified && (
                  <Button size="sm" onClick={() => startAction(bundle.bundleId, 'receive')} disabled={gps.status !== 'ready'}>
                    Receive
                  </Button>
                )}
                {bundle.distributorVerified && !bundle.retailerReceived && (
                  <Button size="sm" onClick={() => startAction(bundle.bundleId, 'verify')}>Verify</Button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      <PinConfirm
        open={showPin}
        title={`${actionType === 'receive' ? 'Receive' : 'Verify'} Bundle`}
        subtitle={`Confirm ${actionType} for bundle ${actionBundleId}. GPS + PIN required.`}
        onConfirm={handlePinConfirm}
        onCancel={() => setShowPin(false)}
        loading={actionLoading}
      />
    </div>
  );
}
