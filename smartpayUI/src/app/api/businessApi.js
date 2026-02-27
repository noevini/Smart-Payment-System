import api from "./apiClient";

export async function listMyBusinesses() {
  const res = await api.get("/businesses");
  return res.data;
}
