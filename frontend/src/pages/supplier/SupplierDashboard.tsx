import { useState, useEffect } from 'react';
import { api, ApiError } from '../../core/api';
import { useAuthStore } from '../../core/auth-store';
import { connectInAppWallet, supplierToManufacturer } from '../../core/blockchain';
import { PageHeader, StatusBadge, EmptyState, ErrorBanner, Button } from '../../components/common/UI';
import { SkeletonList } from '../../components/common/Skeleton';
import { PinConfirm } from '../../components/pin/PinConfirm';
import { useGps } from '../../hooks/useGps';
import { colors, fonts } from '../../core/theme';
import type { Lot, PagedResponse } from '../../types';

export function SupplierDashboard() {
  const gps = useGps();
  const [lots, setLots] = useState<Lot[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showPin, setShowPin] = useState(false);
  const [selectedLotId, setSelectedLotId] = useState<string | null>(null);
  const [transferring, setTransferring] = useState(false);

  const fetchAssignments = async () => {
    setLoading(true);
    try {
      const data = await api.get<PagedResponse<Lot>>('/suppliers/assignments', { query: { page: '0', size: '20' } });
      setLots(data.content || []);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchAssignments(); }, []);

  const startTransfer = (lotId: string) => {
    setSelectedLotId(lotId);
    gps.request();
    setShowPin(true);
  };

  const handlePinConfirm = async (_pin: string) => {
    if (!selectedLotId) return;
    setTransferring(true);
    try {
      await api.post(`/suppliers/lots/${selectedLotId}/transfer`);

      try {
        const walletAddr = useAuthStore.getState().walletAddress;
        if (walletAddr) {
          const account = await connectInAppWallet(useAuthStore.getState().walletEmail || '');
          if (account) {
            await supplierToManufacturer(
              account,
              [0],
              account.address,
            );
          }
        }
      } catch {
        console.warn('Blockchain supplier→manufacturer transfer failed — transferred on backend only');
      }

      setShowPin(false);
      setSelectedLotId(null);
      fetchAssignments();
    } catch (err) {
      setShowPin(false);
      setError(err instanceof ApiError ? err.message : 'Transfer failed');
    } finally {
      setTransferring(false);
    }
  };

  return (
    <div>
      <PageHeader title="Supplier" subtitle="Assigned packages and logistics" />
      <div style={{
        padding: '8px 14px', marginBottom: 12, borderRadius: 8, fontSize: 12,
        backgroundColor: gps.status === 'ready' ? '#E8F5ED' : '#FFF4E5',
        color: gps.status === 'ready' ? colors.success : colors.warning,
        animation: 'fadeInUp 0.2s ease-out both',
      }}>
        GPS: {gps.status === 'ready' ? `Ready` : gps.status}
      </div>
      {error && <ErrorBanner message={error} onRetry={fetchAssignments} />}
      {loading && <SkeletonList count={5} />}
      {!loading && lots.length === 0 && <EmptyState title="No assignments" description="No packages assigned." />}
      {!loading && (
        <div className="agri-stagger">
          {lots.map((lot) => (
            <div key={lot.lotId} className="agri-card" style={{ padding: '14px 16px', marginBottom: 6 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', marginBottom: 8 }}>
                <div>
                  <div style={{ fontSize: 14, fontWeight: 600, fontFamily: fonts.mono, color: colors.ink }}>{lot.lotId}</div>
                  <div style={{ fontSize: 12, color: colors.graphite, marginTop: 2 }}>{lot.quantity} {lot.unit || 'units'}</div>
                </div>
                <StatusBadge status={lot.status} />
              </div>
              {lot.status === 'AT_SUPPLIER' && (
                <Button size="sm" onClick={() => startTransfer(lot.lotId)} disabled={gps.status !== 'ready'}>
                  Transfer to Manufacturer (GPS + PIN)
                </Button>
              )}
            </div>
          ))}
        </div>
      )}

      <PinConfirm
        open={showPin}
        title="Transfer to Manufacturer"
        subtitle={`Transfer lot ${selectedLotId} to the manufacturer. This records ownership transfer on the blockchain.`}
        onConfirm={handlePinConfirm}
        onCancel={() => setShowPin(false)}
        loading={transferring}
      />
    </div>
  );
}
