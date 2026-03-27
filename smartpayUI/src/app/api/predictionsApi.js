import api from "./axios";

export async function getPredictionSummary(businessId) {
  const response = await api.get(
    `/businesses/${businessId}/predictions/summary`,
  );
  return response.data;
}
