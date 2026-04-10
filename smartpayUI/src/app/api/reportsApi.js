import api from "./apiClient";

/**
 * Reports API — wraps the backend /reports endpoints.
 *
 * These endpoints aggregate data server-side, which is more
 * efficient and accurate than calculating on the frontend.
 */

/**
 * Fetches the dashboard summary for the current business.
 * Returns counts and amounts for all payment statuses.
 * Backend: GET /reports/dashboard
 */
export async function getDashboardSummary() {
  const res = await api.get("/reports/dashboard");
  return res.data;
}

/**
 * Fetches monthly revenue breakdown for the current business.
 * @param {number} months - number of months to look back (default 6, max 24)
 * Backend: GET /reports/monthly-revenue?months={months}
 */
export async function getMonthlyRevenue(months = 6) {
  const res = await api.get("/reports/monthly-revenue", {
    params: { months },
  });
  return res.data;
}

/**
 * Fetches the list of overdue payments for the current business.
 * @param {number} limit - max number of results (default 20, max 100)
 * Backend: GET /reports/overdue?limit={limit}
 */
export async function getOverduePayments(limit = 20) {
  const res = await api.get("/reports/overdue", {
    params: { limit },
  });
  return res.data;
}
