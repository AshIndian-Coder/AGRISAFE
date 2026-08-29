import { useEffect, useRef, useCallback } from 'react';

interface QrScannerProps {
  onScan: (decodedText: string) => void;
  onError?: (error: string) => void;
  active: boolean;
  onClose?: () => void;
}

export function QrScanner({ onScan, onError, active, onClose }: QrScannerProps) {
  const scannerRef = useRef<{ stop: () => Promise<void>; clear: () => void } | null>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const scanningRef = useRef(false);

  const stopScanner = useCallback(async () => {
    if (scannerRef.current) {
      try {
        await scannerRef.current.stop();
        scannerRef.current.clear();
      } catch {  }
      scannerRef.current = null;
      scanningRef.current = false;
    }
  }, []);

  useEffect(() => {
    if (!active) {
      stopScanner();
      return;
    }

    let cancelled = false;

    const startScanner = async () => {
      try {
        const { Html5Qrcode } = await import('html5-qrcode');
        if (cancelled || !containerRef.current) return;

        const scanner = new Html5Qrcode('qr-reader');
        scannerRef.current = scanner;

        let started = false;
        const configs = [
          { facingMode: 'environment' },
          { facingMode: 'user' },
        ];

        for (const cam of configs) {
          if (started || cancelled) break;
          try {
            await scanner.start(
              cam,
              {
                fps: 10,
                qrbox: { width: 250, height: 250 },
                aspectRatio: 1.0,
              },
              (decodedText: string) => {
                if (!scanningRef.current) {
                  scanningRef.current = true;
                  onScan(decodedText);
                }
              },
              () => {  },
            );
            started = true;
          } catch {
          }
        }

        if (!started && !cancelled) {
          throw new Error('No camera found. Connect a webcam or grant camera permission.');
        }
      } catch (err: unknown) {
        if (!cancelled) {
          const msg = err instanceof Error ? err.message : String(err);
          onError?.(msg || 'Camera access failed. Check permissions.');
        }
      }
    };

    startScanner();

    return () => {
      cancelled = true;
      stopScanner();
    };
  }, [active, onScan, onError, stopScanner]);

  if (!active) return null;

  return (
    <div style={{ position: 'relative', borderRadius: 12, overflow: 'hidden', backgroundColor: '#000' }}>
      <div
        id="qr-reader"
        ref={containerRef}
        style={{ width: '100%', minHeight: 280 }}
      />
      
      <div style={{
        position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', justifyContent: 'center',
        pointerEvents: 'none',
      }}>
        <div style={{
          width: 220, height: 220, border: `2px solid rgba(255,255,255,0.6)`, borderRadius: 12,
        }} />
      </div>
      {onClose && (
        <button
          onClick={async () => { await stopScanner(); onClose(); }}
          style={{
            position: 'absolute', top: 8, right: 8, width: 32, height: 32,
            borderRadius: '50%', border: 'none', backgroundColor: 'rgba(0,0,0,0.5)',
            color: '#fff', fontSize: 16, cursor: 'pointer', display: 'flex',
            alignItems: 'center', justifyContent: 'center', pointerEvents: 'all',
          }}
        >
          ×
        </button>
      )}
    </div>
  );
}
