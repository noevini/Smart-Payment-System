import { useEffect, useState } from "react";
import { getDashboardSummary } from "../api/reportsApi";
import { getSelectedBusinessId } from "../state/businessStorage";
import StatCard from "../components/dashboard/StatCard";
import RecentPaymentsTable from "../components/dashboard/RecentPaymentsTable";
import { listPayments } from "../api/paymentApi";

/**
 * Dashboard page — shows a financial overview for the selected business.
 *
 * Uses two backend endpoints:
 * - GET /reports/dashboard — aggregated payment stats
 * - GET /businesses/{id}/payments — recent payments list
 *
 * Previously calculated stats on the frontend from the payments list.
 * Now delegates aggregation to the backend for accuracy and efficiency.
 */
export default function Dashboard() {
  // Track selected business — reacts to changes via storage/focus events
  const [businessId, setBusinessId] = useState(getSelectedBusinessId());

  // Dashboard summary from /reports/dashboard
  const [summary, setSummary] = useState(null);

  // Recent payments list for the table
  const [recentPayments, setRecentPayments] = useState([]);

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
   * Fetches dashboard data whenever the selected business changes.
   * Runs both requests in parallel with Promise.all for efficiency.
   */
  useEffect(() => {
    if (!businessId) {
      setSummary(null);
      setRecentPayments([]);
      setLoading(false);
      return;
    }

    async function load() {
      try {
        setLoading(true);
        setError("");

        // Run both requests in parallel
        const [summaryData, paymentsData] = await Promise.all([
          getDashboardSummary(),
          listPayments(),
        ]);

        setSummary(summaryData);

        // Sort by id descending and take the 5 most recent
        const all = Array.isArray(paymentsData)
          ? paymentsData
          : (paymentsData?.content ?? []);

        const sorted = [...all].sort((a, b) => (b.id ?? 0) - (a.id ?? 0));
        setRecentPayments(sorted.slice(0, 5));
      } catch (e) {
        console.error(e);
        setError("Failed to load dashboard data.");
      } finally {
        setLoading(false);
      }
    }

    load();
  }, [businessId]);

  return (
    <div className="page-shell">
      <div>
        <h1 className="page-title">Dashboard</h1>
        <p className="page-subtitle">Overview for your selected business.</p>
      </div>

      {/* No business selected */}
      {!businessId ? (
        <div className="text-sm text-slate-500">
          Select a business to view the dashboard.
        </div>
      ) : null}

      {/* Loading / error states */}
      {loading ? (
        <div className="text-sm text-slate-500">Loading dashboard...</div>
      ) : error ? (
        <div className="text-sm text-red-600">{error}</div>
      ) : null}

      {/* Stats grid — uses data from /reports/dashboard */}
      {summary ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
          <StatCard title="Total payments" value={summary.totalPayments} />
          <StatCard title="Pending" value={summary.pendingCount} />
          <StatCard title="Overdue" value={summary.overdueCount} />
          <StatCard title="Paid" value={summary.paidCount} />
        </div>
      ) : null}

      {/* Recent payments table */}
      {!loading && !error && businessId ? (
        <RecentPaymentsTable rows={recentPayments} />
      ) : null}
    </div>
  );
}
