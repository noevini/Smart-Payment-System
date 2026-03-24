import api from "./axios";

export async function getAnalyticsSummary(businessId) {
  const response = await api.get(`/businesses/${businessId}/analytics/summary`);
  return response.data;
}
