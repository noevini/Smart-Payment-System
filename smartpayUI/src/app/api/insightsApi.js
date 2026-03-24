import api from "./axios";

export async function getInsightSummary(businessId) {
  const response = await api.get(`/businesses/${businessId}/insights/summary`);
  return response.data;
}
