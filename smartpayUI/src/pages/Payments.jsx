import { useEffect, useMemo, useState } from "react";
import {
  listPayments,
  updatePayment,
  deletePayment,
} from "../app/api/paymentApi";
import Badge from "../components/Badge";
import CreatePaymentModal from "../components/payments/CreatePaymentModal";

export default function Payments() {
  const [filter, setFilter] = useState("ALL");
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [open, setOpen] = useState(false);

  async function loadPayments() {
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

  useEffect(() => {
    let cancelled = false;

    async function load() {
      try {
        setLoading(true);
        setError("");
        const data = await listPayments();
        if (!cancelled) setRows(data);
      } catch (e) {
        console.error(e);
        if (!cancelled) setError("Failed to load payments.");
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    load();
    const t = setTimeout(load, 250);

    return () => {
      cancelled = true;
      clearTimeout(t);
    };
  }, []);

  const filtered = useMemo(() => {
    if (filter === "ALL") return rows;
    return rows.filter((p) => p.status === filter);
  }, [rows, filter]);

  return (
    <div className="space-y-6">
      <div className="flex items-start justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold">Payments</h1>
          <p className="text-gray-600">
            Manage payments (connected to backend).
          </p>
        </div>

        <button
          onClick={() => setOpen(true)}
          className="px-4 py-2 rounded bg-black text-white text-sm hover:opacity-90"
        >
          New payment
        </button>
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
                <th className="p-4">Direction</th>
                <th className="p-4">Amount</th>
                <th className="p-4">Status</th>
                <th className="p-4">Due date</th>
                <th className="p-4">Paid at</th>
                <th className="p-4">Actions</th>
              </tr>
            </thead>

            <tbody>
              {filtered.map((p) => (
                <tr key={p.id} className="border-t">
                  <td className="p-4 font-mono">{p.id}</td>

                  <td className="p-4">{p.direction}</td>

                  <td className="p-4">
                    {p.currency} {p.amount}
                  </td>

                  <td className="p-4">
                    <Badge status={p.status} />
                  </td>

                  <td className="p-4">
                    {p.dueDate ? String(p.dueDate).slice(0, 10) : "—"}
                  </td>

                  <td className="p-4">
                    {p.paidAt ? String(p.paidAt).slice(0, 10) : "—"}
                  </td>
                  <td className="p-4 space-x-2">
                    {(p.status === "PENDING" || p.status === "OVERDUE") && (
                      <>
                        <button
                          onClick={async () => {
                            try {
                              await updatePayment(p.id, { status: "PAID" });
                              await loadPayments();
                            } catch (e) {
                              console.error(e);
                              alert("Failed to mark as paid.");
                            }
                          }}
                          className="px-3 py-2 rounded border text-sm hover:bg-gray-50"
                        >
                          Mark as paid
                        </button>

                        <button
                          onClick={async () => {
                            try {
                              await updatePayment(p.id, { status: "CANCELED" });
                              await loadPayments();
                            } catch (e) {
                              console.error(e);
                              alert("Failed to cancel payment.");
                            }
                          }}
                          className="px-3 py-2 rounded border text-sm hover:bg-gray-50"
                        >
                          Cancel
                        </button>
                      </>
                    )}

                    <button
                      onClick={async () => {
                        if (
                          !window.confirm(
                            "Are you sure you want to delete this payment?",
                          )
                        )
                          return;

                        try {
                          await deletePayment(p.id);
                          await loadPayments();
                        } catch (e) {
                          console.error(e);
                          alert("Failed to delete payment.");
                        }
                      }}
                      className="px-3 py-2 rounded border text-sm text-red-600 hover:bg-red-50"
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}

              {!loading && filtered.length === 0 ? (
                <tr className="border-t">
                  <td className="p-4 text-gray-500" colSpan={7}>
                    No payments found.
                  </td>
                </tr>
              ) : null}
            </tbody>
          </table>
        </div>
      </div>

      <CreatePaymentModal
        open={open}
        onClose={() => setOpen(false)}
        onCreated={loadPayments}
      />
    </div>
  );
}
