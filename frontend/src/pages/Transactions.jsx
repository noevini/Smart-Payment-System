import { useMemo, useState } from "react";
import { useTransactions } from "../hooks/useTransactions";
import CreateTransactionModal from "../components/transactions/CreateTransactionModal";

export default function Transactions() {
  const { transactions, loading, error, businessId, reload } = useTransactions();
  const [filter, setFilter] = useState("ALL");
  const [open, setOpen] = useState(false);

  const filtered = useMemo(() => {
    if (filter === "ALL") return transactions;
    return transactions.filter((t) => t.type === filter);
  }, [transactions, filter]);

  const totalIncome = useMemo(
    () =>
      transactions
        .filter((t) => t.type === "INCOME")
        .reduce((sum, t) => sum + Number(t.amount ?? 0), 0),
    [transactions],
  );

  const totalExpenses = useMemo(
    () =>
      transactions
        .filter((t) => t.type === "EXPENSE")
        .reduce((sum, t) => sum + Number(t.amount ?? 0), 0),
    [transactions],
  );

  return (
    <div className="page-shell">
      <div className="flex items-start justify-between gap-4">
        <div>
          <h1 className="page-title">Transactions</h1>
          <p className="page-subtitle">
            Income and expenses for your selected business.
          </p>
        </div>

        <button
          onClick={() => {
            if (!businessId) {
              alert("Select a business first.");
              return;
            }
            setOpen(true);
          }}
          className="btn-primary"
          disabled={!businessId}
        >
          New transaction
        </button>
      </div>

      {!businessId ? (
        <div className="text-sm text-slate-500">
          Select a business to view transactions.
        </div>
      ) : null}

      {/* Summary cards */}
      {!loading && !error && businessId ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="card-surface card-content">
            <div className="text-sm font-medium text-slate-500">
              Total Income
            </div>
            <div className="mt-2 text-3xl font-bold tracking-tight text-emerald-600">
              £{totalIncome.toFixed(2)}
            </div>
          </div>
          <div className="card-surface card-content">
            <div className="text-sm font-medium text-slate-500">
              Total Expenses
            </div>
            <div className="mt-2 text-3xl font-bold tracking-tight text-red-500">
              £{totalExpenses.toFixed(2)}
            </div>
          </div>
        </div>
      ) : null}

      {/* Filter buttons */}
      <div className="flex flex-wrap gap-2">
        {["ALL", "INCOME", "EXPENSE"].map((k) => (
          <button
            key={k}
            onClick={() => setFilter(k)}
            className={filter === k ? "btn-primary" : "btn-secondary"}
          >
            {k === "ALL" ? "All" : k.charAt(0) + k.slice(1).toLowerCase()}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="text-sm text-slate-500">Loading transactions...</div>
      ) : error ? (
        <div className="text-sm text-red-600">{error}</div>
      ) : null}

      {/* Transactions table */}
      <div className="card-surface">
        <div className="p-4 border-b border-slate-200 font-semibold">
          All transactions
        </div>

        <div className="table-shell">
          <table className="table-base">
            <thead className="table-head">
              <tr>
                <th className="table-th">ID</th>
                <th className="table-th">Type</th>
                <th className="table-th">Amount</th>
                <th className="table-th">Currency</th>
                <th className="table-th">Description</th>
                <th className="table-th">Occurred At</th>
              </tr>
            </thead>

            <tbody>
              {filtered.map((t) => (
                <tr key={t.id} className="table-row">
                  <td className="table-td font-mono">{t.id}</td>
                  <td className="table-td">
                    <span
                      className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${
                        t.type === "INCOME"
                          ? "bg-emerald-100 text-emerald-700"
                          : "bg-red-100 text-red-700"
                      }`}
                    >
                      {t.type === "INCOME" ? "Income" : "Expense"}
                    </span>
                  </td>
                  <td className="table-td font-medium">
                    {t.type === "EXPENSE" ? "−" : "+"}
                    {Number(t.amount).toFixed(2)}
                  </td>
                  <td className="table-td">{t.currency ?? "—"}</td>
                  <td className="table-td text-slate-500">
                    {t.description || "—"}
                  </td>
                  <td className="table-td">
                    {t.occurredAt
                      ? new Date(t.occurredAt).toLocaleDateString("en-GB", {
                          day: "2-digit",
                          month: "short",
                          year: "numeric",
                          hour: "2-digit",
                          minute: "2-digit",
                        })
                      : "—"}
                  </td>
                </tr>
              ))}

              {!loading && filtered.length === 0 ? (
                <tr className="table-row">
                  <td className="table-td text-slate-500" colSpan={6}>
                    No transactions found.
                  </td>
                </tr>
              ) : null}
            </tbody>
          </table>
        </div>
      </div>

      <CreateTransactionModal
        open={open}
        onClose={() => setOpen(false)}
        onCreated={reload}
      />
    </div>
  );
}
