import { useState, useEffect, useCallback, useRef } from 'react';
import { api } from '../../core/api';
import { colors, fonts } from '../../core/theme';

interface Props {
  qrId: string;
  compact?: boolean;
}

const ROTATION_SECONDS = 30;

export function RotatingCodeDisplay({ qrId, compact = false }: Props) {
  const [code, setCode] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [secondsLeft, setSecondsLeft] = useState(ROTATION_SECONDS);
  const [revealed, setRevealed] = useState(false);
  const intervalRef = useRef<ReturnType<typeof setInterval>>(null);

  const fetchCode = useCallback(async () => {
    try {
      const result = await api.get<string>(`/qr/${qrId}/current-code`);
      setCode(result);
      setError(null);
      setSecondsLeft(ROTATION_SECONDS);
    } catch {
      setError('Failed to load rotating code');
    }
  }, [qrId]);

  useEffect(() => {
    if (!revealed) return;
    fetchCode();
    intervalRef.current = setInterval(() => {
      setSecondsLeft((prev) => {
        if (prev <= 1) {
          fetchCode();
          return ROTATION_SECONDS;
        }
        return prev - 1;
      });
    }, 1000);
    return () => {
      if (intervalRef.current) clearInterval(intervalRef.current);
    };
  }, [revealed, fetchCode]);

  if (!revealed) {
    if (compact) {
      return (
        <button
          onClick={() => setRevealed(true)}
          style={{
            display: 'inline-flex', alignItems: 'center', gap: 6,
            padding: '4px 10px', borderRadius: 6, border: `1px solid ${colors.stoneBorder}`,
            backgroundColor: colors.softPaper, cursor: 'pointer', fontSize: 12,
            color: colors.graphite,
          }}
        >
          🔒 Tap to reveal rotating code
        </button>
      );
    }
    return (
      <div style={{
        backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`,
        borderRadius: 8, padding: 16, marginBottom: 16, textAlign: 'center',
      }}>
        <div style={{ fontSize: 13, fontWeight: 600, color: colors.ink, marginBottom: 8 }}>
          Rotating Verification Code
        </div>
        <button
          onClick={() => setRevealed(true)}
          style={{
            padding: '10px 20px', borderRadius: 8, border: `1px solid ${colors.stoneBorder}`,
            backgroundColor: colors.softPaper, cursor: 'pointer', fontSize: 13,
            fontWeight: 600, color: colors.forest,
          }}
        >
          🔒 Tap to reveal rotating code
        </button>
        <div style={{ marginTop: 8, fontSize: 11, color: colors.graphite }}>
          Code refreshes every {ROTATION_SECONDS} seconds. Revealed for verification.
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div style={{
        padding: compact ? '6px 10px' : '10px 14px',
        borderRadius: 8, fontSize: 12,
        backgroundColor: '#FFF4E5', color: colors.warning,
      }}>
        {error}
      </div>
    );
  }

  if (!code) {
    return (
      <div style={{
        padding: compact ? '6px 10px' : '10px 14px',
        borderRadius: 8, fontSize: 12,
        backgroundColor: colors.warmPaper, color: colors.graphite,
      }}>
        Loading rotating code…
      </div>
    );
  }

  const progress = secondsLeft / ROTATION_SECONDS;
  const isUrgent = secondsLeft <= 5;

  if (compact) {
    return (
      <div style={{
        display: 'inline-flex', alignItems: 'center', gap: 8,
        padding: '4px 10px', borderRadius: 6,
        backgroundColor: colors.softPaper, border: `1px solid ${colors.stoneBorder}`,
      }}>
        <span style={{
          fontFamily: fonts.mono, fontSize: 14, fontWeight: 700,
          letterSpacing: 2, color: isUrgent ? colors.danger : colors.forest,
        }}>
          {code}
        </span>
        <span style={{
          fontSize: 11, color: isUrgent ? colors.danger : colors.graphite,
          fontVariantNumeric: 'tabular-nums',
        }}>
          {secondsLeft}s
        </span>
        <button onClick={() => setRevealed(false)} style={{
          fontSize: 11, color: colors.graphite, background: 'none', border: 'none', cursor: 'pointer',
        }}>✕</button>
      </div>
    );
  }

  return (
    <div style={{
      backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`,
      borderRadius: 8, padding: 16, marginBottom: 16,
    }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 10 }}>
        <span style={{ fontSize: 13, fontWeight: 600, color: colors.ink }}>
          Rotating Verification Code
        </span>
        <span style={{ fontSize: 11, color: isUrgent ? colors.danger : colors.graphite, fontVariantNumeric: 'tabular-nums' }}>
          Expires in {secondsLeft}s
        </span>
      </div>

      <div style={{
        fontFamily: fonts.mono, fontSize: 28, fontWeight: 700, letterSpacing: 4,
        textAlign: 'center', padding: '12px 0',
        color: isUrgent ? colors.danger : colors.forest,
        backgroundColor: isUrgent ? '#FFF0EF' : colors.softPaper,
        borderRadius: 8, border: `1px solid ${isUrgent ? colors.danger : colors.stoneBorder}`,
      }}>
        {code}
      </div>

      <div style={{ marginTop: 10, height: 4, borderRadius: 2, backgroundColor: colors.paleSage, overflow: 'hidden' }}>
        <div style={{
          height: '100%', width: `${progress * 100}%`, borderRadius: 2,
          backgroundColor: isUrgent ? colors.danger : colors.success, transition: 'width 1s linear',
        }} />
      </div>

      <div style={{ marginTop: 8, fontSize: 11, color: colors.graphite, textAlign: 'center' }}>
        Code refreshes every {ROTATION_SECONDS} seconds.
      </div>
    </div>
  );
}
