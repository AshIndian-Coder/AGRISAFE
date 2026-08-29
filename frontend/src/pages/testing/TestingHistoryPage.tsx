import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, ApiError } from '../../core/api';
import { PageHeader, StatusBadge, Spinner, ErrorBanner, Button } from '../../components/common/UI';
import { colors, fonts } from '../../core/theme';
import type { TestResultResponse } from '../../types';

export function TestingHistoryPage() {
  const navigate = useNavigate();
  const [packageId, setPackageId] = useState('');
  const [tests, setTests] = useState<TestResultResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const lookup = async () => {
    if (!packageId.trim()) return;
    setLoading(true);
    try {
      const data = await api.get<TestResultResponse[]>(`/testing/packages/${packageId.trim()}/results`);
      setTests(data || []);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <PageHeader title="Test History" back={() => navigate('/testing')} />
      <div style={{
        backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`, borderRadius: 8,
        padding: 16, marginBottom: 16, animation: 'fadeInUp 0.25s ease-out both',
      }}>
        <label style={{ fontSize: 13, fontWeight: 500, display: 'block', marginBottom: 4 }}>Package ID</label>
        <div style={{ display: 'flex', gap: 8 }}>
          <input
            value={packageId}
            onChange={(e) => setPackageId(e.target.value)}
            placeholder="PKG-XXXXXXXX"
            className="agri-input"
            style={{ flex: 1, padding: '10px 12px', fontSize: 14, borderRadius: 8, border: `1px solid ${colors.stoneBorder}`, fontFamily: fonts.mono, boxSizing: 'border-box' }}
          />
          <Button onClick={lookup} disabled={!packageId.trim() || loading}>Lookup</Button>
        </div>
      </div>
      {error && <ErrorBanner message={error} />}
      {loading && <Spinner />}
      {!loading && tests.length > 0 && (
        <div className="agri-stagger">
          {tests.map((t) => (
            <div key={t.testRecordId} className="agri-card" style={{ padding: '12px 16px', marginBottom: 6, cursor: 'default' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ fontSize: 13, fontFamily: fonts.mono, color: colors.ink }}>{t.testRecordId}</span>
                <StatusBadge status={t.result} />
              </div>
              <div style={{ fontSize: 12, color: colors.graphite, marginTop: 4 }}>Value: {t.measuredValue} {t.unit || ''} • {t.testedAt ? new Date(t.testedAt).toLocaleString() : '—'}</div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
