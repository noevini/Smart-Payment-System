import api from "./client";

export async function getAnalyticsSummary(businessId) {
  const response = await api.get(`/businesses/${businessId}/analytics/summary`);
  return response.data;
}
