import { useEffect, useMemo, useState } from "react";
import { listPayments } from "../app/api/paymentApi";
import { getSelectedBusinessId } from "../app/business/businessStorage";

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
      setPayments(data);
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

    const paid = payments.filter((p) => p.status === "PAID");

    const revenue = paid.reduce((sum, p) => sum + Number(p.amount), 0);

    const pending = payments.filter((p) => p.status === "PENDING").length;

    const overdue = payments.filter((p) => p.status === "OVERDUE");

    return {
      total,
      revenue,
      pending,
      overdueCount: overdue.length,
      overdue,
    };
  }, [payments]);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Reports</h1>

        <p className="text-gray-600">Financial overview for your business.</p>
      </div>

      {!businessId && (
        <div className="text-sm text-gray-600">
          Select a business to view reports.
        </div>
      )}

      {loading && (
        <div className="text-sm text-gray-600">Loading reports...</div>
      )}

      {error && <div className="text-sm text-red-600">{error}</div>}

      {!loading && !error && businessId && (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            <ReportCard title="Total revenue" value={`£${stats.revenue}`} />

            <ReportCard title="Total payments" value={stats.total} />

            <ReportCard title="Pending payments" value={stats.pending} />

            <ReportCard title="Overdue payments" value={stats.overdueCount} />
          </div>

          <OverduePaymentsTable rows={stats.overdue} />
        </>
      )}
    </div>
  );
}
