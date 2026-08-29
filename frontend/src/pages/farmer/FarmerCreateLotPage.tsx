import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, ApiError } from '../../core/api';
import { useAuthStore } from '../../core/auth-store';
import { connectInAppWallet, createRequest } from '../../core/blockchain';
import { mapLotToRequest } from '../../core/blockchain-mapping';
import { PageHeader, Button, Spinner } from '../../components/common/UI';
import { PinConfirm } from '../../components/pin/PinConfirm';
import { useGps } from '../../hooks/useGps';
import { colors, fonts } from '../../core/theme';
import type { Product, Lot } from '../../types';

export function FarmerCreateLotPage() {
  const navigate = useNavigate();
  const gps = useGps();
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showPin, setShowPin] = useState(false);

  const [form, setForm] = useState({
    productId: '',
    quantity: '',
    unit: '',
    originAddress: '',
    estimatedValue: '',
    notes: '',
  });

  useEffect(() => {
    api.get<Product[]>('/products', { auth: false })
      .then(setProducts)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const set = (key: string, val: string) => setForm((f) => ({ ...f, [key]: val }));

  const handleSubmit = () => {
    if (!form.productId || !form.quantity) return;
    gps.request();
    setShowPin(true);
  };

  const handlePinConfirm = async (_pin: string) => {
    setSubmitting(true);
    setError(null);
    try {
      const lot = await api.post<Lot>('/farmer/lots', {
        body: {
          productId: parseInt(form.productId, 10),
          quantity: parseFloat(form.quantity),
          unit: form.unit || undefined,
          latitude: gps.latitude || undefined,
          longitude: gps.longitude || undefined,
          originAddress: form.originAddress || undefined,
          estimatedValue: form.estimatedValue ? parseFloat(form.estimatedValue) : undefined,
          notes: form.notes || undefined,
        },
      });

      try {
        const walletAddr = useAuthStore.getState().walletAddress;
        if (walletAddr) {
          const account = await connectInAppWallet(useAuthStore.getState().walletEmail || '');
          if (account) {
            const productName = products.find((p) => String(p.id) === form.productId)?.name || 'Unknown';
            const txResult = await createRequest(
              account,
              productName,
              parseFloat(form.quantity),
              JSON.stringify({ lotId: lot.lotId, origin: form.originAddress }),
            );
            const chainRequestId = Math.abs(parseInt(txResult.transactionHash.slice(-8), 16)) % 100000;
            mapLotToRequest(lot.lotId, chainRequestId);
          }
        }
      } catch {
        console.warn('Blockchain request creation failed — lot created on backend only');
      }

      setShowPin(false);
      navigate(`/farmer/lots/${lot.lotId}`);
    } catch (err) {
      setShowPin(false);
      setError(err instanceof ApiError ? err.message : 'Lot creation failed');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <PageHeader title="Create Lot" subtitle="Register a new agricultural lot" back={() => navigate('/farmer')} />

      {loading ? (
        <Spinner text="Loading products…" />
      ) : (
        <div style={{
          backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`, borderRadius: 8,
          padding: 20, animation: 'fadeInUp 0.3s ease-out both',
        }}>
          
          <Field label="Product *">
            <select
              value={form.productId}
              onChange={(e) => set('productId', e.target.value)}
              className="agri-input"
              style={selectStyle}
            >
              <option value="">Select product</option>
              {products.map((p) => (
                <option key={p.id} value={p.id}>{p.name}{p.category ? ` (${p.category})` : ''}</option>
              ))}
            </select>
          </Field>

          
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
            <Field label="Quantity *">
              <input type="number" value={form.quantity} onChange={(e) => set('quantity', e.target.value)} placeholder="0" className="agri-input" style={inputStyle} />
            </Field>
            <Field label="Unit">
              <input type="text" value={form.unit} onChange={(e) => set('unit', e.target.value)} placeholder="kg, tonnes" className="agri-input" style={inputStyle} />
            </Field>
          </div>

          
          <Field label="Origin Address">
            <input type="text" value={form.originAddress} onChange={(e) => set('originAddress', e.target.value)} placeholder="Farm address" className="agri-input" style={inputStyle} />
          </Field>

          
          <Field label="Estimated Value (₹)">
            <input type="number" value={form.estimatedValue} onChange={(e) => set('estimatedValue', e.target.value)} placeholder="0.00" className="agri-input" style={inputStyle} />
          </Field>

          
          <Field label="Notes">
            <textarea value={form.notes} onChange={(e) => set('notes', e.target.value)} placeholder="Optional notes" rows={2} className="agri-input" style={{ ...inputStyle, resize: 'vertical' }} />
          </Field>

          
          <div style={{
            fontSize: 12, color: gps.status === 'ready' ? colors.success : colors.graphite, marginBottom: 16,
            padding: '8px 12px', borderRadius: 6,
            backgroundColor: gps.status === 'ready' ? '#E8F5ED' : colors.softPaper,
            transition: 'all 0.2s ease',
          }}>
            GPS: {gps.status === 'ready' ? `Ready (${gps.accuracy?.toFixed(0)}m)` : gps.status}
            {gps.status !== 'ready' && gps.status !== 'idle' && (
              <span style={{ color: colors.warning }}> — GPS improves traceability</span>
            )}
          </div>

          {error && (
            <div style={{ fontSize: 13, color: colors.danger, backgroundColor: '#FDF2F1', padding: '10px 14px', borderRadius: 8, marginBottom: 16 }}>{error}</div>
          )}

          <Button onClick={handleSubmit} disabled={!form.productId || !form.quantity} fullWidth>
            Create Lot
          </Button>
        </div>
      )}

      <PinConfirm
        open={showPin}
        title="Create Lot"
        subtitle={`This will create a new lot: ${form.quantity} units of product. This action creates a permanent traceability event.`}
        onConfirm={handlePinConfirm}
        onCancel={() => setShowPin(false)}
        loading={submitting}
        error={error}
      />
    </div>
  );
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div style={{ marginBottom: 14 }}>
      <label style={{ display: 'block', fontSize: 13, fontWeight: 500, color: colors.ink, marginBottom: 4 }}>{label}</label>
      {children}
    </div>
  );
}

const inputStyle: React.CSSProperties = {
  width: '100%', padding: '10px 12px', fontSize: 14, borderRadius: 8,
  border: `1px solid ${colors.stoneBorder}`, outline: 'none',
  color: colors.ink, backgroundColor: colors.softPaper, boxSizing: 'border-box',
  fontFamily: fonts.body,
  transition: 'border-color 0.2s ease, box-shadow 0.2s ease',
};

const selectStyle: React.CSSProperties = {
  ...inputStyle, appearance: 'none' as const,
  backgroundImage: `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%2368716B' d='M6 8L1 3h10z'/%3E%3C/svg%3E")`,
  backgroundRepeat: 'no-repeat', backgroundPosition: 'right 12px center',
};
