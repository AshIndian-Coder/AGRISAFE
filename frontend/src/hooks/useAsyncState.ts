import { useState, useCallback } from 'react';
import { ApiError } from '../core/api';

interface UseAsyncState<T> {
  data: T | null;
  loading: boolean;
  error: string | null;
  execute: (fn: () => Promise<T>) => Promise<T | null>;
  reset: () => void;
}

export function useAsyncState<T = unknown>(initialData: T | null = null): UseAsyncState<T> {
  const [data, setData] = useState<T | null>(initialData);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const execute = useCallback(async (fn: () => Promise<T>): Promise<T | null> => {
    setLoading(true);
    setError(null);
    try {
      const result = await fn();
      setData(result);
      return result;
    } catch (err: unknown) {
      const message = err instanceof ApiError
        ? err.message
        : err instanceof Error
          ? err.message
          : 'An unexpected error occurred';
      setError(message);
      return null;
    } finally {
      setLoading(false);
    }
  }, []);

  const reset = useCallback(() => {
    setData(initialData);
    setLoading(false);
    setError(null);
  }, [initialData]);

  return { data, loading, error, execute, reset };
}
