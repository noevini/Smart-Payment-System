import { useMemo, useState } from "react";
import { payments as initialPayments } from "../data/mock";
import Badge from "../components/Badge";

export default function Payments() {
  const [filter, setFilter] = useState("ALL");
  const [rows, setRows] = useState(initialPayments);

  const filtered = useMemo(() => {
    if (filter === "ALL") return rows;
    return rows.filter((p) => p.status === filter);
  }, [rows, filter]);

  function markAsPaid(id) {
    setRows((prev) =>
      prev.map((p) => (p.id === id ? { ...p, status: "PAID" } : p)),
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Payments</h1>
        <p className="text-gray-600">
          Manage payments (mock data for IPD demo).
        </p>
      </div>

      {/* Filters */}
      <div className="flex flex-wrap gap-2">
        {["ALL", "PAID", "PENDING", "OVERDUE"].map((k) => (
          <button
            key={k}
            onClick={() => setFilter(k)}
            className={`px-3 py-2 rounded text-sm border ${
              filter === k
                ? "bg-gray-200 font-medium"
                : "bg-white hover:bg-gray-50"
            }`}
          >
            {k === "ALL" ? "All" : k}
          </button>
        ))}
      </div>

      {/* Table */}
      <div className="bg-white border rounded">
        <div className="p-4 border-b font-semibold">All payments</div>

        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="text-left text-gray-500">
              <tr>
                <th className="p-4">ID</th>
                <th className="p-4">Customer</th>
                <th className="p-4">Amount</th>
                <th className="p-4">Status</th>
                <th className="p-4">Due date</th>
                <th className="p-4">Action</th>
              </tr>
            </thead>

            <tbody>
              {filtered.map((p) => (
                <tr key={p.id} className="border-t">
                  <td className="p-4 font-mono">{p.id}</td>
                  <td className="p-4">{p.customer}</td>
                  <td className="p-4">£{p.amount}</td>
                  <td className="p-4">
                    <Badge status={p.status} />
                  </td>
                  <td className="p-4">{p.dueDate}</td>
                  <td className="p-4">
                    {p.status !== "PAID" ? (
                      <button
                        onClick={() => markAsPaid(p.id)}
                        className="px-3 py-1.5 rounded bg-blue-600 text-white text-xs font-medium"
                      >
                        Mark as paid
                      </button>
                    ) : (
                      <span className="text-xs text-gray-500">—</span>
                    )}
                  </td>
                </tr>
              ))}

              {filtered.length === 0 ? (
                <tr className="border-t">
                  <td className="p-4 text-gray-500" colSpan={6}>
                    No payments found.
                  </td>
                </tr>
              ) : null}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
