import { useEffect, useMemo, useState } from "react";
import { listPayments } from "../app/api/paymentApi";
import { getSelectedBusinessId } from "../app/state/businessStorage";
import ReportCard from "../components/reports/ReportCard";
import OverduePaymentsTable from "../components/reports/OverduePaymentsTable";

export default function Reports() {
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
      setError("Failed to load reports.");
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

    const paidPayments = payments.filter((p) => p.status === "PAID");
    const pendingPayments = payments.filter((p) => p.status === "PENDING");
    const overduePayments = payments.filter((p) => p.status === "OVERDUE");

    const revenue = paidPayments.reduce(
      (sum, p) => sum + Number(p.amount || 0),
      0,
    );

    return {
      total,
      revenue,
      pendingCount: pendingPayments.length,
      overdueCount: overduePayments.length,
      overduePayments,
    };
  }, [payments]);

  return (
    <div className="page-shell">
      <div>
        <h1 className="page-title">Reports</h1>
        <p className="page-subtitle">
          Financial overview for your selected business.
        </p>
      </div>

      {!businessId ? (
        <div className="text-sm text-slate-500">
          Select a business to view reports.
        </div>
      ) : null}

      {loading ? (
        <div className="text-sm text-slate-500">Loading reports...</div>
      ) : error ? (
        <div className="text-sm text-red-600">{error}</div>
      ) : businessId ? (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
            <ReportCard title="Total revenue" value={`£${stats.revenue}`} />
            <ReportCard title="Total payments" value={stats.total} />
            <ReportCard title="Pending payments" value={stats.pendingCount} />
            <ReportCard title="Overdue payments" value={stats.overdueCount} />
          </div>

          <OverduePaymentsTable rows={stats.overduePayments} />
        </>
      ) : null}
    </div>
  );
}
