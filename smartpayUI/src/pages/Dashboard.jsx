import { useEffect, useState } from "react";
import { useBusinessSync } from "../hooks/useBusinessSync";
import { getDashboardSummary } from "../api/reportsApi";
import { listPayments } from "../api/paymentApi";
import StatCard from "../components/dashboard/StatCard";
import RecentPaymentsTable from "../components/dashboard/RecentPaymentsTable";

export default function Dashboard() {
  const businessId = useBusinessSync();
  const [summary, setSummary] = useState(null);
  const [recentPayments, setRecentPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

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
        const [summaryData, paymentsData] = await Promise.all([
          getDashboardSummary(),
          listPayments(),
        ]);
        setSummary(summaryData);
        const all = Array.isArray(paymentsData)
          ? paymentsData
          : (paymentsData?.content ?? []);
        setRecentPayments(
          [...all].sort((a, b) => (b.id ?? 0) - (a.id ?? 0)).slice(0, 5),
        );
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
      {!businessId ? (
        <div className="text-sm text-slate-500">
          Select a business to view the dashboard.
        </div>
      ) : null}
      {loading ? (
        <div className="text-sm text-slate-500">Loading dashboard...</div>
      ) : error ? (
        <div className="text-sm text-red-600">{error}</div>
      ) : null}
      {summary ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
          <StatCard title="Total payments" value={summary.totalPayments} />
          <StatCard title="Pending" value={summary.pendingCount} />
          <StatCard title="Overdue" value={summary.overdueCount} />
          <StatCard title="Paid" value={summary.paidCount} />
        </div>
      ) : null}
      {!loading && !error && businessId ? (
        <RecentPaymentsTable rows={recentPayments} />
      ) : null}
    </div>
  );
}
