import api from "./client";
import { getSelectedBusinessId } from "../state/businessStorage";

function getBasePath() {
  const businessId = getSelectedBusinessId();
  if (!businessId) return null;
  return `/businesses/${businessId}/transactions`;
}

export async function listTransactions() {
  const base = getBasePath();
  if (!base) return [];
  const res = await api.get(base);
  return res.data;
}

export async function createTransaction(payload) {
  const res = await api.post(getBasePath(), payload);
  return res.data;
}
