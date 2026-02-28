import api from "./apiClient";
import { getSelectedBusinessId } from "../business/businessStorage";

function getBasePath() {
  const businessId = getSelectedBusinessId();
  if (!businessId) return null;
  return `/businesses/${businessId}/payments`;
}

export async function listPayments() {
  const base = getBasePath();
  if (!base) return []; // ainda não selecionou business
  const res = await api.get(base);
  return res.data;
}

export async function getPayment(paymentId) {
  const res = await api.get(`${getBasePath()}/${paymentId}`);
  return res.data;
}

export async function createPayment(payload) {
  const res = await api.post(getBasePath(), payload);
  return res.data;
}

export async function updatePayment(paymentId, payload) {
  const res = await api.patch(`${getBasePath()}/${paymentId}`, payload);
  return res.data;
}

export async function deletePayment(paymentId) {
  await api.delete(`${getBasePath()}/${paymentId}`);
}
