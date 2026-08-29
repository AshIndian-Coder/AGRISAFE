import { useState, useEffect } from 'react';
import { api, ApiError } from '../../core/api';
import { PageHeader, StatusBadge, EmptyState, Spinner, ErrorBanner } from '../../components/common/UI';
import { colors } from '../../core/theme';
import type { Complaint, PagedResponse } from '../../types';

export function GovernmentComplaintsPage() {
  const [complaints, setComplaints] = useState<Complaint[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setLoading(true);
    api.get<PagedResponse<Complaint>>('/government/complaints')
      .then((data) => setComplaints(data.content || []))
      .catch((err) => setError(err instanceof ApiError ? err.message : 'Failed'))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div>
      <PageHeader title="Complaints" subtitle="All system complaints" />
      {error && <ErrorBanner message={error} />}
      {loading && <Spinner />}
      {!loading && complaints.length === 0 && <EmptyState title="No complaints" description="No complaints have been filed." />}
      {!loading && (
        <div className="agri-stagger">
          {complaints.map((c) => (
            <div key={c.complaintId} className="agri-card" style={{ padding: '12px 16px', marginBottom: 6, cursor: 'default' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ fontSize: 14, fontWeight: 600, color: colors.ink }}>{c.category}</span>
                <StatusBadge status={c.status} />
              </div>
              <div style={{ fontSize: 13, color: colors.graphite, marginTop: 4 }}>{c.description}</div>
              {c.resolution && <div style={{ fontSize: 12, color: colors.success, marginTop: 4 }}>Resolution: {c.resolution}</div>}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
