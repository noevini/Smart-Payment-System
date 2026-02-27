import api from "./apiClient";

export async function listMyBusinesses() {
  const res = await api.get("/businesses");
  return res.data;
}

export async function createBusiness(name) {
  const res = await api.post("/businesses", { name });
  return res.data;
}
