import { useState, useEffect } from 'react';
import { api, ApiError } from '../../core/api';
import { PageHeader, StatusBadge, EmptyState, Spinner, ErrorBanner, Button } from '../../components/common/UI';
import { colors } from '../../core/theme';
import type { Complaint } from '../../types';

export function RetailerComplaintsPage() {
  const [complaints, setComplaints] = useState<Complaint[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [category, setCategory] = useState('');
  const [description, setDescription] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const fetchComplaints = async () => {
    setLoading(true);
    try {
      const data = await api.get<unknown>('/retailers/complaints');
      const typed = data as { content?: Complaint[] } | Complaint[];
      setComplaints(Array.isArray(typed) ? typed : ('content' in typed ? typed.content || [] : []));
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchComplaints(); }, []);

  const submitComplaint = async () => {
    if (!category.trim() || !description.trim()) return;
    setSubmitting(true);
    try {
      await api.post('/retailers/complaints', { body: { category: category.trim(), description: description.trim() } });
      setShowForm(false);
      setCategory('');
      setDescription('');
      fetchComplaints();
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <PageHeader title="Complaints" />
      <div style={{ marginBottom: 16 }}>
        <Button onClick={() => setShowForm(!showForm)}>{showForm ? 'Cancel' : 'File Complaint'}</Button>
      </div>
      {showForm && (
        <div style={{
          backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`, borderRadius: 8,
          padding: 16, marginBottom: 16, animation: 'fadeInUp 0.25s ease-out both',
        }}>
          <div style={{ marginBottom: 12 }}>
            <label style={{ fontSize: 13, fontWeight: 500, display: 'block', marginBottom: 4 }}>Category</label>
            <input value={category} onChange={(e) => setCategory(e.target.value)} className="agri-input"
              style={{ width: '100%', padding: '10px 12px', fontSize: 14, borderRadius: 8, border: `1px solid ${colors.stoneBorder}`, boxSizing: 'border-box' }} />
          </div>
          <div style={{ marginBottom: 12 }}>
            <label style={{ fontSize: 13, fontWeight: 500, display: 'block', marginBottom: 4 }}>Description</label>
            <textarea value={description} onChange={(e) => setDescription(e.target.value)} rows={3} className="agri-input"
              style={{ width: '100%', padding: '10px 12px', fontSize: 14, borderRadius: 8, border: `1px solid ${colors.stoneBorder}`, boxSizing: 'border-box', resize: 'vertical' }} />
          </div>
          <Button onClick={submitComplaint} disabled={submitting || !category.trim() || !description.trim()}>{submitting ? 'Submitting…' : 'Submit'}</Button>
        </div>
      )}
      {error && <ErrorBanner message={error} />}
      {loading && <Spinner />}
      {!loading && complaints.length === 0 && <EmptyState title="No complaints" description="No complaints filed." />}
      {!loading && (
        <div className="agri-stagger">
          {complaints.map((c) => (
            <div key={c.complaintId} className="agri-card" style={{ padding: '12px 16px', marginBottom: 6, cursor: 'default' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ fontSize: 14, fontWeight: 600, color: colors.ink }}>{c.category}</span>
                <StatusBadge status={c.status} />
              </div>
              <div style={{ fontSize: 13, color: colors.graphite, marginTop: 4 }}>{c.description}</div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
