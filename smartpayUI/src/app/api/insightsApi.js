import api from "./apiClient";

export async function getInsightSummary(businessId) {
  const response = await api.get(`/businesses/${businessId}/insights/summary`);
  return response.data;
}
