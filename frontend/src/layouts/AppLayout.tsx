import { type ReactNode, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuthStore, stageRole } from '../core/auth-store';
import { colors, fonts } from '../core/theme';
import { WalletConnect } from '../components/wallet/WalletConnect';
import { PinConfirm } from '../components/pin/PinConfirm';
import { useTheme } from '../core/theme-context';

interface NavItem {
  label: string;
  path: string;
  icon: string;
}

const ROLE_NAV: Record<string, NavItem[]> = {
  farmer: [
    { label: 'Dashboard', path: '/farmer', icon: '◈' },
    { label: 'Lots', path: '/farmer/lots', icon: '□' },
    { label: 'New Lot', path: '/farmer/lots/new', icon: '+' },
    { label: 'Complaints', path: '/farmer/complaints', icon: '△' },
  ],
  agent: [
    { label: 'Dashboard', path: '/agent', icon: '◈' },
    { label: 'Available', path: '/agent/lots', icon: '□' },
    { label: 'History', path: '/agent/history', icon: '≡' },
    { label: 'Scan QR', path: '/scan', icon: '⊞' },
  ],
  nodal: [
    { label: 'Dashboard', path: '/nodal', icon: '◈' },
    { label: 'Packages', path: '/nodal/packages', icon: '□' },
  ],
  testing: [
    { label: 'Dashboard', path: '/testing', icon: '◈' },
    { label: 'Submit Test', path: '/testing/submit', icon: '+' },
    { label: 'History', path: '/testing/history', icon: '≡' },
  ],
  manufacturer: [
    { label: 'Dashboard', path: '/manufacturer', icon: '◈' },
    { label: 'Lots', path: '/manufacturer/lots', icon: '□' },
    { label: 'New Lot', path: '/manufacturer/lots/new', icon: '+' },
  ],
  distributor: [
    { label: 'Dashboard', path: '/distributor', icon: '◈' },
    { label: 'Bundles', path: '/distributor/bundles', icon: '□' },
  ],
  retailer: [
    { label: 'Dashboard', path: '/retailer', icon: '◈' },
    { label: 'Inventory', path: '/retailer/inventory', icon: '□' },
    { label: 'Complaints', path: '/retailer/complaints', icon: '△' },
  ],
  government: [
    { label: 'Dashboard', path: '/government', icon: '◈' },
    { label: 'Flags', path: '/government/flags', icon: '△' },
    { label: 'Complaints', path: '/government/complaints', icon: '□' },
  ],
  supplier: [
    { label: 'Dashboard', path: '/supplier', icon: '◈' },
    { label: 'Assignments', path: '/supplier/assignments', icon: '□' },
    { label: 'Scan QR', path: '/scan', icon: '⊞' },
  ],
};

export function AppLayout({ children }: { children: ReactNode }) {
  const navigate = useNavigate();
  const location = useLocation();
  const { userName, userType, pin, logout } = useAuthStore();
  const { theme, toggleTheme } = useTheme();
  const role = stageRole(userType);
  const navItems = ROLE_NAV[role] || [];
  const [walletRevealed, setWalletRevealed] = useState(false);
  const [showPinModal, setShowPinModal] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="agri-layout">
      
      <aside className="agri-sidebar">
        <div style={{ padding: '0 20px', marginBottom: 32 }}>
          <div style={{
            fontSize: 18, fontWeight: 700, color: colors.white, letterSpacing: '-0.02em',
            fontFamily: fonts.heading, display: 'flex', alignItems: 'center', gap: 8,
          }}>
            <span style={{
              width: 32, height: 32, borderRadius: 8, backgroundColor: colors.forest,
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              fontSize: 14, color: 'white', fontWeight: 700,
              boxShadow: '0 2px 8px rgba(40, 72, 60, 0.3)',
            }}>
              A
            </span>
            AgriSafe
          </div>
          <div style={{
            fontSize: 11, color: colors.sage, marginTop: 6, textTransform: 'uppercase',
            letterSpacing: '0.08em', paddingLeft: 40,
          }}>
            {role}
          </div>
          {useAuthStore.getState().walletAddress ? (
            walletRevealed ? (
              <div style={{
                fontSize: 10, color: colors.sage, marginTop: 4, paddingLeft: 40,
                fontFamily: 'monospace', opacity: 0.7,
                overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap',
                display: 'flex', alignItems: 'center', gap: 4,
              }}>
                <span style={{ color: colors.success }}>●</span>
                {useAuthStore.getState().walletAddress?.slice(0, 6)}...{useAuthStore.getState().walletAddress?.slice(-4)}
                <button onClick={() => setWalletRevealed(false)} style={{
                  fontSize: 9, color: colors.sage, background: 'none', border: 'none', cursor: 'pointer', marginLeft: 2,
                }}>✕</button>
              </div>
            ) : (
              <button
                onClick={() => setShowPinModal(true)}
                style={{
                  fontSize: 10, color: colors.sage, marginTop: 4, paddingLeft: 40,
                  background: 'none', border: 'none', cursor: 'pointer', textAlign: 'left',
                  fontFamily: 'monospace', padding: 0,
                }}
              >
                🔒 Tap to view wallet
              </button>
            )
          ) : (
            <div style={{
              fontSize: 10, color: colors.sage, marginTop: 4, paddingLeft: 40,
              opacity: 0.6,
            }}>
              No wallet connected
            </div>
          )}
        </div>

        <nav style={{ flex: 1, padding: '0 8px' }}>
          {navItems.map((item, index) => {
            const active = location.pathname === item.path || (item.path !== `/${role}` && location.pathname.startsWith(item.path));
            return (
              <button
                key={item.path}
                onClick={() => navigate(item.path)}
                className={active ? 'agri-nav-active' : ''}
                style={{
                  display: 'flex', alignItems: 'center', gap: 10, width: '100%',
                  padding: '11px 14px', border: 'none', borderRadius: 8, marginBottom: 2,
                  backgroundColor: active ? 'rgba(255,255,255,0.12)' : 'transparent',
                  color: active ? colors.white : colors.paleSage,
                  cursor: 'pointer', fontSize: 14, fontWeight: active ? 600 : 400,
                  textAlign: 'left', fontFamily: fonts.body,
                  transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
                  animation: `slideInLeft 0.3s ease-out both`,
                  animationDelay: `${60 + index * 40}ms`,
                  position: 'relative' as const,
                  overflow: 'hidden',
                }}
              >
                {active && (
                  <span style={{
                    position: 'absolute', left: 0, top: '50%', transform: 'translateY(-50%)',
                    width: 3, height: 20, borderRadius: '0 3px 3px 0',
                    backgroundColor: colors.white, opacity: 0.9,
                  }} />
                )}
                <span style={{
                  width: 22, textAlign: 'center', fontSize: 16,
                  opacity: active ? 1 : 0.6,
                  transition: 'opacity 0.2s ease',
                }}>{item.icon}</span>
                {item.label}
              </button>
            );
          })}
        </nav>

        <div style={{
          padding: '16px 20px', borderTop: '1px solid rgba(255,255,255,0.08)',
          animation: 'fadeIn 0.3s ease-out 0.4s both',
        }}>
          <div style={{
            fontSize: 13, color: colors.paleSage, marginBottom: 8, padding: '0 4px',
            overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap',
          }}>
            {userName}
          </div>
          <button
            onClick={handleLogout}
            style={{
              width: '100%', padding: '9px 0', fontSize: 13, fontWeight: 500,
              border: '1px solid rgba(255,255,255,0.15)', borderRadius: 8,
              backgroundColor: 'transparent', color: colors.paleSage,
              cursor: 'pointer', fontFamily: fonts.body,
              transition: 'all 0.2s ease',
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.backgroundColor = 'rgba(255,255,255,0.08)';
              e.currentTarget.style.borderColor = 'rgba(255,255,255,0.25)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.backgroundColor = 'transparent';
              e.currentTarget.style.borderColor = 'rgba(255,255,255,0.15)';
            }}
          >
            Sign out
          </button>
        </div>

        <PinConfirm
          open={showPinModal}
          title="Verify Identity"
          subtitle="Enter your 6-digit PIN to view wallet address"
          onConfirm={(enteredPin) => {
            if (enteredPin === pin) {
              setWalletRevealed(true);
              setShowPinModal(false);
            } else {
              setShowPinModal(false);
            }
          }}
          onCancel={() => setShowPinModal(false)}
        />
      </aside>

      
      <main className="agri-main" style={{ animation: 'fadeIn 0.25s ease-out both' }}>
        <header style={{
          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
          padding: '14px 16px', backgroundColor: 'rgba(255,255,255,0.85)',
          backdropFilter: 'blur(12px)',
          borderBottom: `1px solid ${colors.stoneBorder}`,
          position: 'sticky', top: 0, zIndex: 50,
        }}>
          <div style={{
            fontSize: 16, fontWeight: 700, color: colors.forest, fontFamily: fonts.heading,
            display: 'flex', alignItems: 'center', gap: 6,
          }}>
            <span style={{
              width: 28, height: 28, borderRadius: 6, backgroundColor: colors.forest,
              display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
              fontSize: 12, color: 'white', fontWeight: 700,
            }}>A</span>
            AgriSafe
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <WalletConnect />
            <button
              onClick={toggleTheme}
              style={{
                width: 36, height: 36, borderRadius: 8, border: `1px solid ${colors.stoneBorder}`,
                backgroundColor: colors.white, color: colors.ink,
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                fontSize: 16, cursor: 'pointer', transition: 'all 0.2s ease',
              }}
              title={`Switch to ${theme === 'light' ? 'dark' : 'light'} mode`}
            >
              {theme === 'light' ? '🌙' : '☀️'}
            </button>
            <div style={{
              width: 32, height: 32, borderRadius: '50%',
              backgroundColor: colors.paleSage, color: colors.forest,
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              fontSize: 13, fontWeight: 600,
            }}>
              {userName?.charAt(0)?.toUpperCase() || '?'}
            </div>
            <span style={{ fontSize: 13, color: colors.graphite, fontWeight: 500 }}>{userName}</span>
          </div>
        </header>

        <div style={{ padding: 16, maxWidth: 800, margin: '0 auto' }} className="agri-stagger">
          {children}
        </div>
      </main>

      
      <nav className="agri-bottomnav">
        {navItems.slice(0, 5).map((item) => {
          const active = location.pathname === item.path;
          return (
            <button
              key={item.path}
              onClick={() => navigate(item.path)}
              style={{
                display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2,
                padding: '6px 14px', border: 'none', backgroundColor: 'transparent',
                color: active ? colors.forest : colors.graphite,
                cursor: 'pointer', fontFamily: fonts.body,
                transition: 'all 0.2s ease',
                borderRadius: 8,
              }}
            >
              <span style={{
                fontSize: 20, transition: 'transform 0.2s ease',
                transform: active ? 'scale(1.15)' : 'scale(1)',
              }}>{item.icon}</span>
              <span style={{
                fontSize: 10, fontWeight: active ? 700 : 400,
                transition: 'all 0.2s ease',
              }}>{item.label}</span>
              {active && (
                <span style={{
                  width: 4, height: 4, borderRadius: '50%',
                  backgroundColor: colors.forest, marginTop: 1,
                }} />
              )}
            </button>
          );
        })}
      </nav>
    </div>
  );
}
