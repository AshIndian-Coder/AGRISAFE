import { useState } from 'react';

import { api, ApiError } from '../../core/api';
import { useAuthStore } from '../../core/auth-store';
import { connectInAppWallet, recordManufacturerInspection } from '../../core/blockchain';
import { getRawBatchId } from '../../core/blockchain-mapping';
import { PageHeader, Button, ErrorBanner } from '../../components/common/UI';
import { SkeletonForm, SkeletonList } from '../../components/common/Skeleton';
import { PinConfirm } from '../../components/pin/PinConfirm';
import { colors, fonts } from '../../core/theme';
import type { TestResultResponse, TestSubmitRequest } from '../../types';

export function TestingDashboard() {

  const [packageId, setPackageId] = useState('');
  const [testHistory, setTestHistory] = useState<TestResultResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showPin, setShowPin] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const [form, setForm] = useState<Omit<TestSubmitRequest, 'packageId'>>({
    testProfileId: 1,
    measuredValue: '',
    unit: '',
  });

  const set = (key: string, val: string | number) => setForm((f) => ({ ...f, [key]: val }));

  const lookupTests = async () => {
    if (!packageId.trim()) return;
    setLoading(true);
    setError(null);
    try {
      const data = await api.get<TestResultResponse[]>(`/testing/packages/${packageId.trim()}/results`);
      setTestHistory(data || []);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed to load test history');
    } finally {
      setLoading(false);
    }
  };

  const handlePinConfirm = async (_pin: string) => {
    setSubmitting(true);
    setError(null);
    try {
      await api.post('/testing/submit', {
        body: {
          packageId: packageId.trim(),
          testProfileId: form.testProfileId,
          measuredValue: form.measuredValue,
          unit: form.unit || undefined,
        },
      });

      try {
        const walletAddr = useAuthStore.getState().walletAddress;
        if (walletAddr) {
          const account = await connectInAppWallet(useAuthStore.getState().walletEmail || '');
          if (account) {
            const rawBatchId = getRawBatchId(packageId.trim()) ?? 0;
            await recordManufacturerInspection(
              account,
              rawBatchId,
              form.measuredValue,
              JSON.stringify({ unit: form.unit, testProfileId: form.testProfileId, packageId: packageId.trim() }),
            );
          }
        }
      } catch {
        console.warn('Blockchain inspection recording failed — test submitted on backend only');
      }

      setShowPin(false);
      const data = await api.get<TestResultResponse[]>(`/testing/packages/${packageId.trim()}/results`);
      setTestHistory(data || []);
      setForm({ testProfileId: 1, measuredValue: '', unit: '' });
    } catch (err) {
      setShowPin(false);
      setError(err instanceof ApiError ? err.message : 'Test submission failed');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <PageHeader title="Quality Testing" subtitle="Submit test results for packages" />

      
      <div style={{
        backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`, borderRadius: 8,
        padding: 16, marginBottom: 16, animation: 'fadeInUp 0.3s ease-out both',
      }}>
        <label style={{ display: 'block', fontSize: 13, fontWeight: 500, color: colors.ink, marginBottom: 4 }}>Package ID</label>
        <div style={{ display: 'flex', gap: 8 }}>
          <input
            value={packageId}
            onChange={(e) => setPackageId(e.target.value)}
            placeholder="PKG-XXXXXXXX"
            className="agri-input"
            style={{
              flex: 1, padding: '10px 12px', fontSize: 14, borderRadius: 8,
              border: `1px solid ${colors.stoneBorder}`, outline: 'none',
              fontFamily: fonts.mono, boxSizing: 'border-box',
            }}
          />
          <Button onClick={lookupTests} disabled={!packageId.trim() || loading}>Lookup</Button>
        </div>
      </div>

      {error && <ErrorBanner message={error} />}
      {loading && !packageId.trim() && <SkeletonForm />}

      
      {packageId.trim() && (
        <div style={{
          backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`, borderRadius: 8,
          padding: 16, marginBottom: 16, animation: 'fadeInUp 0.25s ease-out both',
        }}>
          <div style={{ fontSize: 13, fontWeight: 600, color: colors.ink, marginBottom: 12 }}>Submit Test Result</div>

          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', fontSize: 13, fontWeight: 500, color: colors.ink, marginBottom: 4 }}>Test Profile ID</label>
            <input
              type="number"
              value={form.testProfileId}
              onChange={(e) => set('testProfileId', parseInt(e.target.value) || 1)}
              className="agri-input"
              style={{
                width: '100%', padding: '10px 12px', fontSize: 14, borderRadius: 8,
                border: `1px solid ${colors.stoneBorder}`, outline: 'none', boxSizing: 'border-box',
              }}
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12, marginBottom: 12 }}>
            <div>
              <label style={{ display: 'block', fontSize: 13, fontWeight: 500, color: colors.ink, marginBottom: 4 }}>Measured Value *</label>
              <input
                value={form.measuredValue}
                onChange={(e) => set('measuredValue', e.target.value)}
                placeholder="e.g. 3.5"
                className="agri-input"
                style={{
                  width: '100%', padding: '10px 12px', fontSize: 14, borderRadius: 8,
                  border: `1px solid ${colors.stoneBorder}`, outline: 'none', boxSizing: 'border-box',
                }}
              />
            </div>
            <div>
              <label style={{ display: 'block', fontSize: 13, fontWeight: 500, color: colors.ink, marginBottom: 4 }}>Unit</label>
              <input
                value={form.unit}
                onChange={(e) => set('unit', e.target.value)}
                placeholder="%, ppm"
                className="agri-input"
                style={{
                  width: '100%', padding: '10px 12px', fontSize: 14, borderRadius: 8,
                  border: `1px solid ${colors.stoneBorder}`, outline: 'none', boxSizing: 'border-box',
                }}
              />
            </div>
          </div>

          <Button onClick={() => setShowPin(true)} disabled={!form.measuredValue} fullWidth>Submit Test (PIN required)</Button>
        </div>
      )}

      
      {loading && packageId.trim() && <SkeletonList count={3} />}
      {testHistory.length > 0 && (
        <div style={{
          backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`, borderRadius: 8, padding: 16,
          animation: 'fadeInUp 0.3s ease-out both',
        }}>
          <div style={{ fontSize: 13, fontWeight: 600, color: colors.ink, marginBottom: 8 }}>Test History ({testHistory.length})</div>
          <div className="agri-stagger">
            {testHistory.map((t) => (
              <div key={t.testRecordId} style={{ padding: '10px 0', borderBottom: `1px solid ${colors.softPaper}` }}>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <span style={{ fontSize: 13, fontFamily: fonts.mono, color: colors.ink }}>{t.testRecordId}</span>
                  <span style={{
                    fontSize: 11, fontWeight: 600, padding: '2px 8px', borderRadius: 4,
                    backgroundColor: t.result === 'PASS' ? '#E8F5ED' : t.result === 'FAIL' ? '#FDF2F1' : '#FFF4E5',
                    color: t.result === 'PASS' ? colors.success : t.result === 'FAIL' ? colors.danger : colors.warning,
                    transition: 'all 0.2s ease',
                  }}>
                    {t.result}
                  </span>
                </div>
                <div style={{ fontSize: 12, color: colors.graphite, marginTop: 4 }}>
                  Value: {t.measuredValue} {t.unit || ''} • Source: {t.measurementSource}
                  {t.standardName && ` • Standard: ${t.standardName}`}
                </div>
                <div style={{ fontSize: 11, color: colors.graphite, marginTop: 2 }}>
                  {t.testedAt ? new Date(t.testedAt).toLocaleString() : '—'}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      <PinConfirm
        open={showPin}
        title="Submit Test Result"
        subtitle={`Submit test for package ${packageId}. This creates a permanent quality record.`}
        onConfirm={handlePinConfirm}
        onCancel={() => setShowPin(false)}
        loading={submitting}
      />
    </div>
  );
}
