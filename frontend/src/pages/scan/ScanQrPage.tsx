import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, ApiError } from '../../core/api';
import { useAuthStore } from '../../core/auth-store';
import { QrScanner } from '../../components/qr/QrScanner';
import { useGps } from '../../hooks/useGps';
import { PageHeader, ErrorBanner } from '../../components/common/UI';
import { colors, fonts } from '../../core/theme';
import type { QrCredential } from '../../types';

export function ScanQrPage() {
  const navigate = useNavigate();
  const gps = useGps();
  const { userUuid, userType } = useAuthStore();
  const [gpsReady, setGpsReady] = useState(false);
  const [scanning, setScanning] = useState(false);
  const [qrId, setQrId] = useState<string | null>(null);
  const [rotatingCode, setRotatingCode] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState<QrCredential | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    gps.request();
  }, []);

  useEffect(() => {
    if (gps.status === 'ready' && !gpsReady) {
      setGpsReady(true);
      setScanning(true);
    }
  }, [gps.status, gpsReady]);

  const handleScan = (decodedText: string) => {
    setScanning(false);
    const extracted = decodedText.includes('/verify/')
      ? decodedText.split('/verify/')[1]
      : decodedText;
    setQrId(extracted);
  };

  const handleSubmit = async () => {
    if (!qrId || !rotatingCode || !userUuid) return;
    setSubmitting(true);
    setError(null);
    try {
      const res = await api.post<QrCredential>('/qr/scan', {
        body: {
          qrId,
          rotatingCode: rotatingCode.trim().toUpperCase(),
          scannedByUuid: userUuid,
          scannedByRole: userType,
          latitude: gps.latitude ?? null,
          longitude: gps.longitude ?? null,
        },
      });
      setResult(res);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Scan verification failed');
    } finally {
      setSubmitting(false);
    }
  };

  if (result) {
    return (
      <div>
        <PageHeader title="QR Verified" subtitle="Scan successful" back={() => navigate(-1)} />
        <div style={{
          backgroundColor: '#E8F5ED', border: `1px solid ${colors.success}`,
          borderRadius: 8, padding: 16, marginBottom: 16, textAlign: 'center',
        }}>
          <div style={{ fontSize: 32, marginBottom: 8 }}>✅</div>
          <div style={{ fontSize: 16, fontWeight: 700, color: colors.success, marginBottom: 4 }}>Verification Successful</div>
          <div style={{ fontSize: 13, color: colors.graphite }}>QR code has been consumed and recorded on the blockchain.</div>
        </div>
        <div style={{
          backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`,
          borderRadius: 8, padding: 16,
        }}>
          <div style={{ fontSize: 13, fontWeight: 600, color: colors.ink, marginBottom: 8 }}>Details</div>
          <div style={{ fontSize: 12, color: colors.graphite, marginBottom: 4 }}><strong>QR ID:</strong> <span style={{ fontFamily: fonts.mono }}>{result.qrId}</span></div>
          <div style={{ fontSize: 12, color: colors.graphite, marginBottom: 4 }}><strong>Object:</strong> {result.objectType} — {result.objectId}</div>
          <div style={{ fontSize: 12, color: colors.graphite, marginBottom: 4 }}><strong>Stage:</strong> {result.stage}</div>
          <div style={{ fontSize: 12, color: colors.graphite }}><strong>Consumed:</strong> {result.consumedAt ? new Date(result.consumedAt).toLocaleString() : '—'}</div>
        </div>
      </div>
    );
  }

  return (
    <div>
      <PageHeader title="Scan QR Code" subtitle="Verify supply chain authenticity" back={() => navigate(-1)} />

      <div style={{
        padding: '10px 14px', marginBottom: 16, borderRadius: 8, fontSize: 13,
        backgroundColor: gpsReady ? '#E8F5ED' : '#FFF4E5',
        color: gpsReady ? colors.success : colors.warning,
      }}>
        GPS: {gpsReady ? `Ready (${gps.accuracy?.toFixed(0)}m accuracy)` : gps.status === 'requesting' ? 'Requesting…' : gps.status === 'denied' ? 'Permission denied — GPS is required for QR scanning' : 'GPS is required for QR scanning'}
      </div>

      {!gpsReady && (
        <div style={{ textAlign: 'center', padding: 32 }}>
          <div style={{ fontSize: 14, color: colors.graphite, marginBottom: 12 }}>Please enable GPS to scan QR codes.</div>
          <button
            onClick={() => gps.request()}
            style={{
              padding: '10px 20px', borderRadius: 8, border: `1px solid ${colors.stoneBorder}`,
              backgroundColor: colors.forest, color: 'white', cursor: 'pointer', fontSize: 13, fontWeight: 600,
            }}
          >
            Enable GPS
          </button>
        </div>
      )}

      {gpsReady && !qrId && (
        <div style={{ marginBottom: 16 }}>
          <QrScanner active={scanning} onScan={handleScan} onClose={() => navigate(-1)} />
        </div>
      )}

      {gpsReady && qrId && (
        <div style={{
          backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`,
          borderRadius: 8, padding: 16, marginBottom: 16,
        }}>
          <div style={{ fontSize: 13, fontWeight: 600, color: colors.ink, marginBottom: 8 }}>QR Detected</div>
          <div style={{
            fontFamily: fonts.mono, fontSize: 14, padding: '8px 12px',
            backgroundColor: colors.softPaper, borderRadius: 6, marginBottom: 12,
          }}>
            {qrId}
          </div>

          <label style={{ fontSize: 13, fontWeight: 500, color: colors.ink, display: 'block', marginBottom: 4 }}>
            Enter rotating verification code
          </label>
          <input
            value={rotatingCode}
            onChange={(e) => setRotatingCode(e.target.value.toUpperCase())}
            placeholder="Enter 6-character code"
            maxLength={6}
            style={{
              width: '100%', padding: '10px 12px', fontSize: 16, fontFamily: fonts.mono,
              letterSpacing: 4, textAlign: 'center', borderRadius: 8,
              border: `1px solid ${colors.stoneBorder}`, boxSizing: 'border-box',
              marginBottom: 12,
            }}
          />

          {error && <ErrorBanner message={error} />}

          <div style={{ display: 'flex', gap: 8 }}>
            <button
              onClick={() => { setQrId(null); setScanning(true); }}
              style={{
                flex: 1, padding: '10px 0', fontSize: 13, fontWeight: 600,
                border: `1px solid ${colors.stoneBorder}`, borderRadius: 8,
                backgroundColor: colors.white, color: colors.graphite, cursor: 'pointer',
              }}
            >
              Rescan
            </button>
            <button
              onClick={handleSubmit}
              disabled={rotatingCode.length < 6 || submitting}
              style={{
                flex: 2, padding: '10px 0', fontSize: 13, fontWeight: 600,
                border: 'none', borderRadius: 8,
                backgroundColor: rotatingCode.length >= 6 && !submitting ? colors.forest : colors.stoneBorder,
                color: 'white', cursor: rotatingCode.length >= 6 ? 'pointer' : 'not-allowed',
              }}
            >
              {submitting ? 'Verifying…' : 'Verify & Consume'}
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
