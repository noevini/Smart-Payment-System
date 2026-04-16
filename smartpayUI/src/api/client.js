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

  // Prevent 304 responses — axios treats them as errors (outside 2xx range)
  if (config.method === "get" || !config.method) {
    config.headers["Cache-Control"] = "no-cache";
    config.headers["Pragma"] = "no-cache";
  }

  return config;
});

export default api;
