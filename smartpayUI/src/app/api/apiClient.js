import axios from "axios";
import { getToken } from "../auth/tokenStorage";
import { getSelectedBusinessId } from "../business/businessStorage";

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080",
});

api.interceptors.request.use((config) => {
  const token = getToken();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  const businessId = getSelectedBusinessId();
  if (businessId) {
    config.headers["X-Business-Id"] = String(businessId);
  }

  return config;
});

export default api;
