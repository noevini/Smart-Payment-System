import { useEffect, useMemo, useState } from "react";
import { listPayments, updatePayment, deletePayment } from "../api/paymentApi";
import { getSelectedBusinessId } from "../state/businessStorage";
import Badge from "../components/ui/Badge";
import CreatePaymentModal from "../components/payments/CreatePaymentModal";

export default function Payments() {
  const [businessId, setBusinessId] = useState(getSelectedBusinessId());
  const [filter, setFilter] = useState("ALL");
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [open, setOpen] = useState(false);
  const [editingPayment, setEditingPayment] = useState(null);

  async function loadPayments() {
    try {
      setLoading(true);
      setError("");
      const data = await listPayments();
      setRows(Array.isArray(data) ? data : (data?.content ?? []));
    } catch (e) {
      console.error(e);
      setError("Failed to load payments.");
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
      setRows([]);
      setLoading(false);
      return;
    }
    loadPayments();
  }, [businessId]);

  const filtered = useMemo(() => {
    if (filter === "ALL") return rows;
    return rows.filter((p) => p.status === filter);
  }, [rows, filter]);

  return (
    <div className="page-shell">
      <div className="flex items-start justify-between gap-4">
        <div>
          <h1 className="page-title">Payments</h1>
          <p className="page-subtitle">
            Manage payments for your selected business.
          </p>
        </div>

        <button
          onClick={() => {
            if (!businessId) {
              alert("Select a business first.");
              return;
            }
            setEditingPayment(null);
            setOpen(true);
          }}
          className="btn-primary"
          disabled={!businessId}
        >
          New payment
        </button>
      </div>

      {!businessId ? (
        <div className="text-sm text-slate-500">
          Select a business to view payments.
        </div>
      ) : null}

      <div className="flex flex-wrap gap-2">
        {["ALL", "PAID", "PENDING", "OVERDUE", "CANCELED"].map((k) => (
          <button
            key={k}
            onClick={() => setFilter(k)}
            className={filter === k ? "btn-primary" : "btn-secondary"}
          >
            {k === "ALL" ? "All" : k}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="text-sm text-slate-500">Loading payments...</div>
      ) : error ? (
        <div className="text-sm text-red-600">{error}</div>
      ) : null}

      <div className="card-surface">
        <div className="p-4 border-b border-slate-200 font-semibold">
          All payments
        </div>

        <div className="table-shell">
          <table className="table-base">
            <thead className="table-head">
              <tr>
                <th className="table-th">ID</th>
                <th className="table-th">Direction</th>
                <th className="table-th">Amount</th>
                <th className="table-th">Status</th>
                <th className="table-th">Due date</th>
                <th className="table-th">Paid at</th>
                <th className="table-th">Actions</th>
              </tr>
            </thead>

            <tbody>
              {filtered.map((p) => (
                <tr key={p.id} className="table-row">
                  <td className="table-td font-mono">{p.id}</td>

                  <td className="table-td">{p.direction ?? "—"}</td>

                  <td className="table-td">
                    {p.currency} {p.amount}
                  </td>

                  <td className="table-td">
                    <Badge status={p.status} />
                  </td>

                  <td className="table-td">
                    {p.dueDate ? String(p.dueDate).slice(0, 10) : "—"}
                  </td>

                  <td className="table-td">
                    {p.paidAt ? String(p.paidAt).slice(0, 10) : "—"}
                  </td>

                  <td className="table-td">
                    <div className="flex flex-wrap gap-2">
                      <button
                        onClick={() => {
                          setEditingPayment(p);
                          setOpen(true);
                        }}
                        className="btn-secondary"
                      >
                        Edit
                      </button>

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
                            className="btn-secondary"
                          >
                            Mark as paid
                          </button>

                          <button
                            onClick={async () => {
                              try {
                                await updatePayment(p.id, {
                                  status: "CANCELED",
                                });
                                await loadPayments();
                              } catch (e) {
                                console.error(e);
                                alert("Failed to cancel payment.");
                              }
                            }}
                            className="btn-secondary"
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
                        className="btn-danger"
                      >
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}

              {!loading && filtered.length === 0 ? (
                <tr className="table-row">
                  <td className="table-td text-slate-500" colSpan={7}>
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
        payment={editingPayment}
        onClose={() => {
          setOpen(false);
          setEditingPayment(null);
        }}
        onCreated={loadPayments}
      />
    </div>
  );
}
