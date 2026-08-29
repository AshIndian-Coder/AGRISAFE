import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { api } from '../../core/api';
import { getManufacturedBatch, getManufacturedBatchCount } from '../../core/blockchain';
import { Spinner } from '../../components/common/UI';
import { colors, fonts } from '../../core/theme';
import type { ProductVerification, TraceEvent } from '../../types';

export function ConsumerVerifyPage() {
  const { qrToken } = useParams<{ qrToken: string }>();
  const [verification, setVerification] = useState<ProductVerification | null>(null);
  const [trace, setTrace] = useState<TraceEvent[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [blockchainData, setBlockchainData] = useState<{
    productName: string;
    manufacturer: string;
    quantity: number;
    recalled: boolean;
    distributed: boolean;
  } | null>(null);

  useEffect(() => {
    if (!qrToken) return;
    setLoading(true);
    Promise.all([
      api.get<ProductVerification>(`/public/products/${qrToken}`, { auth: false }),
      api.get<TraceEvent[]>(`/public/products/${qrToken}/trace`, { auth: false }).catch(() => []),
    ])
      .then(async ([v, t]) => {
        setVerification(v);
        setTrace(t || []);

        try {
          const count = await getManufacturedBatchCount();
          if (count > 0) {
            const batch = await getManufacturedBatch(count);
            if (batch) {
              setBlockchainData({
                productName: batch.productName,
                manufacturer: batch.manufacturer,
                quantity: batch.quantity,
                recalled: batch.recalled,
                distributed: batch.distributed,
              });
            }
          }
        } catch {
        }
      })
      .catch(() => setError('Product not found or verification failed.'))
      .finally(() => setLoading(false));
  }, [qrToken]);

  if (loading) {
    return (
      <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', backgroundColor: colors.warmPaper }}>
        <Spinner text="Verifying product…" />
      </div>
    );
  }

  if (error || !verification) {
    return (
      <div style={{
        minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center',
        backgroundColor: colors.warmPaper, padding: 16,
        animation: 'fadeIn 0.3s ease-out both',
      }}>
        <div style={{ textAlign: 'center', maxWidth: 360, animation: 'fadeInUp 0.3s ease-out both' }}>
          <div style={{
            width: 56, height: 56, borderRadius: '50%', backgroundColor: '#FDF2F1',
            display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 16px',
          }}>
            <span style={{ fontSize: 24, color: colors.danger }}>✕</span>
          </div>
          <div style={{ fontSize: 16, fontWeight: 600, color: colors.danger, marginBottom: 4 }}>Verification Failed</div>
          <div style={{ fontSize: 14, color: colors.graphite }}>{error || 'Product not found.'}</div>
        </div>
      </div>
    );
  }

  const isVerified = verification.verificationStatus === 'VERIFIED';
  const isRecalled = verification.recalled;

  const journeySteps = [
    { label: 'Origin registered', reached: true },
    { label: 'Collection completed', reached: trace.some((t) => t.eventType?.includes('ACCEPTED')) },
    { label: 'Nodal processing completed', reached: trace.some((t) => t.eventType?.includes('NODAL') || t.eventType?.includes('PACKAGE')) },
    { label: 'Quality testing completed', reached: trace.some((t) => t.eventType?.includes('TEST')) },
    { label: 'Manufacturing completed', reached: trace.some((t) => t.eventType?.includes('MANUFACTURER')) },
    { label: 'Distribution completed', reached: trace.some((t) => t.eventType?.includes('DISTRIBUTOR')) },
    { label: 'Retail verification completed', reached: verification.retailerReceived },
  ];

  return (
    <div style={{ minHeight: '100vh', backgroundColor: colors.warmPaper, padding: 16 }}>
      <div style={{ maxWidth: 440, margin: '0 auto' }}>
        
        <div style={{
          textAlign: 'center', marginBottom: 24, paddingTop: 24,
          animation: 'fadeInUp 0.3s ease-out both',
        }}>
          <div style={{
            width: 48, height: 48, borderRadius: 12, backgroundColor: colors.forest,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            margin: '0 auto 10px', fontSize: 20, color: 'white', fontWeight: 700,
          }}>A</div>
          <div style={{ fontSize: 18, fontWeight: 700, color: colors.forest, letterSpacing: '-0.02em', fontFamily: fonts.heading }}>AgriSafe</div>
          <div style={{ fontSize: 11, color: colors.graphite, textTransform: 'uppercase', letterSpacing: '0.1em', marginTop: 2 }}>Product Verification</div>
        </div>

        
        <div style={{
          textAlign: 'center', padding: '24px 16px', marginBottom: 16,
          backgroundColor: colors.white, borderRadius: 12,
          border: `1px solid ${isVerified ? '#C8E6D5' : isRecalled ? '#F0D0CD' : colors.stoneBorder}`,
          boxShadow: '0 4px 24px rgba(0, 0, 0, 0.04)',
          animation: 'fadeInUp 0.3s ease-out 0.1s both',
        }}>
          {isVerified ? (
            <div style={{
              width: 56, height: 56, borderRadius: '50%', backgroundColor: '#E8F5ED',
              display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 12px',
              animation: 'fadeInScale 0.4s ease-out 0.2s both',
            }}>
              <span style={{ fontSize: 28, color: colors.success }}>✓</span>
            </div>
          ) : isRecalled ? (
            <div style={{
              width: 56, height: 56, borderRadius: '50%', backgroundColor: '#FDF2F1',
              display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 12px',
            }}>
              <span style={{ fontSize: 28, color: colors.danger }}>✕</span>
            </div>
          ) : (
            <div style={{
              width: 56, height: 56, borderRadius: '50%', backgroundColor: '#FFF4E5',
              display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 12px',
            }}>
              <span style={{ fontSize: 28, color: colors.warning }}>?</span>
            </div>
          )}

          <div style={{ fontSize: 18, fontWeight: 700, color: isVerified ? colors.success : isRecalled ? colors.danger : colors.warning, fontFamily: fonts.heading }}>
            {isVerified ? 'Product Verified' : isRecalled ? 'Product Recalled' : 'Not Verified'}
          </div>
          {verification.productName && (
            <div style={{ fontSize: 15, color: colors.ink, marginTop: 4 }}>{verification.productName}</div>
          )}
          {verification.manufacturer && (
            <div style={{ fontSize: 13, color: colors.graphite, marginTop: 2 }}>{verification.manufacturer}</div>
          )}
        </div>

        
        <div style={{
          backgroundColor: colors.white, borderRadius: 8, border: `1px solid ${colors.stoneBorder}`,
          padding: 16, marginBottom: 12, animation: 'fadeInUp 0.3s ease-out 0.15s both',
        }}>
          <div style={{ fontSize: 12, fontWeight: 600, color: colors.graphite, textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 8 }}>Authenticity</div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <span style={{ color: isVerified ? colors.success : colors.danger, fontSize: 16 }}>{isVerified ? '✓' : '✕'}</span>
            <span style={{ fontSize: 14, color: colors.ink }}>{isVerified ? 'Verified' : 'Not Verified'}</span>
          </div>
        </div>

        
        <div style={{
          backgroundColor: colors.white, borderRadius: 8, border: `1px solid ${colors.stoneBorder}`,
          padding: 16, marginBottom: 12, animation: 'fadeInUp 0.3s ease-out 0.2s both',
        }}>
          <div style={{ fontSize: 12, fontWeight: 600, color: colors.graphite, textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 12 }}>Traceability</div>
          {journeySteps.map((step, i) => (
            <div key={i} style={{
              display: 'flex', alignItems: 'center', gap: 8, padding: '4px 0',
              animation: `fadeInUp 0.3s ease-out ${0.25 + i * 0.04}s both`,
            }}>
              <span style={{
                fontSize: 14, color: step.reached ? colors.success : colors.stoneBorder,
                fontWeight: 600, width: 20, textAlign: 'center',
              }}>
                {step.reached ? '✓' : '○'}
              </span>
              <span style={{
                fontSize: 13,
                color: step.reached ? colors.ink : colors.graphite,
              }}>
                {step.label}
              </span>
            </div>
          ))}
        </div>

        
        {verification.qualityStatus && (
          <div style={{
            backgroundColor: colors.white, borderRadius: 8, border: `1px solid ${colors.stoneBorder}`,
            padding: 16, marginBottom: 12, animation: 'fadeInUp 0.3s ease-out 0.25s both',
          }}>
            <div style={{ fontSize: 12, fontWeight: 600, color: colors.graphite, textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 8 }}>Quality</div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <span style={{
                fontSize: 14,
                color: verification.qualityStatus === 'PASSED' ? colors.success : colors.danger,
              }}>
                {verification.qualityStatus === 'PASSED' ? '✓' : '✕'}
              </span>
              <span style={{ fontSize: 14, color: colors.ink }}>Quality: {verification.qualityStatus}</span>
            </div>
          </div>
        )}

        
        <div style={{
          backgroundColor: colors.white, borderRadius: 8, border: `1px solid ${colors.stoneBorder}`,
          padding: 16, marginBottom: 12, animation: 'fadeInUp 0.3s ease-out 0.3s both',
        }}>
          <div style={{ fontSize: 12, fontWeight: 600, color: colors.graphite, textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 8 }}>🔗 Blockchain Verification (Polygon Amoy)</div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 8 }}>
            <span style={{ color: verification.traceabilityComplete ? colors.success : colors.warning, fontSize: 14 }}>
              {verification.traceabilityComplete ? '✓' : '○'}
            </span>
            <span style={{ fontSize: 13, color: colors.ink }}>
              {verification.traceabilityComplete ? 'Confirmed on Polygon Amoy' : 'Pending blockchain confirmation'}
            </span>
          </div>
          <div style={{ fontSize: 12, color: colors.graphite }}>
            {verification.traceEventCount} trace event(s) recorded
          </div>
          {blockchainData && (
            <div style={{ marginTop: 10, padding: '8px 12px', backgroundColor: colors.softPaper, borderRadius: 6, fontSize: 12 }}>
              <div style={{ fontWeight: 600, color: colors.ink, marginBottom: 4 }}>On-Chain Product Data</div>
              <div style={{ color: colors.graphite }}>Product: {blockchainData.productName}</div>
              <div style={{ color: colors.graphite }}>Manufacturer: {blockchainData.manufacturer?.slice(0, 6)}...{blockchainData.manufacturer?.slice(-4)}</div>
              <div style={{ color: colors.graphite }}>Quantity: {blockchainData.quantity}</div>
              <div style={{ color: colors.graphite }}>Distributed: {blockchainData.distributed ? 'Yes' : 'No'}</div>
              {blockchainData.recalled && (
                <div style={{ color: colors.danger, fontWeight: 600, marginTop: 4 }}>⚠ RECALLED ON-CHAIN</div>
              )}
            </div>
          )}
        </div>

        
        {verification.reason && (
          <div style={{
            backgroundColor: '#FDF2F1', borderRadius: 8, border: `1px solid #F0D0CD`, padding: 16, marginBottom: 12,
            animation: 'fadeInUp 0.3s ease-out 0.3s both',
          }}>
            <div style={{ fontSize: 13, color: colors.danger, fontWeight: 600 }}>Reason: {verification.reason.replace(/_/g, ' ')}</div>
          </div>
        )}

        
        <div style={{ textAlign: 'center', padding: '16px 0 24px' }}>
          <div style={{ fontSize: 11, color: colors.graphite }}>
            Powered by AgriSafe • Agricultural Supply Chain Traceability
          </div>
        </div>
      </div>
    </div>
  );
}
