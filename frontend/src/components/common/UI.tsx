import type { ReactNode } from 'react';
import { colors, fonts } from '../../core/theme';

const STATUS_COLORS: Record<string, { bg: string; fg: string }> = {
  CREATED: { bg: '#EDF2EE', fg: colors.forest },
  ACCEPTED: { bg: '#EDF2EE', fg: colors.forest },
  AT_NODAL_CENTER: { bg: '#F0EDE5', fg: colors.olive },
  PACKAGED: { bg: '#F0EDE5', fg: colors.olive },
  IN_TRANSIT: { bg: '#E8EDF0', fg: colors.info },
  TESTING: { bg: '#E8EDF0', fg: colors.info },
  TEST_PASSED: { bg: '#E8F5ED', fg: colors.success },
  TEST_FAILED: { bg: '#FDF2F1', fg: colors.danger },
  AT_MANUFACTURER: { bg: '#E8EDF0', fg: colors.info },
  PROCESSED: { bg: '#E8EDF0', fg: colors.info },
  BUNDLED: { bg: '#F0EDE5', fg: colors.olive },
  AT_DISTRIBUTOR: { bg: '#F0EDE5', fg: colors.olive },
  AT_RETAILER: { bg: '#E8F5ED', fg: colors.success },
  READY_FOR_SALE: { bg: '#E8F5ED', fg: colors.success },
  QUARANTINED: { bg: '#FDF2F1', fg: colors.danger },
  REJECTED: { bg: '#FDF2F1', fg: colors.danger },
  RECALLED: { bg: '#FDF2F1', fg: colors.danger },
  OPEN: { bg: '#FFF4E5', fg: colors.warning },
  RESOLVED: { bg: '#E8F5ED', fg: colors.success },
  VERIFIED: { bg: '#E8F5ED', fg: colors.success },
  NOT_VERIFIED: { bg: '#FDF2F1', fg: colors.danger },
  ACTIVE: { bg: '#E8F5ED', fg: colors.success },
  CONSUMED: { bg: '#F0EDE5', fg: colors.olive },
  EXPIRED: { bg: '#FDF2F1', fg: colors.danger },
  PASS: { bg: '#E8F5ED', fg: colors.success },
  FAIL: { bg: '#FDF2F1', fg: colors.danger },
  PENDING: { bg: '#FFF4E5', fg: colors.warning },
  HIGH: { bg: '#FDF2F1', fg: colors.danger },
  CRITICAL: { bg: '#FDF2F1', fg: colors.danger },
  MEDIUM: { bg: '#FFF4E5', fg: colors.warning },
  LOW: { bg: '#EDF2EE', fg: colors.forest },
};

export function StatusBadge({ status }: { status: string }) {
  const c = STATUS_COLORS[status] || { bg: colors.softPaper, fg: colors.graphite };
  return (
    <span style={{
      display: 'inline-block', padding: '3px 10px', fontSize: 11, fontWeight: 600,
      backgroundColor: c.bg, color: c.fg, borderRadius: 4,
      textTransform: 'uppercase', letterSpacing: '0.04em', fontFamily: fonts.mono,
      transition: 'all 0.2s ease',
    }}>
      {status.replace(/_/g, ' ')}
    </span>
  );
}

export function SectionHeader({ title, action }: { title: string; action?: ReactNode }) {
  return (
    <div style={{
      display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      marginBottom: 12, paddingBottom: 8, borderBottom: `1px solid ${colors.stoneBorder}`,
    }}>
      <h2 style={{
        fontSize: 14, fontWeight: 600, color: colors.ink, textTransform: 'uppercase',
        letterSpacing: '0.05em', fontFamily: fonts.heading, margin: 0,
      }}>
        {title}
      </h2>
      {action}
    </div>
  );
}

export function DataRow({ label, value, mono }: { label: string; value: ReactNode; mono?: boolean }) {
  return (
    <div style={{ display: 'flex', justifyContent: 'space-between', padding: '6px 0', borderBottom: `1px solid ${colors.softPaper}` }}>
      <span style={{ fontSize: 13, color: colors.graphite }}>{label}</span>
      <span style={{
        fontSize: 13, color: colors.ink, fontWeight: 500,
        fontFamily: mono ? fonts.mono : fonts.body,
        textAlign: 'right', maxWidth: '60%', wordBreak: 'break-all',
      }}>
        {value || '—'}
      </span>
    </div>
  );
}

type ButtonVariant = 'primary' | 'secondary' | 'danger' | 'ghost';

const BUTTON_CLASS: Record<ButtonVariant, string> = {
  primary: 'agri-btn agri-btn-primary',
  secondary: 'agri-btn agri-btn-secondary',
  danger: 'agri-btn agri-btn-danger',
  ghost: 'agri-btn agri-btn-ghost',
};

const BUTTON_SIZE: Record<string, React.CSSProperties> = {
  sm: { padding: '6px 12px', fontSize: 13 },
  md: { padding: '10px 18px', fontSize: 14 },
  lg: { padding: '12px 24px', fontSize: 15 },
};

export function Button({
  children, variant = 'primary', size = 'md', disabled, onClick, fullWidth, type = 'button', loading, style: customStyle,
}: {
  children: ReactNode;
  variant?: ButtonVariant;
  size?: 'sm' | 'md' | 'lg';
  disabled?: boolean;
  onClick?: () => void;
  fullWidth?: boolean;
  type?: 'button' | 'submit';
  loading?: boolean;
  style?: React.CSSProperties;
}) {
  const baseStyle: React.CSSProperties = {
    ...BUTTON_SIZE[size],
    fontWeight: 600,
    borderRadius: 8,
    cursor: disabled || loading ? 'not-allowed' : 'pointer',
    opacity: disabled || loading ? 0.5 : 1,
    fontFamily: fonts.body,
    width: fullWidth ? '100%' : undefined,
    transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
    ...customStyle,
  };

  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled || loading}
      className={BUTTON_CLASS[variant]}
      style={baseStyle}
    >
      {loading && (
        <span style={{
          display: 'inline-block', width: 14, height: 14,
          border: '2px solid rgba(255,255,255,0.3)',
          borderTopColor: 'white', borderRadius: '50%',
          animation: 'spin 0.6s linear infinite',
          marginRight: 8, verticalAlign: 'middle',
        }} />
      )}
      {children}
    </button>
  );
}

export function EmptyState({ title, description }: { title: string; description: string }) {
  return (
    <div style={{ padding: '48px 24px', textAlign: 'center', animation: 'fadeInUp 0.3s ease-out both' }}>
      <div style={{
        width: 48, height: 48, borderRadius: '50%', backgroundColor: colors.paleSage,
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        margin: '0 auto 16px', fontSize: 20, color: colors.sage,
      }}>
        ◇
      </div>
      <div style={{ fontSize: 14, fontWeight: 600, color: colors.ink, marginBottom: 4 }}>{title}</div>
      <div style={{ fontSize: 13, color: colors.graphite }}>{description}</div>
    </div>
  );
}

export function Spinner({ text }: { text?: string }) {
  return (
    <div style={{ padding: '48px 24px', textAlign: 'center', animation: 'fadeIn 0.2s ease-out both' }}>
      <div style={{
        width: 28, height: 28, border: `2.5px solid ${colors.stoneBorder}`,
        borderTopColor: colors.forest, borderRadius: '50%',
        animation: 'spin 0.8s linear infinite', margin: '0 auto 12px',
      }} />
      {text && <div style={{ fontSize: 13, color: colors.graphite }}>{text}</div>}
    </div>
  );
}

export function ErrorBanner({ message, onRetry }: { message: string; onRetry?: () => void }) {
  return (
    <div style={{
      padding: '12px 16px', backgroundColor: '#FDF2F1', border: `1px solid #F0D0CD`,
      borderRadius: 8, marginBottom: 16, display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 12,
      animation: 'fadeInUp 0.2s ease-out both',
    }}>
      <span style={{ fontSize: 13, color: colors.danger }}>{message}</span>
      {onRetry && (
        <button onClick={onRetry} style={{
          fontSize: 13, fontWeight: 600, color: colors.danger, background: 'none',
          border: 'none', cursor: 'pointer', fontFamily: fonts.body, whiteSpace: 'nowrap',
          transition: 'opacity 0.15s',
        }}>
          Retry
        </button>
      )}
    </div>
  );
}

interface TimelineStep {
  label: string;
  status: 'completed' | 'current' | 'upcoming' | 'skipped';
  detail?: string;
}

export function Timeline({ steps }: { steps: TimelineStep[] }) {
  return (
    <div style={{ padding: '8px 0' }}>
      {steps.map((step, i) => (
        <div key={i} style={{
          display: 'flex', gap: 12,
          animation: `fadeInUp 0.3s ease-out both`,
          animationDelay: `${i * 50}ms`,
        }}>
          
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: 20 }}>
            <div style={{
              width: 10, height: 10, borderRadius: '50%', flexShrink: 0,
              backgroundColor: step.status === 'completed' ? colors.success
                : step.status === 'current' ? colors.forest
                : step.status === 'skipped' ? colors.danger
                : colors.stoneBorder,
              border: step.status === 'current' ? `3px solid ${colors.paleSage}` : undefined,
              transition: 'all 0.2s ease',
            }} />
            {i < steps.length - 1 && (
              <div style={{
                width: 1, flex: 1, minHeight: 24,
                backgroundColor: step.status === 'completed' ? colors.success : colors.stoneBorder,
              }} />
            )}
          </div>
          
          <div style={{ paddingBottom: 16 }}>
            <div style={{
              fontSize: 13, fontWeight: 600,
              color: step.status === 'skipped' ? colors.danger : colors.ink,
            }}>
              {step.label}
            </div>
            {step.detail && (
              <div style={{ fontSize: 12, color: colors.graphite, marginTop: 2 }}>{step.detail}</div>
            )}
          </div>
        </div>
      ))}
    </div>
  );
}

export function PageHeader({ title, subtitle, back }: { title: string; subtitle?: string; back?: () => void }) {
  return (
    <div style={{ marginBottom: 20 }} className="agri-page-header">
      {back && (
        <button onClick={back} style={{
          display: 'flex', alignItems: 'center', gap: 4, fontSize: 13, color: colors.forest,
          background: 'none', border: 'none', cursor: 'pointer', marginBottom: 8, fontFamily: fonts.body, padding: 0,
          transition: 'opacity 0.15s, transform 0.15s',
        }}
        onMouseEnter={(e) => { e.currentTarget.style.opacity = '0.7'; e.currentTarget.style.transform = 'translateX(-2px)'; }}
        onMouseLeave={(e) => { e.currentTarget.style.opacity = '1'; e.currentTarget.style.transform = 'translateX(0)'; }}
        >
          ← Back
        </button>
      )}
      <h1 style={{ fontSize: 22, fontWeight: 700, color: colors.ink, margin: 0, fontFamily: fonts.heading }}>
        {title}
      </h1>
      {subtitle && <p style={{ fontSize: 14, color: colors.graphite, margin: '4px 0 0' }}>{subtitle}</p>}
    </div>
  );
}
