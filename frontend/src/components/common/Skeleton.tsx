import { colors } from '../../core/theme';

const shimmerStyle: React.CSSProperties = {
  background: `linear-gradient(90deg, ${colors.stoneBorder}22 25%, ${colors.stoneBorder}44 50%, ${colors.stoneBorder}22 75%)`,
  backgroundSize: '200% 100%',
  animation: 'shimmer 1.5s ease-in-out infinite',
  borderRadius: 6,
};

export function SkeletonText({ width = '100%', height = 14 }: { width?: string | number; height?: number }) {
  return <div style={{ ...shimmerStyle, width, height }} />;
}

export function SkeletonCircle({ size = 32 }: { size?: number }) {
  return <div style={{ ...shimmerStyle, width: size, height: size, borderRadius: '50%' }} />;
}

export function SkeletonCard() {
  return (
    <div style={{
      padding: '14px 16px', marginBottom: 6,
      backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`, borderRadius: 8,
    }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
        <div style={{ flex: 1 }}>
          <SkeletonText width="60%" height={16} />
          <div style={{ marginTop: 8 }}>
            <SkeletonText width="40%" height={12} />
          </div>
        </div>
        <SkeletonText width={60} height={22} />
      </div>
    </div>
  );
}

export function SkeletonStatCard() {
  return (
    <div style={{
      padding: '16px', backgroundColor: colors.white,
      border: `1px solid ${colors.stoneBorder}`, borderRadius: 8,
    }}>
      <SkeletonText width={48} height={28} />
      <div style={{ marginTop: 8 }}>
        <SkeletonText width={64} height={12} />
      </div>
    </div>
  );
}

export function SkeletonList({ count = 3 }: { count?: number }) {
  return (
    <div>
      {Array.from({ length: count }).map((_, i) => (
        <SkeletonCard key={i} />
      ))}
    </div>
  );
}

export function SkeletonPageHeader() {
  return (
    <div style={{ marginBottom: 20 }}>
      <SkeletonText width="200px" height={22} />
      <div style={{ marginTop: 6 }}>
        <SkeletonText width="300px" height={14} />
      </div>
    </div>
  );
}

export function SkeletonForm() {
  return (
    <div style={{
      backgroundColor: colors.white, border: `1px solid ${colors.stoneBorder}`,
      borderRadius: 8, padding: 20,
    }}>
      <div style={{ marginBottom: 14 }}>
        <SkeletonText width={80} height={12} />
        <div style={{ marginTop: 6 }}>
          <SkeletonText width="100%" height={40} />
        </div>
      </div>
      <div style={{ marginBottom: 14 }}>
        <SkeletonText width={100} height={12} />
        <div style={{ marginTop: 6 }}>
          <SkeletonText width="100%" height={40} />
        </div>
      </div>
      <SkeletonText width="100%" height={40} />
    </div>
  );
}
