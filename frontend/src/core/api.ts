import type { ApiResponse } from '../types';

const BASE_URL = '/api/v1';

class ApiError extends Error {
  httpStatus: number;
  code: string;
  traceId: string | undefined;
  constructor(
    httpStatus: number,
    code: string,
    message: string,
    traceId?: string,
  ) {
    super(message);
    this.httpStatus = httpStatus;
    this.code = code;
    this.traceId = traceId;
    this.name = 'ApiError';
  }
}

function readSession(): Record<string, unknown> {
  try {
    const raw = localStorage.getItem('agri_session');
    if (!raw) return {};
    const parsed = JSON.parse(raw);
    return parsed.state && typeof parsed.state === 'object' ? parsed.state : parsed;
  } catch {
    return {};
  }
}

function writeSession(updates: Record<string, unknown>) {
  try {
    const raw = localStorage.getItem('agri_session');
    const parsed = raw ? JSON.parse(raw) : {};
    const state = parsed.state && typeof parsed.state === 'object' ? parsed.state : parsed;
    Object.assign(state, updates);
    parsed.state = state;
    localStorage.setItem('agri_session', JSON.stringify(parsed));
  } catch {  }
}

function getAccessToken(): string | null {
  const session = readSession();
  return (session.accessToken as string) ?? null;
}

function getRefreshToken(): string | null {
  const session = readSession();
  return (session.refreshToken as string) ?? null;
}

function setSession(data: { accessToken: string; refreshToken: string }) {
  writeSession({ accessToken: data.accessToken, refreshToken: data.refreshToken });
}

async function tryRefresh(): Promise<boolean> {
  const rt = getRefreshToken();
  if (!rt) return false;
  try {
    const res = await fetch(`${BASE_URL}/auth/refresh?refreshToken=${encodeURIComponent(rt)}`, {
      method: 'POST',
    });
    if (!res.ok) return false;
    const envelope: ApiResponse<unknown> = await res.json();
    if (envelope.success && envelope.data) {
      const d = envelope.data as Record<string, string>;
      setSession({
        accessToken: d.access_token,
        refreshToken: d.refresh_token,
      });
      return true;
    }
  } catch {  }
  return false;
}

async function request<T = unknown>(
  method: string,
  path: string,
  options: {
    body?: unknown;
    query?: Record<string, string>;
    auth?: boolean;
    raw?: boolean;
  } = {},
): Promise<T> {
  const { body, query, auth = true, raw = false } = options;

  let url = `${BASE_URL}${path}`;
  if (query && Object.keys(query).length > 0) {
    const params = new URLSearchParams(query);
    url += `?${params.toString()}`;
  }

  const headers: Record<string, string> = {
    Accept: 'application/json',
  };

  if (auth) {
    const token = getAccessToken();
    if (token) headers['Authorization'] = `Bearer ${token}`;
  }

  if (body !== undefined) {
    headers['Content-Type'] = 'application/json';
  }

  const res = await fetch(url, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  if (res.status === 401 && auth) {
    const refreshed = await tryRefresh();
    if (refreshed) {
      return request<T>(method, path, { body, query, auth, raw });
    }
  }

  const text = await res.text();
  let parsed: unknown;
  try {
    parsed = text ? JSON.parse(text) : null;
  } catch {
    parsed = text;
  }

  if (raw) return parsed as T;

  if (parsed && typeof parsed === 'object' && 'success' in parsed) {
    const envelope = parsed as ApiResponse<unknown>;
    if (envelope.success) return envelope.data as T;
    throw new ApiError(
      envelope.status || res.status,
      envelope.code || 'ERROR',
      envelope.message || 'Request failed',
      envelope.traceId,
    );
  }

  if (res.ok) return parsed as T;

  throw new ApiError(
    res.status,
    res.status === 401
      ? 'UNAUTHORIZED'
      : res.status === 403
        ? 'FORBIDDEN'
        : `HTTP_${res.status}`,
    res.status === 401
      ? 'Session expired. Sign in again.'
      : res.status === 403
        ? 'Your role is not permitted for this operation.'
        : `Backend returned HTTP ${res.status}.`,
  );
}

export const api = {
  get: <T = unknown>(path: string, opts?: { query?: Record<string, string>; auth?: boolean }) =>
    request<T>('GET', path, opts),

  post: <T = unknown>(path: string, opts?: { body?: unknown; query?: Record<string, string>; auth?: boolean }) =>
    request<T>('POST', path, opts),

  put: <T = unknown>(path: string, opts?: { body?: unknown; query?: Record<string, string>; auth?: boolean }) =>
    request<T>('PUT', path, opts),

  delete: <T = unknown>(path: string, opts?: { auth?: boolean }) =>
    request<T>('DELETE', path, opts),
};

export { ApiError };
