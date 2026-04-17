import { useEffect, useState } from "react";
import {
  getDashboardSummary,
  getOverduePayments,
  getMonthlyRevenue,
} from "../api/reportsApi";
import { getSelectedBusinessId } from "../state/businessStorage";
import ReportCard from "../components/reports/ReportCard";
import OverduePaymentsTable from "../components/reports/OverduePaymentsTable";

/**
 * Reports page — shows financial reports for the selected business.
 *
 * Uses three backend endpoints:
 * - GET /businesses/{businessId}/reports/dashboard — summary counts and amounts
 * - GET /businesses/{businessId}/reports/overdue — list of overdue payments
 * - GET /businesses/{businessId}/reports/monthly-revenue — revenue breakdown by month
 *
 * Previously fetched all payments and calculated everything on the frontend.
 * Now delegates to dedicated backend report endpoints for accuracy.
 */
export default function Reports() {
  // Track selected business — reacts to changes via storage/focus events
  const [businessId, setBusinessId] = useState(getSelectedBusinessId());

  // Data from each backend endpoint
  const [summary, setSummary] = useState(null);
  const [overduePayments, setOverduePayments] = useState([]);
  const [monthlyRevenue, setMonthlyRevenue] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  /**
   * Syncs businessId from localStorage.
   * Listening to both "storage" (other tabs) and "focus" (same tab via Topbar).
   */
  useEffect(() => {
    function sync() {
      setBusinessId(getSelectedBusinessId());
    }
    window.addEventListener("storage", sync);
    window.addEventListener("focus", sync);
    sync();
    return () => {
      window.removeEventListener("storage", sync);
      window.removeEventListener("focus", sync);
    };
  }, []);

  /**
   * Fetches all report data when the selected business changes.
   * Runs all three requests in parallel with Promise.all for efficiency.
   */
  useEffect(() => {
    if (!businessId) {
      setSummary(null);
      setOverduePayments([]);
      setMonthlyRevenue([]);
      setLoading(false);
      return;
    }

    async function load() {
      try {
        setLoading(true);
        setError("");

        // Run all report requests in parallel
        const [summaryData, overdueData, revenueData] = await Promise.all([
          getDashboardSummary(),
          getOverduePayments(),
          getMonthlyRevenue(6),
        ]);

        setSummary(summaryData);
        setOverduePayments(overdueData?.items ?? []);
        setMonthlyRevenue(revenueData?.points ?? []);
      } catch (e) {
        console.error(e);
        const backendMessage = e?.response?.data?.message;
        const status = e?.response?.status;
        setError(
          backendMessage
            ? `Failed to load reports (${status ?? "error"}): ${backendMessage}`
            : status
              ? `Failed to load reports (${status}).`
              : "Failed to load reports.",
        );
      } finally {
        setLoading(false);
      }
    }

    load();
  }, [businessId]);

  return (
    <div className="page-shell">
      <div>
        <h1 className="page-title">Reports</h1>
        <p className="page-subtitle">
          Financial overview for your selected business.
        </p>
      </div>

      {/* No business selected */}
      {!businessId ? (
        <div className="text-sm text-slate-500">
          Select a business to view reports.
        </div>
      ) : null}

      {/* Loading / error states */}
      {loading ? (
        <div className="text-sm text-slate-500">Loading reports...</div>
      ) : error ? (
        <div className="text-sm text-red-600">{error}</div>
      ) : null}

      {/* Summary cards — from /businesses/{businessId}/reports/dashboard */}
      {summary ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
          <ReportCard title="Total revenue" value={`£${summary.paidAmount}`} />
          <ReportCard title="Total payments" value={summary.totalPayments} />
          <ReportCard title="Pending payments" value={summary.pendingCount} />
          <ReportCard title="Overdue payments" value={summary.overdueCount} />
        </div>
      ) : null}

      {/* Monthly revenue breakdown */}
      {monthlyRevenue.length > 0 ? (
        <div className="card-surface card-content">
          <h2 className="font-semibold text-slate-900 mb-4">
            Monthly Revenue (last 6 months)
          </h2>
          <div className="space-y-2">
            {monthlyRevenue.map((point, i) => (
              <div
                key={i}
                className="flex items-center justify-between text-sm"
              >
                {/* Format month start date */}
                <span className="text-slate-600">
                  {point.monthStart
                    ? new Date(point.monthStart).toLocaleDateString("en-GB", {
                        month: "long",
                        year: "numeric",
                      })
                    : "—"}
                </span>
                <span className="font-semibold text-slate-900">
                  £{point.revenue} ({point.count} payments)
                </span>
              </div>
            ))}
          </div>
        </div>
      ) : null}

      {/* Overdue payments table — from /businesses/{businessId}/reports/overdue */}
      {!loading && !error && businessId ? (
        <OverduePaymentsTable rows={overduePayments} />
      ) : null}
    </div>
  );
}
