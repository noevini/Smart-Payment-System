import api from "./apiClient";

export async function listNotifications() {
  const res = await api.get("/notifications");
  return res.data.content;
}

export async function markNotificationAsRead(id) {
  await api.patch(`/notifications/${id}/read`);
}
