import axios from "axios";
import { getToken } from "../auth/tokenStorage";
import { getSelectedBusinessId } from "../state/businessStorage";

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080",
});

api.interceptors.request.use((config) => {
  config.headers = config.headers ?? {};

  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  const businessId = getSelectedBusinessId();
  const isAuthRoute =
    config.url?.includes("/auth/login") ||
    config.url?.includes("/auth/register");

  if (businessId && !isAuthRoute) {
    config.headers["X-Business-Id"] = String(businessId);
  }

  // Avoid conditional browser cache on GET requests.
  // The login flow depends on fresh `/businesses` data to decide where to navigate.
  if (config.method === "get" || !config.method) {
    config.headers["Cache-Control"] = "no-cache, no-store, must-revalidate";
    config.headers.Pragma = "no-cache";
    config.headers.Expires = "0";
    config.params = { ...config.params, _t: Date.now() };
  }

  return config;
});

export default api;
