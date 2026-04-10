import api from "./apiClient";

export async function getPredictionSummary(businessId) {
  const response = await api.get(
    `/businesses/${businessId}/predictions/summary`,
  );
  return response.data;
}
