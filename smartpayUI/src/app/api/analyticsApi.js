import api from "./apiClient";

export async function getAnalyticsSummary(businessId) {
  const response = await api.get(`/businesses/${businessId}/analytics/summary`);
  return response.data;
}
