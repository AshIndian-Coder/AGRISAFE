import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, ApiError } from '../../core/api';
import { PageHeader, EmptyState, Spinner, ErrorBanner, Button } from '../../components/common/UI';
import { colors } from '../../core/theme';
import type { Complaint, PagedResponse } from '../../types';

export function FarmerComplaintsPage() {
  const navigate = useNavigate();
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
      const data = await api.get<PagedResponse<Complaint>>('/farmer/complaints');
      setComplaints(data.content || []);
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
      await api.post('/farmer/complaints', { body: { category: category.trim(), description: description.trim() } });
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
      <PageHeader title="Complaints" subtitle="File and track complaints" back={() => navigate('/farmer')} />

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
            <input
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              placeholder="e.g. Quality, Delivery"
              className="agri-input"
              style={{ width: '100%', padding: '10px 12px', fontSize: 14, borderRadius: 8, border: `1px solid ${colors.stoneBorder}`, boxSizing: 'border-box' }}
            />
          </div>
          <div style={{ marginBottom: 12 }}>
            <label style={{ fontSize: 13, fontWeight: 500, display: 'block', marginBottom: 4 }}>Description</label>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows={3}
              className="agri-input"
              style={{ width: '100%', padding: '10px 12px', fontSize: 14, borderRadius: 8, border: `1px solid ${colors.stoneBorder}`, boxSizing: 'border-box', resize: 'vertical' }}
            />
          </div>
          <Button onClick={submitComplaint} disabled={submitting || !category.trim() || !description.trim()}>
            {submitting ? 'Submitting…' : 'Submit'}
          </Button>
        </div>
      )}

      {error && <ErrorBanner message={error} />}
      {loading && <Spinner />}
      {!loading && complaints.length === 0 && <EmptyState title="No complaints" description="No complaints filed yet." />}

      {!loading && (
        <div className="agri-stagger">
          {complaints.map((c) => (
            <div key={c.complaintId} className="agri-card" style={{ padding: '12px 16px', marginBottom: 6, cursor: 'default' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ fontSize: 14, fontWeight: 600, color: colors.ink }}>{c.category || 'Complaint'}</span>
                <span style={{
                  fontSize: 12, color: c.status === 'RESOLVED' ? colors.success : colors.warning,
                  fontWeight: 600,
                }}>
                  {c.status}
                </span>
              </div>
              <div style={{ fontSize: 13, color: colors.graphite, marginTop: 4 }}>{c.description}</div>
              {c.resolution && (
                <div style={{ fontSize: 12, color: colors.success, marginTop: 4 }}>Resolution: {c.resolution}</div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
