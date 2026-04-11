import { useEffect, useState } from "react";
import { getAnalyticsSummary } from "../api/analyticsApi";
import { getSelectedBusinessId } from "../state/businessStorage";

export default function Analytics() {
  const [businessId, setBusinessId] = useState(getSelectedBusinessId());
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

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

  useEffect(() => {
    if (!businessId) {
      setData(null);
      return;
    }

    async function fetchAnalytics() {
      try {
        setLoading(true);
        setError("");
        const result = await getAnalyticsSummary(businessId);
        setData(result);
      } catch (err) {
        console.error(err);
        setError("Failed to load analytics.");
      } finally {
        setLoading(false);
      }
    }

    fetchAnalytics();
  }, [businessId]);

  if (!businessId) {
    return (
      <div className="page-shell">
        <h1 className="page-title">Analytics</h1>
        <div className="text-sm text-slate-500">
          Select a business to view analytics.
        </div>
      </div>
    );
  }

  return (
    <div className="page-shell">
      <div>
        <h1 className="page-title">Analytics</h1>
        <p className="page-subtitle">
          Payment analytics for your selected business.
        </p>
      </div>

      {loading ? (
        <div className="text-sm text-slate-500">Loading analytics...</div>
      ) : error ? (
        <div className="text-sm text-red-600">{error}</div>
      ) : data ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-4">
          <AnalyticsCard title="Total Payments" value={data.totalPayments} />
          <AnalyticsCard title="Paid Payments" value={data.paidPayments} />
          <AnalyticsCard
            title="Pending Payments"
            value={data.pendingPayments}
          />
          <AnalyticsCard
            title="Overdue Payments"
            value={data.overduePayments}
          />
          <AnalyticsCard
            title="Total Revenue"
            value={`£${data.totalRevenue}`}
          />
          <AnalyticsCard
            title="Pending Amount"
            value={`£${data.totalPendingAmount}`}
          />
          <AnalyticsCard
            title="Overdue Amount"
            value={`£${data.totalOverdueAmount}`}
          />
        </div>
      ) : null}
    </div>
  );
}

function AnalyticsCard({ title, value }) {
  return (
    <div className="card-surface card-content">
      <div className="text-sm font-medium text-slate-500">{title}</div>
      <div className="mt-2 text-3xl font-bold tracking-tight text-slate-900">
        {value}
      </div>
    </div>
  );
}
