import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, ApiError } from '../../core/api';
import { useAuthStore } from '../../core/auth-store';
import { connectInAppWallet, createManufacturedBatch } from '../../core/blockchain';
import { getRawBatchId, mapMfgLotToMfgBatch } from '../../core/blockchain-mapping';
import { PageHeader, Button } from '../../components/common/UI';
import { PinConfirm } from '../../components/pin/PinConfirm';

import { colors, fonts } from '../../core/theme';

export function ManufacturerCreateLotPage() {
  const navigate = useNavigate();
  const [showPin, setShowPin] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [form, setForm] = useState({
    productId: '',
    inputLotIds: '',
    productionQuantity: '',
    unit: '',
    facilityName: '',
    notes: '',
  });

  const set = (key: string, val: string) => setForm((f) => ({ ...f, [key]: val }));

  const handleSubmit = () => {
    if (!form.productId || !form.inputLotIds || !form.productionQuantity) return;
    setShowPin(true);
  };

  const handlePinConfirm = async (_pin: string) => {
    setSubmitting(true);
    setError(null);
    try {
      const inputLotIds = form.inputLotIds.split(',').map((s) => s.trim()).filter(Boolean);
      await api.post('/manufacturers/lots', {
        body: {
          productId: parseInt(form.productId, 10),
          inputLotIds,
          productionQuantity: parseFloat(form.productionQuantity),
          unit: form.unit || undefined,
          facilityName: form.facilityName || undefined,
          notes: form.notes || undefined,
        },
      });

      try {
        const walletAddr = useAuthStore.getState().walletAddress;
        if (walletAddr) {
          const account = await connectInAppWallet(useAuthStore.getState().walletEmail || '');
          if (account) {
            const rawBatchIds = inputLotIds
              .map((id) => getRawBatchId(id))
              .filter((id): id is number => id !== null);
            const txResult = await createManufacturedBatch(
              account,
              rawBatchIds.length > 0 ? rawBatchIds : [0],
              form.facilityName || 'Manufactured Product',
              parseFloat(form.productionQuantity),
            );
            const chainMfgBatchId = Math.abs(parseInt(txResult.transactionHash.slice(-8), 16)) % 100000;
            mapMfgLotToMfgBatch('pending', chainMfgBatchId);
          }
        }
      } catch {
        console.warn('Blockchain manufactured batch creation failed — lot created on backend only');
      }

      setShowPin(false);
      navigate('/manufacturer');
    } catch (err) {
      setShowPin(false);
      setError(err instanceof ApiError ? err.message : 'Creation failed');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <PageHeader title="Create Manufacturing Lot" subtitle="Merge input lots into a manufacturing lot" back={() => navigate('/manufacturer')} />

      <div style={{
        backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`, borderRadius: 8,
        padding: 20, animation: 'fadeInUp 0.3s ease-out both',
      }}>
        <Field label="Product ID *">
          <input type="number" value={form.productId} onChange={(e) => set('productId', e.target.value)} placeholder="Product ID" className="agri-input" style={inputStyle} />
        </Field>

        <Field label="Input Lot/Package IDs *">
          <input value={form.inputLotIds} onChange={(e) => set('inputLotIds', e.target.value)} placeholder="LOT-XXX, PKG-YYY" className="agri-input" style={inputStyle} />
          <div style={{ fontSize: 11, color: colors.graphite, marginTop: 2 }}>Comma-separated lot or package IDs to merge</div>
        </Field>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
          <Field label="Production Quantity *">
            <input type="number" value={form.productionQuantity} onChange={(e) => set('productionQuantity', e.target.value)} placeholder="0" className="agri-input" style={inputStyle} />
          </Field>
          <Field label="Unit">
            <input value={form.unit} onChange={(e) => set('unit', e.target.value)} placeholder="kg" className="agri-input" style={inputStyle} />
          </Field>
        </div>

        <Field label="Facility Name">
          <input value={form.facilityName} onChange={(e) => set('facilityName', e.target.value)} placeholder="Plant name" className="agri-input" style={inputStyle} />
        </Field>

        <Field label="Notes">
          <textarea value={form.notes} onChange={(e) => set('notes', e.target.value)} placeholder="Optional" rows={2} className="agri-input" style={{ ...inputStyle, resize: 'vertical' }} />
        </Field>

        {error && (
          <div style={{ fontSize: 13, color: colors.danger, backgroundColor: '#FDF2F1', padding: '10px 14px', borderRadius: 8, marginBottom: 16 }}>{error}</div>
        )}

        <Button onClick={handleSubmit} disabled={!form.productId || !form.inputLotIds || !form.productionQuantity} fullWidth>Create Manufacturing Lot</Button>
      </div>

      <PinConfirm
        open={showPin}
        title="Create Manufacturing Lot"
        subtitle="This creates a permanent manufacturing traceability record."
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
  color: colors.ink, backgroundColor: colors.softPaper, boxSizing: 'border-box', fontFamily: fonts.body,
  transition: 'border-color 0.2s ease, box-shadow 0.2s ease',
};
