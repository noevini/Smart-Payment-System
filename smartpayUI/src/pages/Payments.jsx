import { useEffect, useMemo, useState } from "react";
import { listPayments } from "../app/api/paymentApi";
import Badge from "../components/Badge";

export default function Payments() {
  const [filter, setFilter] = useState("ALL");
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function load() {
      try {
        setLoading(true);
        setError("");
        const data = await listPayments();
        setRows(data);
      } catch (e) {
        console.error(e);
        setError("Failed to load payments.");
      } finally {
        setLoading(false);
      }
    }

    load();
  }, []);

  const filtered = useMemo(() => {
    if (filter === "ALL") return rows;
    return rows.filter((p) => p.status === filter);
  }, [rows, filter]);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Payments</h1>
        <p className="text-gray-600">Manage payments (connected to backend).</p>
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

      {/* State messages */}
      {loading ? (
        <div className="text-sm text-gray-600">Loading payments...</div>
      ) : error ? (
        <div className="text-sm text-red-600">{error}</div>
      ) : null}

      {/* Table */}
      <div className="bg-white border rounded">
        <div className="p-4 border-b font-semibold">All payments</div>

        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="text-left text-gray-500">
              <tr>
                <th className="p-4">ID</th>
                <th className="p-4">Amount</th>
                <th className="p-4">Status</th>
                <th className="p-4">Due date</th>
                <th className="p-4">Description</th>
              </tr>
            </thead>

            <tbody>
              {filtered.map((p) => (
                <tr key={p.id} className="border-t">
                  <td className="p-4 font-mono">{p.id}</td>
                  <td className="p-4">
                    {p.currency} {p.amount}
                  </td>
                  <td className="p-4">
                    <Badge status={p.status} />
                  </td>
                  <td className="p-4">
                    {p.dueDate ? String(p.dueDate).slice(0, 10) : "—"}
                  </td>
                  <td className="p-4">{p.description || "—"}</td>
                </tr>
              ))}

              {!loading && filtered.length === 0 ? (
                <tr className="border-t">
                  <td className="p-4 text-gray-500" colSpan={5}>
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
