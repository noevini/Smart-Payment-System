import api from "./client";

export async function listNotifications() {
  const res = await api.get("/notifications");
  return res.data?.content ?? res.data ?? [];
}

export async function markNotificationAsRead(id) {
  await api.patch(`/notifications/${id}/read`);
}
