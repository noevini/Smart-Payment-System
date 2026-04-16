const TOKEN_KEY = "smartpay_token";

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY);
}

function decodeBase64Url(value) {
  const normalized = value.replace(/-/g, "+").replace(/_/g, "/");
  const padded = normalized.padEnd(
    normalized.length + ((4 - (normalized.length % 4)) % 4),
    "=",
  );
  return atob(padded);
}

export function decodeJwtPayload(token) {
  if (!token) return null;

  const [, payload] = token.split(".");
  if (!payload) return null;

  return JSON.parse(decodeBase64Url(payload));
}
