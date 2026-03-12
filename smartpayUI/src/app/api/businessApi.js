import api from "./apiClient";

export async function listMyBusinesses() {
  const res = await api.get("/businesses");
  return res.data;
}

export async function createBusiness(payload) {
  const res = await api.post("/businesses", payload);
  return res.data;
}
