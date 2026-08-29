import { useEffect, useState } from 'react';
import { useAuthStore } from '../../core/auth-store';
import { connectInAppWallet, autoConnectWallet } from '../../core/blockchain';
import { colors } from '../../core/theme';

export function WalletConnect() {
  const { walletAddress, setWalletAddress, signedIn } = useAuthStore();
  const [connecting, setConnecting] = useState(false);

  useEffect(() => {
    if (walletAddress || !signedIn) return;
    autoConnectWallet()
      .then((account) => {
        if (account?.address) {
          setWalletAddress(account.address, '');
        }
      })
      .catch(() => {});
  }, [walletAddress, signedIn, setWalletAddress]);

  const handleConnect = async () => {
    setConnecting(true);
    try {
      const email = useAuthStore.getState().walletEmail || '';
      const account = await connectInAppWallet(email);
      if (account?.address) {
        setWalletAddress(account.address, email);
      }
    } catch {
      console.warn('Wallet connection failed');
    } finally {
      setConnecting(false);
    }
  };

  const handleDisconnect = () => {
    setWalletAddress('', '');
  };

  if (!signedIn) return null;

  return (
    <div style={{ position: 'relative' }}>
      {walletAddress ? (
        <button
          onClick={handleDisconnect}
          className="agri-btn agri-btn-secondary"
          style={{
            height: 36, fontSize: 13, fontWeight: 600,
            borderRadius: 8, padding: '0 14px',
            backgroundColor: colors.success, color: colors.white,
            border: 'none', cursor: 'pointer',
          }}
          title="Click to disconnect wallet"
        >
          🔗 {walletAddress.slice(0, 6)}...{walletAddress.slice(-4)}
        </button>
      ) : (
        <button
          onClick={handleConnect}
          disabled={connecting}
          className="agri-btn agri-btn-primary"
          style={{
            height: 36, fontSize: 13, fontWeight: 600,
            borderRadius: 8, padding: '0 14px',
            backgroundColor: colors.forest, color: colors.white,
            border: 'none', cursor: connecting ? 'not-allowed' : 'pointer',
            opacity: connecting ? 0.7 : 1,
          }}
        >
          {connecting ? 'Connecting…' : 'Connect Wallet'}
        </button>
      )}
    </div>
  );
}

export function WalletBadge() {
  const { walletAddress } = useAuthStore();

  if (!walletAddress) {
    return (
      <div style={{
        fontSize: 10, color: colors.sage, marginTop: 4, paddingLeft: 40,
        opacity: 0.6,
      }}>
        No wallet connected
      </div>
    );
  }

  return (
    <div style={{
      fontSize: 10, color: colors.sage, marginTop: 4, paddingLeft: 40,
      fontFamily: 'monospace', opacity: 0.7,
      overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap',
      display: 'flex', alignItems: 'center', gap: 4,
    }}>
      <span style={{ color: colors.success }}>●</span>
      {walletAddress.slice(0, 6)}...{walletAddress.slice(-4)}
    </div>
  );
}
