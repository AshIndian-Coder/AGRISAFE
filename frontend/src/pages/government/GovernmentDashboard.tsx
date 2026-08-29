import { useState, useEffect } from 'react';

import { api, ApiError } from '../../core/api';
import { useAuthStore } from '../../core/auth-store';
import { connectInAppWallet, recordAuthorityInspection } from '../../core/blockchain';
import { getRequestId } from '../../core/blockchain-mapping';
import { PageHeader, StatusBadge, EmptyState, ErrorBanner, Button } from '../../components/common/UI';
import { SkeletonStatCard, SkeletonList } from '../../components/common/Skeleton';
import { PinConfirm } from '../../components/pin/PinConfirm';
import { colors, fonts } from '../../core/theme';
import type { Flag, PagedResponse } from '../../types';

export function GovernmentDashboard() {
  const [flags, setFlags] = useState<Flag[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [resolveFlagId, setResolveFlagId] = useState<number | null>(null);
  const [resolveText, setResolveText] = useState('');
  const [showPin, setShowPin] = useState(false);
  const [resolving, setResolving] = useState(false);

  const fetchFlags = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.get<PagedResponse<Flag>>('/government/flags', { query: { page: '0', size: '50' } });
      setFlags(data.content || []);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed to load flags');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchFlags(); }, []);

  const startResolve = (flagId: number) => {
    setResolveFlagId(flagId);
    setResolveText('');
    setShowPin(true);
  };

  const handlePinConfirm = async (_pin: string) => {
    if (!resolveFlagId || !resolveText.trim()) return;
    setResolving(true);
    try {
      await api.post(`/government/flags/${resolveFlagId}/resolve`, {
        query: { resolution: resolveText.trim() },
      });

      try {
        const walletAddr = useAuthStore.getState().walletAddress;
        if (walletAddr) {
          const account = await connectInAppWallet(useAuthStore.getState().walletEmail || '');
          if (account) {
            const flag = flags.find((f) => f.id === resolveFlagId);
            const chainRequestId = flag?.entityId ? (getRequestId(flag.entityId) ?? resolveFlagId) : resolveFlagId;
            await recordAuthorityInspection(
              account,
              chainRequestId,
              'RESOLVED',
              JSON.stringify({ resolution: resolveText.trim(), entityType: flag?.entityType, entityId: flag?.entityId }),
            );
          }
        }
      } catch {
        console.warn('Blockchain authority inspection failed — resolved on backend only');
      }

      setShowPin(false);
      fetchFlags();
    } catch (err) {
      setShowPin(false);
      setError(err instanceof ApiError ? err.message : 'Resolution failed');
    } finally {
      setResolving(false);
    }
  };

  return (
    <div>
      <PageHeader title="Government Oversight" subtitle="Fraud flags and investigation" />

      
      <div className="agri-stagger" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(120px, 1fr))', gap: 8, marginBottom: 16 }}>
        <div className="agri-stat-card">
          <div style={{ fontSize: 24, fontWeight: 700, color: colors.danger, lineHeight: 1 }}>
            {flags.filter((f) => f.status === 'OPEN').length}
          </div>
          <div style={{ fontSize: 11, color: colors.graphite, marginTop: 6, fontWeight: 500 }}>Open Flags</div>
        </div>
        <div className="agri-stat-card">
          <div style={{ fontSize: 24, fontWeight: 700, color: colors.success, lineHeight: 1 }}>
            {flags.filter((f) => f.status === 'RESOLVED').length}
          </div>
          <div style={{ fontSize: 11, color: colors.graphite, marginTop: 6, fontWeight: 500 }}>Resolved</div>
        </div>
      </div>

      {error && <ErrorBanner message={error} onRetry={fetchFlags} />}
      {loading && (
        <>
          <div className="agri-stagger" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(120px, 1fr))', gap: 8, marginBottom: 16 }}>
            <SkeletonStatCard /><SkeletonStatCard />
          </div>
          <SkeletonList count={5} />
        </>
      )}

      {!loading && !error && flags.length === 0 && (
        <EmptyState title="No flags" description="No fraud flags have been raised." />
      )}

      {!loading && (
        <div className="agri-stagger">
          {flags.map((flag) => (
            <div
              key={flag.id}
              className="agri-card"
              style={{ padding: '14px 16px', marginBottom: 6, cursor: 'default' }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', marginBottom: 6 }}>
                <div>
                  <div style={{ fontSize: 14, fontWeight: 600, color: colors.ink }}>
                    Flag #{flag.id}
                    {flag.flagType && <span style={{ fontSize: 12, color: colors.graphite, marginLeft: 8, fontFamily: fonts.mono }}>{flag.flagType}</span>}
                  </div>
                  {flag.description && <div style={{ fontSize: 13, color: colors.graphite, marginTop: 4 }}>{flag.description}</div>}
                  <div style={{ fontSize: 12, color: colors.graphite, marginTop: 4 }}>
                    {flag.entityType && <span>{flag.entityType}:{flag.entityId} • </span>}
                    {flag.createdAt ? new Date(flag.createdAt).toLocaleString() : '—'}
                  </div>
                </div>
                <div style={{ display: 'flex', gap: 4, alignItems: 'center' }}>
                  {flag.severity && <StatusBadge status={flag.severity} />}
                  <StatusBadge status={flag.status} />
                </div>
              </div>

              {flag.status === 'OPEN' && (
                <div style={{ marginTop: 8 }}>
                  <input
                    value={resolveFlagId === flag.id ? resolveText : ''}
                    onChange={(e) => { setResolveFlagId(flag.id); setResolveText(e.target.value); }}
                    placeholder="Resolution notes…"
                    className="agri-input"
                    style={{
                      width: '100%', padding: '8px 10px', fontSize: 13, borderRadius: 6,
                      border: `1px solid ${colors.stoneBorder}`, outline: 'none', marginBottom: 6,
                      boxSizing: 'border-box',
                    }}
                  />
                  <Button size="sm" onClick={() => startResolve(flag.id)} disabled={!resolveText.trim() || resolveFlagId !== flag.id}>
                    Resolve (PIN)
                  </Button>
                </div>
              )}

              {flag.resolution && (
                <div style={{ fontSize: 12, color: colors.success, marginTop: 6 }}>
                  Resolution: {flag.resolution}
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      <PinConfirm
        open={showPin}
        title="Resolve Flag"
        subtitle={`Resolve flag #${resolveFlagId}. This is a permanent administrative action.`}
        onConfirm={handlePinConfirm}
        onCancel={() => setShowPin(false)}
        loading={resolving}
      />
    </div>
  );
}
