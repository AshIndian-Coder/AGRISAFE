import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { api, ApiError } from '../../core/api';
import { useAuthStore } from '../../core/auth-store';
import { PageHeader, StatusBadge, DataRow, Spinner, ErrorBanner, Button } from '../../components/common/UI';
import { PinConfirm } from '../../components/pin/PinConfirm';
import { RotatingCodeDisplay } from '../../components/qr/RotatingCodeDisplay';
import { QrScanner } from '../../components/qr/QrScanner';
import { useGps } from '../../hooks/useGps';
import { colors, fonts } from '../../core/theme';
import type { Lot, Package } from '../../types';

export function NodalLotDetailPage() {
  const navigate = useNavigate();
  const { lotId } = useParams<{ lotId: string }>();
  const gps = useGps();
  const { userUuid } = useAuthStore();
  const [lot, setLot] = useState<Lot | null>(null);
  const [packages, setPackages] = useState<Package[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showSplit, setShowSplit] = useState(false);
  const [splitQty, setSplitQty] = useState('');
  const [splitting, setSplitting] = useState(false);
  const [allotmentQrId, setAllotmentQrId] = useState<string | null>(null);
  const [generatingAllotment, setGeneratingAllotment] = useState(false);
  const [showScanTransfer, setShowScanTransfer] = useState(false);
  const [scannedAllotmentQr, setScannedAllotmentQr] = useState<string | null>(null);
  const [transferring, setTransferring] = useState(false);

  useEffect(() => {
    if (!lotId) return;
    setLoading(true);
    Promise.all([
      api.get<Lot>(`/lots/${lotId}`),
      api.get<Package[]>(`/nodal-centers/lots/${lotId}/packages`).catch(() => []),
    ])
      .then(([l, p]) => { setLot(l); setPackages(p || []); })
      .catch((err) => setError(err instanceof ApiError ? err.message : 'Failed'))
      .finally(() => setLoading(false));
  }, [lotId]);

  const handleSplit = async (_pin: string) => {
    if (!lotId || !splitQty) return;
    setSplitting(true);
    try {
      const quantities = splitQty.split(',').map((s) => parseFloat(s.trim())).filter((n) => !isNaN(n) && n > 0);
      await api.post(`/nodal-centers/lots/${lotId}/split`, {
        body: { quantities, packageType: 'STANDARD' },
      });
      setShowSplit(false);
      const p = await api.get<Package[]>(`/nodal-centers/lots/${lotId}/packages`);
      setPackages(p || []);
      const updatedLot = await api.get<Lot>(`/lots/${lotId}`);
      setLot(updatedLot);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Split failed');
    } finally {
      setSplitting(false);
    }
  };

  const handleGenerateAllotmentQr = async () => {
    if (!lotId) return;
    setGeneratingAllotment(true);
    try {
      const qrId = await api.post<string>(`/nodal-centers/lots/${lotId}/allotment-qr`);
      setAllotmentQrId(qrId);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed to generate allotment QR');
    } finally {
      setGeneratingAllotment(false);
    }
  };

  const handleTransferWithAllotment = async () => {
    if (!lotId || !scannedAllotmentQr || packages.length === 0) return;
    gps.request();
    setTransferring(true);
    try {
      const packageIds = packages.map((p) => p.packageId);
      await api.post('/nodal-centers/allotment/transfer', {
        body: { allotmentQrId: scannedAllotmentQr, packageIds },
        query: {
          ...(gps.latitude ? { latitude: String(gps.latitude) } : {}),
          ...(gps.longitude ? { longitude: String(gps.longitude) } : {}),
        },
      });
      setShowScanTransfer(false);
      const p = await api.get<Package[]>(`/nodal-centers/lots/${lotId}/packages`);
      setPackages(p || []);
      const updatedLot = await api.get<Lot>(`/lots/${lotId}`);
      setLot(updatedLot);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Transfer failed');
    } finally {
      setTransferring(false);
    }
  };

  if (loading) return <Spinner text="Loading…" />;
  if (error) return <div><PageHeader title="Lot" back={() => navigate(-1)} /><ErrorBanner message={error} /></div>;
  if (!lot) return null;

  const allPackaged = packages.length > 0 && lot.status === 'PACKAGED';
  const canSplit = lot.status === 'ACCEPTED' || lot.status === 'AT_NODAL_CENTER';

  return (
    <div>
      <PageHeader title="Lot Details" subtitle={lot.lotId} back={() => navigate(-1)} />

      <div style={{
        backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`, borderRadius: 8,
        padding: 16, marginBottom: 16, animation: 'fadeInUp 0.3s ease-out both',
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
          <span style={{ fontSize: 18, fontWeight: 700, fontFamily: fonts.mono, color: colors.ink }}>{lot.lotId}</span>
          <StatusBadge status={lot.status} />
        </div>
        <DataRow label="Quantity" value={`${lot.quantity} ${lot.unit || 'units'}`} />
        {lot.qrId && <DataRow label="QR ID" value={lot.qrId} mono />}
      </div>

      {lot.qrId && <RotatingCodeDisplay qrId={lot.qrId} compact />}

      {canSplit && (
        <div style={{
          backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`, borderRadius: 8,
          padding: 16, marginBottom: 16, animation: 'fadeInUp 0.3s ease-out 0.1s both',
        }}>
          <div style={{ fontSize: 13, fontWeight: 600, color: colors.ink, marginBottom: 8 }}>Split Lot into Packages</div>
          <div style={{ fontSize: 12, color: colors.graphite, marginBottom: 8 }}>
            Each package gets its own rotating QR. All QRs are siblings — if any is missing, an auto-flag is raised.
          </div>
          <input
            value={splitQty}
            onChange={(e) => setSplitQty(e.target.value)}
            placeholder="e.g. 100, 100, 100, 100, 100"
            className="agri-input"
            style={{
              width: '100%', padding: '10px 12px', fontSize: 14, borderRadius: 8,
              border: `1px solid ${colors.stoneBorder}`, outline: 'none', marginBottom: 8,
              fontFamily: fonts.mono, boxSizing: 'border-box',
            }}
          />
          <div style={{ fontSize: 12, color: colors.graphite, marginBottom: 12 }}>Enter comma-separated quantities that sum to {lot.quantity}</div>
          <Button onClick={() => setShowSplit(true)} disabled={!splitQty} fullWidth>Split Lot</Button>
        </div>
      )}

      {packages.length > 0 && (
        <div style={{
          backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`, borderRadius: 8, padding: 16,
          animation: 'fadeInUp 0.3s ease-out 0.15s both', marginBottom: 16,
        }}>
          <div style={{ fontSize: 13, fontWeight: 600, color: colors.ink, marginBottom: 4 }}>Packages ({packages.length})</div>
          <div style={{ fontSize: 11, color: colors.graphite, marginBottom: 8 }}>
            Sibling group: {lotId} • Each QR rotates every 30s
          </div>
          <div className="agri-stagger">
            {packages.map((pkg) => (
              <div key={pkg.packageId} style={{ padding: '10px 0', borderBottom: `1px solid ${colors.softPaper}` }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div>
                    <span style={{ fontSize: 13, fontWeight: 600, fontFamily: fonts.mono, color: colors.ink }}>{pkg.packageId}</span>
                    <span style={{ fontSize: 12, color: colors.graphite, marginLeft: 8 }}>{pkg.quantity} {pkg.unit || 'units'}</span>
                  </div>
                  <StatusBadge status={pkg.status} />
                </div>
                {pkg.qrId && <div style={{ marginTop: 4 }}><RotatingCodeDisplay qrId={pkg.qrId} compact /></div>}
              </div>
            ))}
          </div>
        </div>
      )}

      {allPackaged && (
        <div style={{
          backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`, borderRadius: 8,
          padding: 16, marginBottom: 16,
        }}>
          <div style={{ fontSize: 13, fontWeight: 600, color: colors.ink, marginBottom: 8 }}>Supplier Pickup</div>
          <div style={{ fontSize: 12, color: colors.graphite, marginBottom: 12 }}>
            Generate an allotment QR for the supplier, or scan the supplier's QR to transfer ownership.
          </div>

          {!allotmentQrId ? (
            <Button onClick={handleGenerateAllotmentQr} disabled={generatingAllotment} fullWidth style={{ marginBottom: 8 }}>
              {generatingAllotment ? 'Generating…' : 'Generate Supplier Allotment QR'}
            </Button>
          ) : (
            <div style={{ marginBottom: 12 }}>
              <div style={{ fontSize: 12, fontWeight: 600, color: colors.success, marginBottom: 4 }}>Allotment QR Generated</div>
              <RotatingCodeDisplay qrId={allotmentQrId} />
            </div>
          )}

          <div style={{ fontSize: 11, color: colors.graphite, marginBottom: 8, textAlign: 'center' }}>— OR —</div>

          <Button onClick={() => { gps.request(); setShowScanTransfer(true); }} fullWidth variant="secondary">
            Scan Supplier's QR to Transfer
          </Button>
        </div>
      )}

      <PinConfirm
        open={showSplit}
        title="Split Lot"
        subtitle={`Split lot ${lotId} into packages. This creates sibling QR codes for each package.`}
        onConfirm={handleSplit}
        onCancel={() => setShowSplit(false)}
        loading={splitting}
      />

      {showScanTransfer && (
        <div style={{
          position: 'fixed', inset: 0, zIndex: 1200, backgroundColor: 'rgba(0,0,0,0.6)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', backdropFilter: 'blur(4px)',
        }} onClick={() => setShowScanTransfer(false)}>
          <div style={{
            backgroundColor: colors.white, borderRadius: 12, padding: 20, width: 380,
            maxHeight: '80vh', overflow: 'auto',
          }} onClick={(e) => e.stopPropagation()}>
            <div style={{ fontSize: 16, fontWeight: 700, color: colors.ink, marginBottom: 4 }}>Scan Supplier QR</div>
            <div style={{ fontSize: 12, color: colors.graphite, marginBottom: 12 }}>
              Scan the supplier's allotment QR to transfer all {packages.length} packages.
            </div>

            {!scannedAllotmentQr ? (
              <div style={{ marginBottom: 12 }}>
                <QrScanner active={true} onScan={(text) => {
                  const extracted = text.includes('/verify/') ? text.split('/verify/')[1] : text;
                  setScannedAllotmentQr(extracted);
                }} onClose={() => setShowScanTransfer(false)} />
              </div>
            ) : (
              <div style={{ marginBottom: 12 }}>
                <div style={{ fontSize: 12, color: colors.graphite, marginBottom: 4 }}>Scanned QR:</div>
                <div style={{ fontFamily: fonts.mono, fontSize: 14, padding: '8px 12px', backgroundColor: colors.softPaper, borderRadius: 6 }}>
                  {scannedAllotmentQr}
                </div>
              </div>
            )}

            <div style={{ display: 'flex', gap: 8 }}>
              <button onClick={() => setShowScanTransfer(false)} style={{
                flex: 1, padding: '10px 0', fontSize: 13, fontWeight: 600,
                border: `1px solid ${colors.stoneBorder}`, borderRadius: 8,
                backgroundColor: colors.white, color: colors.graphite, cursor: 'pointer',
              }}>Cancel</button>
              <button
                onClick={handleTransferWithAllotment}
                disabled={!scannedAllotmentQr || transferring}
                style={{
                  flex: 2, padding: '10px 0', fontSize: 13, fontWeight: 600,
                  border: 'none', borderRadius: 8,
                  backgroundColor: scannedAllotmentQr && !transferring ? colors.forest : colors.stoneBorder,
                  color: 'white', cursor: scannedAllotmentQr ? 'pointer' : 'not-allowed',
                }}
              >
                {transferring ? 'Transferring…' : `Transfer ${packages.length} Packages`}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
