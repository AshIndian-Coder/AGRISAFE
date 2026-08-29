import { useState, useEffect, useCallback, useRef } from 'react';

export type GpsStatus = 'idle' | 'requesting' | 'ready' | 'unavailable' | 'denied' | 'error';

interface GpsState {
  status: GpsStatus;
  latitude: number | null;
  longitude: number | null;
  accuracy: number | null;
  error: string | null;
  request: () => void;
}

export function useGps(): GpsState {
  const [status, setStatus] = useState<GpsStatus>('idle');
  const [latitude, setLatitude] = useState<number | null>(null);
  const [longitude, setLongitude] = useState<number | null>(null);
  const [accuracy, setAccuracy] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);
  const watchIdRef = useRef<number | null>(null);
  const mountedRef = useRef(true);

  const handleSuccess = useCallback((pos: GeolocationPosition) => {
    if (!mountedRef.current) return;
    setLatitude(pos.coords.latitude);
    setLongitude(pos.coords.longitude);
    setAccuracy(pos.coords.accuracy);
    setStatus('ready');
    setError(null);
  }, []);

  const handleError = useCallback((err: GeolocationPositionError) => {
    if (!mountedRef.current) return;
    switch (err.code) {
      case err.PERMISSION_DENIED:
        setStatus('denied');
        setError('Location permission denied. Enable it in browser settings.');
        break;
      case err.POSITION_UNAVAILABLE:
        setStatus('unavailable');
        setError('Location unavailable. Check your connection.');
        break;
      case err.TIMEOUT:
        setStatus('error');
        setError('Location request timed out. Try again.');
        break;
      default:
        setStatus('error');
        setError('Could not get location.');
    }
  }, []);

  const request = useCallback(() => {
    if (!navigator.geolocation) {
      setStatus('unavailable');
      setError('Geolocation is not supported by this browser.');
      return;
    }

    setStatus('requesting');
    setError(null);

    if (watchIdRef.current !== null) {
      navigator.geolocation.clearWatch(watchIdRef.current);
    }

    navigator.geolocation.getCurrentPosition(handleSuccess, handleError, {
      enableHighAccuracy: false,
      timeout: 15000,
      maximumAge: 30000,
    });

    watchIdRef.current = navigator.geolocation.watchPosition(handleSuccess, handleError, {
      enableHighAccuracy: false,
      timeout: 30000,
      maximumAge: 10000,
    });
  }, [handleSuccess, handleError]);

  useEffect(() => {
    mountedRef.current = true;
    const timer = setTimeout(() => {
      if (mountedRef.current) {
        request();
      }
    }, 500);

    return () => {
      mountedRef.current = false;
      clearTimeout(timer);
      if (watchIdRef.current !== null) {
        navigator.geolocation.clearWatch(watchIdRef.current);
      }
    };
  }, []); 

  return { status, latitude, longitude, accuracy, error, request };
}
