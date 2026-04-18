import api from "./client";
import { getSelectedBusinessId } from "../state/businessStorage";

function getBasePath() {
  const businessId = getSelectedBusinessId();
  if (!businessId) return null;
  return `/businesses/${businessId}/staff`;
}

export async function listStaff() {
  const base = getBasePath();
  if (!base) return [];
  const res = await api.get(base);
  return Array.isArray(res.data) ? res.data : [];
}

export async function createStaff(payload) {
  const res = await api.post("/auth/register", payload);
  return res.data;
}

export async function deleteStaff(userId) {
  const base = getBasePath();
  if (!base) return;
  await api.delete(`${base}/${userId}`);
}
