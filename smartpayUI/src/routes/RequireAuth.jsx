import { Navigate } from "react-router-dom";
import { getToken, clearToken } from "../auth/tokenStorage";
import { clearSelectedBusinessId } from "../state/businessStorage";

/**
 * Decodes a JWT token and checks if it has expired.
 * Does NOT verify the signature — that is the backend's responsibility.
 * This is only used to avoid sending requests with clearly expired tokens.
 */
function isTokenExpired(token) {
  try {
    // JWT structure: header.payload.signature
    // Payload is base64-encoded JSON
    const payload = JSON.parse(atob(token.split(".")[1]));

    // "exp" is the expiration timestamp in seconds
    const nowInSeconds = Date.now() / 1000;
    return payload.exp < nowInSeconds;
  } catch {
    // If we can't decode the token, treat it as expired
    return true;
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
