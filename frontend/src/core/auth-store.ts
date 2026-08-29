import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { api } from './api';
import type { AuthResponse, UserRole } from '../types';

export type SupplyRole =
  | 'FARMER'
  | 'COLLECTING_AGENT'
  | 'NODAL_CENTER_AGENT'
  | 'TESTING_AGENT'
  | 'MANUFACTURER_EMPLOYEE'
  | 'DISTRIBUTOR_EMPLOYEE'
  | 'RETAILER'
  | 'GOVERNMENT_EMPLOYEE'
  | 'GOVERNMENT_INVESTIGATOR'
  | 'SUPPLIER'
  | 'SYSTEM_ADMIN';

interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  userUuid: string | null;
  userName: string | null;
  userType: SupplyRole | null;
  role: UserRole | null;
  organizationId: number | null;
  signedIn: boolean;
  walletAddress: string | null;
  walletEmail: string | null;
  pin: string | null;

  login: (email: string, pin: string) => Promise<void>;
  logout: () => void;
  setFromResponse: (res: AuthResponse, signupPin?: string) => void;
  setWalletAddress: (address: string, email: string) => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      accessToken: null,
      refreshToken: null,
      userUuid: null,
      userName: null,
      userType: null,
      role: null,
      organizationId: null,
      signedIn: false,
      walletAddress: null,
      walletEmail: null,
      pin: null,

      login: async (email: string, loginPin: string) => {
        const res = await api.post<AuthResponse>('/auth/login', {
          body: { email, pin: loginPin },
          auth: false,
        });
        set({
          accessToken: res.access_token,
          refreshToken: res.refresh_token,
          userUuid: res.user_uuid,
          userName: res.user_name,
          userType: res.user_type as SupplyRole,
          role: res.role as UserRole,
          organizationId: res.organization_id ?? null,
          signedIn: true,
          pin: loginPin,
        });
      },

      logout: () => {
        set({
          accessToken: null,
          refreshToken: null,
          userUuid: null,
          userName: null,
          userType: null,
          role: null,
          organizationId: null,
          signedIn: false,
          walletAddress: null,
          walletEmail: null,
          pin: null,
        });
      },

      setFromResponse: (res: AuthResponse, signupPin?: string) => {
        set({
          accessToken: res.access_token,
          refreshToken: res.refresh_token,
          userUuid: res.user_uuid,
          userName: res.user_name,
          userType: res.user_type as SupplyRole,
          role: res.role as UserRole,
          organizationId: res.organization_id ?? null,
          signedIn: true,
          pin: signupPin ?? null,
        });
      },

      setWalletAddress: (address: string, email: string) => {
        set({ walletAddress: address, walletEmail: email });
      },
    }),
    {
      name: 'agri_session',
      partialize: (state) => ({
        accessToken: state.accessToken,
        refreshToken: state.refreshToken,
        userUuid: state.userUuid,
        userName: state.userName,
        userType: state.userType,
        role: state.role,
        organizationId: state.organizationId,
        signedIn: state.signedIn,
        walletAddress: state.walletAddress,
        walletEmail: state.walletEmail,
      }),
    },
  ),
);

export function stageRole(userType: SupplyRole | null): string {
  switch (userType) {
    case 'FARMER': return 'farmer';
    case 'COLLECTING_AGENT': return 'agent';
    case 'NODAL_CENTER_AGENT': return 'nodal';
    case 'TESTING_AGENT': return 'testing';
    case 'MANUFACTURER_EMPLOYEE': return 'manufacturer';
    case 'DISTRIBUTOR_EMPLOYEE': return 'distributor';
    case 'RETAILER': return 'retailer';
    case 'GOVERNMENT_EMPLOYEE':
    case 'GOVERNMENT_INVESTIGATOR': return 'government';
    case 'SUPPLIER': return 'supplier';
    case 'SYSTEM_ADMIN': return 'admin';
    default: return 'unknown';
  }
}
