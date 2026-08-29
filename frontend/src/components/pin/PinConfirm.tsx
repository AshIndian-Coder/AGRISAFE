import { useState, useRef, useEffect } from 'react';
import { colors, fonts } from '../../core/theme';

interface PinConfirmProps {
  open: boolean;
  title: string;
  subtitle: string;
  onConfirm: (pin: string) => Promise<void>;
  onCancel: () => void;
  error?: string | null;
  loading?: boolean;
}

export function PinConfirm({ open, title, subtitle, onConfirm, onCancel, error, loading }: PinConfirmProps) {
  const [digits, setDigits] = useState<string[]>(['', '', '', '', '', '']);
  const inputRefs = useRef<(HTMLInputElement | null)[]>([]);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (open) {
      setDigits(['', '', '', '', '', '']);
      setSubmitting(false);
      setTimeout(() => inputRefs.current[0]?.focus(), 150);
    }
  }, [open]);

  if (!open) return null;

  const handleChange = (index: number, value: string) => {
    if (!/^\d*$/.test(value)) return;
    const newDigits = [...digits];
    newDigits[index] = value.slice(-1);
    setDigits(newDigits);

    if (value && index < 5) {
      inputRefs.current[index + 1]?.focus();
    }

    if (newDigits.every((d) => d !== '')) {
      const pin = newDigits.join('');
      setSubmitting(true);
      onConfirm(pin).finally(() => setSubmitting(false));
    }
  };

  const handleKeyDown = (index: number, e: React.KeyboardEvent) => {
    if (e.key === 'Backspace' && !digits[index] && index > 0) {
      inputRefs.current[index - 1]?.focus();
    }
    if (e.key === 'Escape') {
      onCancel();
    }
  };

  const handlePaste = (e: React.ClipboardEvent) => {
    e.preventDefault();
    const pasted = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6);
    if (pasted.length === 0) return;
    const newDigits = pasted.split('').concat(Array(6 - pasted.length).fill(''));
    setDigits(newDigits.slice(0, 6));
    const focusIndex = Math.min(pasted.length, 5);
    inputRefs.current[focusIndex]?.focus();

    if (pasted.length === 6) {
      setSubmitting(true);
      onConfirm(pasted).finally(() => setSubmitting(false));
    }
  };

  const isBusy = loading || submitting;

  return (
    <div
      style={{
        position: 'fixed', inset: 0, zIndex: 1000,
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        backgroundColor: 'rgba(32, 38, 34, 0.6)', backdropFilter: 'blur(8px)',
        animation: 'fadeIn 0.2s ease-out both',
      }}
      onClick={(e) => { if (e.target === e.currentTarget && !isBusy) onCancel(); }}
    >
      <div
        style={{
          backgroundColor: colors.white, borderRadius: 14, padding: '32px 28px',
          width: '100%', maxWidth: 380, margin: 16,
          boxShadow: '0 24px 64px rgba(0,0,0,0.2)',
          animation: 'fadeInScale 0.25s ease-out both',
        }}
        role="dialog" aria-label="PIN confirmation"
      >
        <div style={{
          marginBottom: 8, fontSize: 11, fontWeight: 600, color: colors.sage,
          textTransform: 'uppercase', letterSpacing: '0.08em',
        }}>
          Confirm Action
        </div>
        <div style={{ fontSize: 18, fontWeight: 600, color: colors.ink, marginBottom: 4, fontFamily: fonts.heading }}>
          {title}
        </div>
        <div style={{ fontSize: 14, color: colors.graphite, marginBottom: 28 }}>
          {subtitle}
        </div>

        
        <div style={{ display: 'flex', gap: 8, justifyContent: 'center', marginBottom: 8 }}>
          {digits.map((d, i) => (
            <input
              key={i}
              ref={(el) => { inputRefs.current[i] = el; }}
              type="password"
              inputMode="numeric"
              maxLength={1}
              value={d}
              onChange={(e) => handleChange(i, e.target.value)}
              onKeyDown={(e) => handleKeyDown(i, e)}
              onPaste={i === 0 ? handlePaste : undefined}
              disabled={isBusy}
              aria-label={`PIN digit ${i + 1}`}
              style={{
                width: 44, height: 52, textAlign: 'center', fontSize: 22, fontWeight: 600,
                border: `1.5px solid ${d ? colors.forest : colors.stoneBorder}`,
                borderRadius: 8, outline: 'none',
                backgroundColor: d ? colors.paleSage : colors.softPaper,
                color: colors.ink, fontFamily: fonts.mono,
                transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
                transform: d ? 'scale(1.05)' : 'scale(1)',
              }}
              onFocus={(e) => {
                e.target.style.borderColor = colors.forest;
                e.target.style.boxShadow = '0 0 0 3px rgba(40, 72, 60, 0.1)';
                e.target.style.transform = 'scale(1.08)';
              }}
              onBlur={(e) => {
                e.target.style.borderColor = d ? colors.forest : colors.stoneBorder;
                e.target.style.boxShadow = 'none';
                e.target.style.transform = d ? 'scale(1.05)' : 'scale(1)';
              }}
            />
          ))}
        </div>

        
        {error && (
          <div style={{
            fontSize: 13, color: colors.danger, textAlign: 'center', marginBottom: 12,
            padding: '8px 12px', backgroundColor: '#FDF2F1', borderRadius: 6,
            animation: 'fadeInUp 0.2s ease-out both',
          }}>
            {error}
          </div>
        )}

        
        <div style={{ display: 'flex', gap: 12, marginTop: 20 }}>
          <button
            onClick={onCancel}
            disabled={isBusy}
            className="agri-btn agri-btn-secondary"
            style={{
              flex: 1, padding: '11px 0', fontSize: 14, fontWeight: 500,
              borderRadius: 8, fontFamily: fonts.body,
              cursor: isBusy ? 'not-allowed' : 'pointer',
            }}
          >
            Cancel
          </button>
          <button
            disabled={isBusy || digits.some((d) => !d)}
            className="agri-btn agri-btn-primary"
            style={{
              flex: 1, padding: '11px 0', fontSize: 14, fontWeight: 600,
              borderRadius: 8, fontFamily: fonts.body,
              cursor: isBusy || digits.some((d) => !d) ? 'not-allowed' : 'pointer',
              opacity: isBusy || digits.some((d) => !d) ? 0.5 : 1,
            }}
          >
            {isBusy ? 'Confirming…' : 'Confirm'}
          </button>
        </div>
      </div>
    </div>
  );
}
