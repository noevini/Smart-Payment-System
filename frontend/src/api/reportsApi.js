import api from "./client";
import { getSelectedBusinessId } from "../state/businessStorage";

/**
 * Reports API — wraps backend /businesses/{businessId}/reports endpoints.
 *
 * These endpoints aggregate data server-side, which is more
 * efficient and accurate than calculating on the frontend.
 */

function getReportsBasePath() {
  const businessId = getSelectedBusinessId();
  if (!businessId) {
    throw new Error("Missing selected business id");
  }
  return `/businesses/${businessId}/reports`;
}

/**
 * Fetches the dashboard summary for the current business.
 * Returns counts and amounts for all payment statuses.
 * Backend: GET /businesses/{businessId}/reports/dashboard
 */
export async function getDashboardSummary() {
  const res = await api.get(`${getReportsBasePath()}/dashboard`);
  return res.data;
}

/**
 * Fetches monthly revenue breakdown for the current business.
 * @param {number} months - number of months to look back (default 6, max 24)
 * Backend: GET /businesses/{businessId}/reports/monthly-revenue?months={months}
 */
export async function getMonthlyRevenue(months = 6) {
  const res = await api.get(`${getReportsBasePath()}/monthly-revenue`, {
    params: { months },
  });
  return res.data;
}

/**
 * Fetches the list of overdue payments for the current business.
 * @param {number} limit - max number of results (default 20, max 100)
 * Backend: GET /businesses/{businessId}/reports/overdue?limit={limit}
 */
export async function getOverduePayments(limit = 20) {
  const res = await api.get(`${getReportsBasePath()}/overdue`, {
    params: { limit },
  });
  return res.data;
}
