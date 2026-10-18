import api from "./client";

export async function listMyBusinesses() {
  const res = await api.get("/businesses");
  const data = res.data;

  // Accept common response shapes and always return an array.
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.content)) return data.content;
  if (Array.isArray(data?.items)) return data.items;

  return [];
}

export async function createBusiness(payload) {
  const res = await api.post("/businesses", payload);
  return res.data;
}
