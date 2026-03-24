import { useEffect, useState } from "react";
import { getAnalyticsSummary } from "../app/api/analyticsApi";
import { getSelectedBusinessId } from "../app/business/businessStorage";

export default function Analytics() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const businessId = getSelectedBusinessId();

  useEffect(() => {
    if (!businessId) return;

    const fetchAnalytics = async () => {
      try {
        setLoading(true);
        setError("");

        const result = await getAnalyticsSummary(businessId);
        setData(result);
      } catch (err) {
        console.error(err);
        setError("Failed to load analytics");
      } finally {
        setLoading(false);
      }
    };

    fetchAnalytics();
  }, [businessId]);

  if (!businessId) {
    return <div className="p-4">No business selected</div>;
  }

  if (loading) {
    return <div className="p-4">Loading analytics...</div>;
  }

  if (error) {
    return <div className="p-4 text-red-500">{error}</div>;
  }

  if (!data) return null;

  return (
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-bold">Analytics</h1>

      <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
        <Card title="Total Payments" value={data.totalPayments} />
        <Card title="Paid Payments" value={data.paidPayments} />
        <Card title="Pending Payments" value={data.pendingPayments} />
        <Card title="Overdue Payments" value={data.overduePayments} />

        <Card title="Total Revenue" value={`£${data.totalRevenue}`} />
        <Card title="Pending Amount" value={`£${data.totalPendingAmount}`} />
        <Card title="Overdue Amount" value={`£${data.totalOverdueAmount}`} />
      </div>
    </div>
  );
}

function Card({ title, value }) {
  return (
    <div className="bg-white p-4 rounded-xl shadow">
      <p className="text-sm text-gray-500">{title}</p>
      <p className="text-xl font-semibold mt-1">{value}</p>
    </div>
  );
}
