const KEY = "smartpay_business_id";

export function getSelectedBusinessId() {
  const raw = localStorage.getItem(KEY);
  if (!raw) return null;

  const id = Number(raw);
  return Number.isFinite(id) ? id : null;
}

export function setSelectedBusinessId(businessId) {
  localStorage.setItem(KEY, String(businessId));
}

export function clearSelectedBusinessId() {
  localStorage.removeItem(KEY);
}
