import { useEffect, useMemo, useState } from "react";
import { listPayments } from "../app/api/paymentApi";
import { getSelectedBusinessId } from "../app/state/businessStorage";
import StatCard from "../components/dashboard/StatCard";
import RecentPaymentsTable from "../components/dashboard/RecentPaymentsTable";

export default function Dashboard() {
  const [businessId, setBusinessId] = useState(getSelectedBusinessId());
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function load() {
    try {
      setLoading(true);
      setError("");
      const data = await listPayments();
      setPayments(Array.isArray(data) ? data : (data?.content ?? []));
    } catch (e) {
      console.error(e);
      setError("Failed to load dashboard data.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    function syncBusiness() {
      setBusinessId(getSelectedBusinessId());
    }

    window.addEventListener("storage", syncBusiness);
    window.addEventListener("focus", syncBusiness);
    syncBusiness();

    return () => {
      window.removeEventListener("storage", syncBusiness);
      window.removeEventListener("focus", syncBusiness);
    };
  }, []);

  useEffect(() => {
    if (!businessId) {
      setPayments([]);
      setLoading(false);
      return;
    }
    load();
  }, [businessId]);

  const stats = useMemo(() => {
    const total = payments.length;
    const pending = payments.filter((p) => p.status === "PENDING").length;
    const overdue = payments.filter((p) => p.status === "OVERDUE").length;
    const paid = payments.filter((p) => p.status === "PAID").length;

    return { total, pending, overdue, paid };
  }, [payments]);

  const recent = useMemo(() => {
    const copy = [...payments];
    copy.sort((a, b) => (b.id ?? 0) - (a.id ?? 0));
    return copy.slice(0, 5);
  }, [payments]);

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

      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
        <StatCard title="Total payments" value={stats.total} />
        <StatCard title="Pending" value={stats.pending} />
        <StatCard title="Overdue" value={stats.overdue} />
        <StatCard title="Paid" value={stats.paid} />
      </div>

      <RecentPaymentsTable rows={recent} />
    </div>
  );
}
