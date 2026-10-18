import { useCallback, useEffect, useState } from "react";
import {
  getDashboardSummary,
  getOverduePayments,
  getMonthlyRevenue,
} from "../api/reportsApi";
import { useBusinessSync } from "./useBusinessSync";

/**
 * useReports — fetches all report data for the selected business.
 *
 * Runs three backend requests in parallel:
 * - /businesses/{businessId}/reports/dashboard
 * - /businesses/{businessId}/reports/overdue
 * - /businesses/{businessId}/reports/monthly-revenue
 *
 * Usage:
 *   const { summary, overduePayments, monthlyRevenue, loading, error } = useReports();
 */
export function useReports() {
  const businessId = useBusinessSync();

  const [summary, setSummary] = useState(null);
  const [overduePayments, setOverduePayments] = useState([]);
  const [monthlyRevenue, setMonthlyRevenue] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    if (!businessId) {
      setSummary(null);
      setOverduePayments([]);
      setMonthlyRevenue([]);
      setLoading(false);
      return;
    }
    try {
      setLoading(true);
      setError("");
      const [summaryData, overdueData, revenueData] = await Promise.all([
        getDashboardSummary(),
        getOverduePayments(),
        getMonthlyRevenue(6),
      ]);
      setSummary(summaryData);
      setOverduePayments(overdueData?.items ?? []);
      setMonthlyRevenue(revenueData?.points ?? []);
    } catch (e) {
      console.error("Failed to load reports:", e);
      setError("Failed to load reports.");
    } finally {
      setLoading(false);
    }
  }, [businessId]);

  useEffect(() => {
    load();
  }, [load]);

  return {
    summary,
    overduePayments,
    monthlyRevenue,
    loading,
    error,
    businessId,
  };
}
