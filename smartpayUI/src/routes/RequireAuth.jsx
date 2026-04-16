import { Navigate } from "react-router-dom";
import { getToken, clearToken, decodeJwtPayload } from "../auth/tokenStorage";
import { clearSelectedBusinessId } from "../state/businessStorage";

/**
 * Decodes a JWT token and checks if it has expired.
 * Does NOT verify the signature — that is the backend's responsibility.
 * This is only used to avoid sending requests with clearly expired tokens.
 */
function isTokenExpired(token) {
  try {
    const payload = decodeJwtPayload(token);
    // If we can't read "exp", don't block navigation here.
    // Backend will still reject invalid/expired tokens on requests.
    if (!payload?.exp) return false;

    // "exp" is the expiration timestamp in seconds
    const nowInSeconds = Date.now() / 1000;
    return payload.exp < nowInSeconds;
  } catch {
    // If we can't decode the token, don't block navigation here.
    return false;
  }
}

/**
 * Protects routes that require authentication.
 * Redirects to /login if:
 * - No token exists in storage
 * - Token exists but has expired
 */
export default function RequireAuth({ children }) {
  const token = getToken();

  // No token — redirect to login
  if (!token) {
    return <Navigate to="/login" replace />;
  }

  // Token exists but is expired — clear storage and redirect
  if (isTokenExpired(token)) {
    clearToken();
    clearSelectedBusinessId();
    return <Navigate to="/login" replace />;
  }

  return children;
}
