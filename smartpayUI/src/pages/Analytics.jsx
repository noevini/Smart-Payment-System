import { useEffect, useState } from "react";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  PieChart,
  Pie,
  Cell,
  Legend,
  ResponsiveContainer,
} from "recharts";
import { getAnalyticsSummary } from "../api/analyticsApi";
import { getSelectedBusinessId } from "../state/businessStorage";

const STATUS_COLORS = {
  PAID: "#10b981",
  PENDING: "#f59e0b",
  OVERDUE: "#ef4444",
  CANCELED: "#6b7280",
};

const PIE_COLORS = ["#10b981", "#f59e0b", "#ef4444"];

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

  const barData = data
    ? [
        { status: "PAID", count: data.paidPayments ?? 0 },
        { status: "PENDING", count: data.pendingPayments ?? 0 },
        { status: "OVERDUE", count: data.overduePayments ?? 0 },
        { status: "CANCELED", count: data.canceledPayments ?? 0 },
      ]
    : [];

  const pieData = data
    ? [
        { name: "Total Revenue", value: Number(data.totalRevenue ?? 0) },
        { name: "Pending Amount", value: Number(data.totalPendingAmount ?? 0) },
        { name: "Overdue Amount", value: Number(data.totalOverdueAmount ?? 0) },
      ]
    : [];

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
        <>
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

          <div className="grid grid-cols-1 xl:grid-cols-2 gap-6 mt-6">
            <div className="card-surface p-6">
              <h2 className="text-base font-semibold text-slate-800 mb-4">
                Payments by Status
              </h2>
              <ResponsiveContainer width="100%" height={260}>
                <BarChart data={barData} barCategoryGap="35%">
                  <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                  <XAxis
                    dataKey="status"
                    tick={{ fontSize: 12, fill: "#64748b" }}
                    axisLine={false}
                    tickLine={false}
                  />
                  <YAxis
                    allowDecimals={false}
                    tick={{ fontSize: 12, fill: "#64748b" }}
                    axisLine={false}
                    tickLine={false}
                  />
                  <Tooltip
                    contentStyle={{
                      borderRadius: "8px",
                      border: "1px solid #e2e8f0",
                      fontSize: "13px",
                    }}
                    cursor={{ fill: "#f1f5f9" }}
                  />
                  <Bar dataKey="count" radius={[4, 4, 0, 0]}>
                    {barData.map((entry) => (
                      <Cell
                        key={entry.status}
                        fill={STATUS_COLORS[entry.status]}
                      />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>

            <div className="card-surface p-6">
              <h2 className="text-base font-semibold text-slate-800 mb-4">
                Financial Overview
              </h2>
              <ResponsiveContainer width="100%" height={260}>
                <PieChart>
                  <Pie
                    data={pieData}
                    cx="50%"
                    cy="45%"
                    innerRadius={60}
                    outerRadius={100}
                    paddingAngle={3}
                    dataKey="value"
                  >
                    {pieData.map((entry, index) => (
                      <Cell
                        key={entry.name}
                        fill={PIE_COLORS[index]}
                      />
                    ))}
                  </Pie>
                  <Tooltip
                    formatter={(value) => `£${value.toLocaleString()}`}
                    contentStyle={{
                      borderRadius: "8px",
                      border: "1px solid #e2e8f0",
                      fontSize: "13px",
                    }}
                  />
                  <Legend
                    iconType="circle"
                    iconSize={8}
                    wrapperStyle={{ fontSize: "12px", color: "#64748b" }}
                  />
                </PieChart>
              </ResponsiveContainer>
            </div>
          </div>
        </>
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
