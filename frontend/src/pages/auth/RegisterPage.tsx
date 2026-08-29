import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { api, ApiError } from '../../core/api';
import { useAuthStore } from '../../core/auth-store';
import { sendEmailOtp, verifyEmailOtp } from '../../core/blockchain';
import { Button } from '../../components/common/UI';
import { colors, fonts } from '../../core/theme';
import type { AuthResponse } from '../../types';

type Step = 'email' | 'otp' | 'details';

const USER_TYPES: { value: string; label: string }[] = [
  { value: 'FARMER', label: 'Farmer' },
  { value: 'COLLECTING_AGENT', label: 'Collecting Agent' },
  { value: 'NODAL_CENTER_AGENT', label: 'Nodal Center Agent' },
  { value: 'TESTING_AGENT', label: 'Testing Agent' },
  { value: 'MANUFACTURER_EMPLOYEE', label: 'Manufacturer Employee' },
  { value: 'DISTRIBUTOR_EMPLOYEE', label: 'Distributor Employee' },
  { value: 'RETAILER', label: 'Retailer' },
  { value: 'SUPPLIER', label: 'Supplier' },
  { value: 'GOVERNMENT_EMPLOYEE', label: 'Government Employee' },
  { value: 'GOVERNMENT_INVESTIGATOR', label: 'Government Investigator' },
];

export function RegisterPage() {
  const navigate = useNavigate();
  const setFromResponse = useAuthStore((s) => s.setFromResponse);

  const [step, setStep] = useState<Step>('email');
  const [email, setEmail] = useState('');
  const [otp, setOtp] = useState('');
  const [name, setName] = useState('');
  const [userType, setUserType] = useState('');
  const [pin, setPin] = useState('');
  const [pinConfirm, setPinConfirm] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [status, setStatus] = useState('');
  const [walletAddress, setWalletAddress] = useState('');

  const handleSendOtp = async () => {
    if (!email || !email.includes('@')) return;
    setLoading(true);
    setError(null);
    setStatus('Sending verification code via Thirdweb...');

    try {
      await sendEmailOtp(email);
      setStatus('Verification code sent! Check your email.');
      setStep('otp');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to send verification code.');
      setStatus('');
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyOtp = async () => {
    if (!otp || otp.length !== 6) return;
    setLoading(true);
    setError(null);

    try {
      const account = await verifyEmailOtp(email, otp);
      setWalletAddress(account.address);
      setStatus('Email verified! Complete your profile.');
      setStep('details');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Invalid verification code.');
    } finally {
      setLoading(false);
    }
  };

  const handleSignup = async () => {
    if (!name.trim() || !userType || pin.length !== 6 || pin !== pinConfirm) return;
    setLoading(true);
    setError(null);
    try {
      const res = await api.post<AuthResponse>('/auth/signup', {
        body: { email, name: name.trim(), userType, pin, walletAddress },
        auth: false,
      });
      setFromResponse(res);
      useAuthStore.getState().setWalletAddress(walletAddress, email);
      redirectToRole(res.user_type);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Registration failed.');
    } finally {
      setLoading(false);
    }
  };

  const redirectToRole = (role: string) => {
    switch (role) {
      case 'FARMER': navigate('/farmer'); break;
      case 'COLLECTING_AGENT': navigate('/agent'); break;
      case 'NODAL_CENTER_AGENT': navigate('/nodal'); break;
      case 'TESTING_AGENT': navigate('/testing'); break;
      case 'MANUFACTURER_EMPLOYEE': navigate('/manufacturer'); break;
      case 'DISTRIBUTOR_EMPLOYEE': navigate('/distributor'); break;
      case 'RETAILER': navigate('/retailer'); break;
      case 'GOVERNMENT_EMPLOYEE':
      case 'GOVERNMENT_INVESTIGATOR': navigate('/government'); break;
      case 'SUPPLIER': navigate('/supplier'); break;
      default: navigate('/');
    }
  };

  const detailsValid = name.trim().length > 0 && userType !== '' && pin.length === 6 && pin === pinConfirm;

  return (
    <div style={{
      minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center',
      backgroundColor: colors.softPaper, padding: 16,
      animation: 'fadeIn 0.3s ease-out both',
    }}>
      <div style={{ width: '100%', maxWidth: 400 }}>
        <div style={{
          textAlign: 'center', marginBottom: 24,
          animation: 'fadeInUp 0.4s ease-out both',
        }}>
          <div style={{
            width: 56, height: 56, borderRadius: 14, backgroundColor: colors.forest,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            margin: '0 auto 12px', fontSize: 24, color: 'white', fontWeight: 700,
            boxShadow: '0 4px 20px rgba(40, 72, 60, 0.25)',
          }}>
            A
          </div>
        </div>

        <div style={{
          backgroundColor: colors.white, borderRadius: 14, padding: '40px 32px',
          border: `1px solid ${colors.stoneBorder}`,
          boxShadow: '0 4px 24px rgba(0, 0, 0, 0.04)',
          animation: 'fadeInUp 0.4s ease-out 0.1s both',
        }}>
          <div style={{ textAlign: 'center', marginBottom: 28 }}>
            <h1 style={{ fontSize: 24, fontWeight: 700, color: colors.forest, fontFamily: fonts.heading, margin: 0 }}>
              AgriSafe
            </h1>
            <p style={{ fontSize: 13, color: colors.graphite, marginTop: 4 }}>Create your account</p>
          </div>

          <div style={{ display: 'flex', gap: 8, marginBottom: 28, justifyContent: 'center' }}>
            {(['email', 'otp', 'details'] as Step[]).map((s) => (
              <div key={s} style={{
                width: step === s ? 24 : 8, height: 8, borderRadius: 4,
                backgroundColor: (s === step || (s === 'otp' && step === 'details') || (s === 'email' && step !== 'email'))
                  ? colors.forest : colors.stoneBorder,
                transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
              }} />
            ))}
          </div>

          {step === 'email' && (
            <div style={{ animation: 'fadeInUp 0.3s ease-out both' }}>
              <label style={labelStyle}>Email</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="you@example.com"
                className="agri-input"
                style={inputStyle}
                autoFocus
                onKeyDown={(e) => e.key === 'Enter' && handleSendOtp()}
              />
              <p style={{ fontSize: 12, color: colors.graphite, marginTop: 8 }}>
                Thirdweb will send a verification code to this email
              </p>
              <Button onClick={handleSendOtp} disabled={!email || !email.includes('@')} fullWidth loading={loading} style={{ marginTop: 16 }}>
                Send Verification Code
              </Button>
            </div>
          )}

          {step === 'otp' && (
            <div style={{ animation: 'fadeInUp 0.3s ease-out both' }}>
              <p style={{ fontSize: 13, color: colors.graphite, marginBottom: 16 }}>
                Enter the 6-digit code sent to <strong>{email}</strong>
              </p>
              <label style={labelStyle}>Verification Code</label>
              <input
                type="text"
                value={otp}
                onChange={(e) => setOtp(e.target.value.replace(/\D/g, '').slice(0, 6))}
                placeholder="000000"
                className="agri-input"
                style={{ ...inputStyle, textAlign: 'center', fontSize: 24, letterSpacing: 8, fontFamily: fonts.mono }}
                autoFocus
                maxLength={6}
                onKeyDown={(e) => e.key === 'Enter' && handleVerifyOtp()}
              />
              <Button onClick={handleVerifyOtp} disabled={otp.length !== 6} fullWidth loading={loading} style={{ marginTop: 16 }}>
                Verify Code
              </Button>
              <button
                onClick={() => { setStep('email'); setOtp(''); setError(null); setStatus(''); }}
                style={{
                  fontSize: 12, color: colors.graphite, background: 'none', border: 'none',
                  cursor: 'pointer', marginTop: 12, display: 'block', margin: '12px auto 0',
                }}
                onMouseEnter={(e) => { e.currentTarget.style.color = colors.forest; }}
                onMouseLeave={(e) => { e.currentTarget.style.color = colors.graphite; }}
              >
                Change email
              </button>
            </div>
          )}

          {step === 'details' && (
            <div style={{ animation: 'fadeInUp 0.3s ease-out both' }}>
              <label style={labelStyle}>Full Name</label>
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Your full name"
                className="agri-input"
                style={inputStyle}
                autoFocus
              />

              <label style={{ ...labelStyle, marginTop: 16 }}>Account Type</label>
              <select
                value={userType}
                onChange={(e) => setUserType(e.target.value)}
                className="agri-input"
                style={{ ...inputStyle, backgroundColor: colors.white }}
              >
                <option value="">Select your role...</option>
                {USER_TYPES.map((t) => (
                  <option key={t.value} value={t.value}>{t.label}</option>
                ))}
              </select>

              <label style={{ ...labelStyle, marginTop: 16 }}>Create 6-digit PIN</label>
              <input
                type="password"
                value={pin}
                onChange={(e) => setPin(e.target.value.replace(/\D/g, '').slice(0, 6))}
                placeholder="••••••"
                className="agri-input"
                style={{ ...inputStyle, textAlign: 'center', fontSize: 24, letterSpacing: 8 }}
                maxLength={6}
              />

              <label style={{ ...labelStyle, marginTop: 16 }}>Confirm PIN</label>
              <input
                type="password"
                value={pinConfirm}
                onChange={(e) => setPinConfirm(e.target.value.replace(/\D/g, '').slice(0, 6))}
                placeholder="••••••"
                className="agri-input"
                style={{ ...inputStyle, textAlign: 'center', fontSize: 24, letterSpacing: 8 }}
                maxLength={6}
                onKeyDown={(e) => e.key === 'Enter' && detailsValid && handleSignup()}
              />

              {pin.length === 6 && pinConfirm.length === 6 && pin !== pinConfirm && (
                <p style={{ fontSize: 12, color: '#DC2626', marginTop: 8, animation: 'fadeIn 0.15s ease-out both' }}>
                  PINs do not match
                </p>
              )}

              {walletAddress && (
                <div style={{
                  marginTop: 12, padding: '8px 12px', backgroundColor: '#F0FDF4', borderRadius: 8,
                  fontSize: 11, color: '#166534', border: '1px solid #BBF7D0', fontFamily: fonts.mono,
                  wordBreak: 'break-all',
                }}>
                  Wallet: {walletAddress.slice(0, 6)}...{walletAddress.slice(-4)}
                </div>
              )}

              <Button onClick={handleSignup} disabled={!detailsValid} fullWidth loading={loading} style={{ marginTop: 16 }}>
                Create Account
              </Button>
            </div>
          )}

          {error && (
            <div style={{
              marginTop: 16, padding: '10px 14px', backgroundColor: '#FEE2E2', borderRadius: 8,
              fontSize: 13, color: '#DC2626', border: '1px solid #F0B8B8',
              animation: 'fadeInUp 0.2s ease-out both',
            }}>
              {error}
            </div>
          )}

          {status && !error && (
            <div style={{
              marginTop: 16, padding: '10px 14px', backgroundColor: '#ECFDF5', borderRadius: 8,
              fontSize: 13, color: '#059669', border: '1px solid #A7F3D0',
              animation: 'fadeInUp 0.2s ease-out both',
            }}>
              {status}
            </div>
          )}

          <p style={{ textAlign: 'center', marginTop: 28, fontSize: 13, color: colors.graphite }}>
            Already have an account?{' '}
            <Link to="/login" style={{
              color: colors.forest, fontWeight: 600,
              transition: 'opacity 0.15s',
            }}
            onMouseEnter={(e) => { e.currentTarget.style.opacity = '0.7'; }}
            onMouseLeave={(e) => { e.currentTarget.style.opacity = '1'; }}
            >
              Sign in
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}

const labelStyle: React.CSSProperties = {
  display: 'block', fontSize: 13, fontWeight: 600, color: colors.ink, marginBottom: 6,
};

const inputStyle: React.CSSProperties = {
  width: '100%', padding: '12px 14px', borderRadius: 8,
  border: `1px solid ${colors.stoneBorder}`, fontSize: 15,
  fontFamily: fonts.body, outline: 'none', boxSizing: 'border-box',
  backgroundColor: colors.white,
  transition: 'border-color 0.2s ease, box-shadow 0.2s ease',
};
