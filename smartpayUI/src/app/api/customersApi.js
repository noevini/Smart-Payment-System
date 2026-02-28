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

export async function createCustomer(payload) {
  const res = await api.post(getBasePath(), payload);
  return res.data;
}

export async function updateCustomer(customerId, payload) {
  const res = await api.patch(`${getBasePath()}/${customerId}`, payload);
  return res.data;
}

export async function deleteCustomer(customerId) {
  await api.delete(`${getBasePath()}/${customerId}`);
}
