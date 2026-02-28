import api from "./apiClient";
import { getSelectedBusinessId } from "../business/businessStorage";

function getBasePath() {
  const businessId = getSelectedBusinessId();
  if (!businessId) return null;
  return `/businesses/${businessId}/customers`;
}

export async function listCustomers() {
  const base = getBasePath();
  if (!base) return [];
  const res = await api.get(base);
  return res.data;
}
