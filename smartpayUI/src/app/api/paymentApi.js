import api from "./apiClient";
import { getSelectedBusinessId } from "../business/businessStorage";

function getBasePath() {
  const businessId = getSelectedBusinessId();
  if (!businessId) {
    throw new Error("No business selected");
  }
  return `/businesses/${businessId}/payments`;
}

export async function listPayments() {
  const res = await api.get(getBasePath());
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
  const res = await api.put(`${getBasePath()}/${paymentId}`, payload);
  return res.data;
}

export async function deletePayment(paymentId) {
  await api.delete(`${getBasePath()}/${paymentId}`);
}
