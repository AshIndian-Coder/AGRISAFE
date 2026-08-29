import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { useAuthStore, stageRole } from './core/auth-store';

import { AppLayout } from './layouts/AppLayout';

import { LoginPage } from './pages/auth/LoginPage';
import { RegisterPage } from './pages/auth/RegisterPage';

import { FarmerDashboard } from './pages/farmer/FarmerDashboard';
import { FarmerLotsPage } from './pages/farmer/FarmerLotsPage';
import { FarmerCreateLotPage } from './pages/farmer/FarmerCreateLotPage';
import { FarmerLotDetailPage } from './pages/farmer/FarmerLotDetailPage';
import { FarmerComplaintsPage } from './pages/farmer/FarmerComplaintsPage';

import { AgentDashboard } from './pages/agent/AgentDashboard';
import { AgentLotDetailPage } from './pages/agent/AgentLotDetailPage';
import { AgentHistoryPage } from './pages/agent/AgentHistoryPage';

import { NodalDashboard } from './pages/nodal/NodalDashboard';
import { NodalLotDetailPage } from './pages/nodal/NodalLotDetailPage';
import { NodalPackagesPage } from './pages/nodal/NodalPackagesPage';

import { TestingDashboard } from './pages/testing/TestingDashboard';
import { TestingHistoryPage } from './pages/testing/TestingHistoryPage';

import { ManufacturerDashboard } from './pages/manufacturer/ManufacturerDashboard';
import { ManufacturerLotsPage } from './pages/manufacturer/ManufacturerLotsPage';
import { ManufacturerCreateLotPage } from './pages/manufacturer/ManufacturerCreateLotPage';
import { ManufacturerLotDetailPage } from './pages/manufacturer/ManufacturerLotDetailPage';

import { DistributorDashboard } from './pages/distributor/DistributorDashboard';
import { DistributorBundlesPage } from './pages/distributor/DistributorBundlesPage';

import { RetailerDashboard } from './pages/retailer/RetailerDashboard';
import { RetailerInventoryPage } from './pages/retailer/RetailerInventoryPage';
import { RetailerComplaintsPage } from './pages/retailer/RetailerComplaintsPage';

import { GovernmentDashboard } from './pages/government/GovernmentDashboard';
import { GovernmentFlagsPage } from './pages/government/GovernmentFlagsPage';
import { GovernmentComplaintsPage } from './pages/government/GovernmentComplaintsPage';

import { SupplierDashboard } from './pages/supplier/SupplierDashboard';
import { ScanQrPage } from './pages/scan/ScanQrPage';

import { ConsumerVerifyPage } from './pages/consumer/ConsumerVerifyPage';

function RequireAuth({ children, allowedRoles }: { children: React.ReactNode; allowedRoles?: string[] }) {
  const { signedIn, userType } = useAuthStore();
  const location = useLocation();
  const role = stageRole(userType);

  if (!signedIn) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (allowedRoles && !allowedRoles.includes(role)) {
    return <Navigate to="/" replace />;
  }

  return <AppLayout>{children}</AppLayout>;
}

function HomeRedirect() {
  const { signedIn, userType } = useAuthStore();
  const role = stageRole(userType);

  if (!signedIn) return <Navigate to="/login" replace />;

  const ROLE_HOME: Record<string, string> = {
    farmer: '/farmer',
    agent: '/agent',
    nodal: '/nodal',
    testing: '/testing',
    manufacturer: '/manufacturer',
    distributor: '/distributor',
    retailer: '/retailer',
    government: '/government',
    supplier: '/supplier',
  };

  return <Navigate to={ROLE_HOME[role] || '/login'} replace />;
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/verify/:qrToken" element={<ConsumerVerifyPage />} />
        <Route path="/verify" element={<ConsumerVerifyPage />} />

        <Route path="/" element={<HomeRedirect />} />

        <Route path="/farmer" element={<RequireAuth><FarmerDashboard /></RequireAuth>} />
        <Route path="/farmer/lots" element={<RequireAuth><FarmerLotsPage /></RequireAuth>} />
        <Route path="/farmer/lots/new" element={<RequireAuth><FarmerCreateLotPage /></RequireAuth>} />
        <Route path="/farmer/lots/:lotId" element={<RequireAuth><FarmerLotDetailPage /></RequireAuth>} />
        <Route path="/farmer/complaints" element={<RequireAuth><FarmerComplaintsPage /></RequireAuth>} />

        <Route path="/agent" element={<RequireAuth><AgentDashboard /></RequireAuth>} />
        <Route path="/agent/lots" element={<RequireAuth><AgentDashboard /></RequireAuth>} />
        <Route path="/agent/lots/:lotId" element={<RequireAuth><AgentLotDetailPage /></RequireAuth>} />
        <Route path="/agent/history" element={<RequireAuth><AgentHistoryPage /></RequireAuth>} />

        <Route path="/nodal" element={<RequireAuth><NodalDashboard /></RequireAuth>} />
        <Route path="/nodal/packages" element={<RequireAuth><NodalPackagesPage /></RequireAuth>} />
        <Route path="/nodal/lots/:lotId" element={<RequireAuth><NodalLotDetailPage /></RequireAuth>} />

        <Route path="/testing" element={<RequireAuth><TestingDashboard /></RequireAuth>} />
        <Route path="/testing/submit" element={<RequireAuth><TestingDashboard /></RequireAuth>} />
        <Route path="/testing/history" element={<RequireAuth><TestingHistoryPage /></RequireAuth>} />

        <Route path="/manufacturer" element={<RequireAuth><ManufacturerDashboard /></RequireAuth>} />
        <Route path="/manufacturer/lots" element={<RequireAuth><ManufacturerLotsPage /></RequireAuth>} />
        <Route path="/manufacturer/lots/new" element={<RequireAuth><ManufacturerCreateLotPage /></RequireAuth>} />
        <Route path="/manufacturer/lots/:lotId" element={<RequireAuth><ManufacturerLotDetailPage /></RequireAuth>} />

        <Route path="/distributor" element={<RequireAuth><DistributorDashboard /></RequireAuth>} />
        <Route path="/distributor/bundles" element={<RequireAuth><DistributorBundlesPage /></RequireAuth>} />

        <Route path="/retailer" element={<RequireAuth><RetailerDashboard /></RequireAuth>} />
        <Route path="/retailer/inventory" element={<RequireAuth><RetailerInventoryPage /></RequireAuth>} />
        <Route path="/retailer/complaints" element={<RequireAuth><RetailerComplaintsPage /></RequireAuth>} />

        <Route path="/government" element={<RequireAuth><GovernmentDashboard /></RequireAuth>} />
        <Route path="/government/flags" element={<RequireAuth><GovernmentFlagsPage /></RequireAuth>} />
        <Route path="/government/complaints" element={<RequireAuth><GovernmentComplaintsPage /></RequireAuth>} />

        <Route path="/supplier" element={<RequireAuth><SupplierDashboard /></RequireAuth>} />
        <Route path="/supplier/assignments" element={<RequireAuth><SupplierDashboard /></RequireAuth>} />

        <Route path="/scan" element={<RequireAuth allowedRoles={['agent', 'supplier']}><ScanQrPage /></RequireAuth>} />

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
