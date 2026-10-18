import { useEffect, useState } from "react";
import { createTransaction } from "../../api/transactionApi";

export default function CreateTransactionModal({ open, onClose, onCreated }) {
  const [form, setForm] = useState({
    type: "INCOME",
    amount: "",
    currency: "GBP",
    description: "",
    occurredAt: new Date().toISOString().slice(0, 16),
  });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!open) return;
    setError("");
    setForm({
      type: "INCOME",
      amount: "",
      currency: "GBP",
      description: "",
      occurredAt: new Date().toISOString().slice(0, 16),
    });
  }, [open]);

  function handleClose() {
    onClose?.();
    setError("");
  }

  async function onSubmit(e) {
    e.preventDefault();
    setError("");

    const amountNum = Number(form.amount);
    if (!Number.isFinite(amountNum) || amountNum <= 0) {
      return setError("Amount must be greater than 0.");
    }
    if (!form.occurredAt) {
      return setError("Date is required.");
    }

    try {
      setSaving(true);
      await createTransaction({
        type: form.type,
        amount: amountNum,
        currency: form.currency.trim().toUpperCase() || "GBP",
        description: form.description.trim() || null,
        occurredAt: new Date(form.occurredAt).toISOString(),
      });
      handleClose();
      await onCreated?.();
    } catch (e2) {
      console.error(e2);
      setError(
        e2?.response?.data?.message ||
          e2?.response?.data?.error ||
          "Failed to create transaction.",
      );
    } finally {
      setSaving(false);
    }
  }

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center px-4">
      <div
        className="absolute inset-0 bg-slate-900/50 backdrop-blur-sm"
        onClick={handleClose}
      />

      <div className="relative w-full max-w-lg card-surface overflow-hidden">
        <div className="flex items-center justify-between border-b border-slate-200 px-6 py-4">
          <div>
            <h2 className="text-lg font-semibold text-slate-900">
              New transaction
            </h2>
            <p className="text-sm text-slate-500 mt-1">
              Record an income or expense for your business.
            </p>
          </div>
          <button onClick={handleClose} className="btn-secondary">
            Close
          </button>
        </div>

        <form onSubmit={onSubmit} className="p-6 space-y-5">
          <div className="space-y-2">
            <label className="text-sm font-medium text-slate-700">Type</label>
            <select
              value={form.type}
              onChange={(e) => setForm((f) => ({ ...f, type: e.target.value }))}
              className="select-field"
            >
              <option value="INCOME">Income</option>
              <option value="EXPENSE">Expense</option>
            </select>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div className="space-y-2">
              <label className="text-sm font-medium text-slate-700">
                Amount
              </label>
              <input
                type="number"
                step="0.01"
                min="0.01"
                value={form.amount}
                onChange={(e) =>
                  setForm((f) => ({ ...f, amount: e.target.value }))
                }
                className="input-field"
                placeholder="e.g. 250.00"
                required
              />
            </div>

            <div className="space-y-2">
              <label className="text-sm font-medium text-slate-700">
                Currency
              </label>
              <input
                type="text"
                value={form.currency}
                onChange={(e) =>
                  setForm((f) => ({ ...f, currency: e.target.value }))
                }
                className="input-field"
                placeholder="e.g. GBP"
                maxLength={3}
              />
            </div>
          </div>

          <div className="space-y-2">
            <label className="text-sm font-medium text-slate-700">
              Date &amp; time
            </label>
            <input
              type="datetime-local"
              value={form.occurredAt}
              onChange={(e) =>
                setForm((f) => ({ ...f, occurredAt: e.target.value }))
              }
              className="input-field"
              required
            />
          </div>

          <div className="space-y-2">
            <label className="text-sm font-medium text-slate-700">
              Description
            </label>
            <input
              type="text"
              value={form.description}
              onChange={(e) =>
                setForm((f) => ({ ...f, description: e.target.value }))
              }
              className="input-field"
              placeholder="Optional"
            />
          </div>

          {error ? (
            <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600">
              {error}
            </div>
          ) : null}

          <div className="flex items-center justify-end gap-3 pt-2">
            <button
              type="button"
              onClick={handleClose}
              className="btn-secondary"
              disabled={saving}
            >
              Cancel
            </button>
            <button type="submit" className="btn-primary" disabled={saving}>
              {saving ? "Creating..." : "Create"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
