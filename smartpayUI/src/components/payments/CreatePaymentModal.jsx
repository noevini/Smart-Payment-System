import { useEffect, useState } from "react";
import { createPayment, updatePayment } from "../../app/api/paymentApi";
import { listCustomers } from "../../app/api/customersApi";

export default function CreatePaymentModal({
  open,
  onClose,
  onCreated,
  payment,
}) {
  const isEdit = !!payment;

  const [customers, setCustomers] = useState([]);
  const [customersLoading, setCustomersLoading] = useState(false);
  const [customersError, setCustomersError] = useState("");

  const [form, setForm] = useState({
    customerId: "",
    direction: "RECEIVABLE",
    amount: "",
    currency: "",
    dueDate: "",
    description: "",
  });

  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  // Load customers when opening for create
  useEffect(() => {
    let cancelled = false;

    async function loadCustomers() {
      try {
        setCustomersLoading(true);
        setCustomersError("");
        const data = await listCustomers();
        if (!cancelled) {
          setCustomers(Array.isArray(data) ? data : (data?.content ?? []));
        }
      } catch (e) {
        console.error(e);
        if (!cancelled) setCustomersError("Failed to load customers.");
      } finally {
        if (!cancelled) setCustomersLoading(false);
      }
    }

    if (open && !isEdit) loadCustomers();

    return () => {
      cancelled = true;
    };
  }, [open, isEdit]);

  useEffect(() => {
    if (!open) return;
    setError("");

    if (isEdit && payment) {
      setForm({
        customerId: "",
        direction: payment.direction ?? "RECEIVABLE",
        amount: payment.amount != null ? String(payment.amount) : "",
        currency: payment.currency ?? "",
        dueDate: payment.dueDate ? String(payment.dueDate).slice(0, 10) : "",
        description: payment.description ?? "",
      });
    } else {
      setForm({
        customerId: "",
        direction: "RECEIVABLE",
        amount: "",
        currency: "",
        dueDate: "",
        description: "",
      });
    }
  }, [open, isEdit, payment]);

  function handleClose() {
    onClose?.();
    setError("");
  }

  async function onSubmit(e) {
    e.preventDefault();
    setError("");

    if (!isEdit && !form.customerId) {
      return setError("Please select a customer.");
    }

    const amountNum = Number(form.amount);
    if (!Number.isFinite(amountNum) || amountNum <= 0) {
      return setError("Amount must be greater than 0.");
    }

    if (!form.dueDate) {
      return setError("Due date is required.");
    }

    const payload = isEdit
      ? {
          amount: amountNum,
          currency: form.currency?.trim() || null,
          dueDate: new Date(form.dueDate).toISOString(),
          description: form.description?.trim() || null,
        }
      : {
          direction: form.direction,
          amount: amountNum,
          currency: form.currency?.trim() || null,
          dueDate: new Date(form.dueDate).toISOString(),
          description: form.description?.trim() || null,
        };

    try {
      setSaving(true);
      if (isEdit) {
        await updatePayment(payment.id, payload);
      } else {
        await createPayment(payload);
      }
      handleClose();
      await onCreated?.();
    } catch (e2) {
      console.error(e2);
      setError(
        e2?.response?.data?.message ||
          e2?.response?.data?.error ||
          (isEdit ? "Failed to update payment." : "Failed to create payment."),
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

      <div className="relative w-full max-w-2xl card-surface overflow-hidden">
        <div className="flex items-center justify-between border-b border-slate-200 px-6 py-4">
          <div>
            <h2 className="text-lg font-semibold text-slate-900">
              {isEdit ? "Edit payment" : "Create payment"}
            </h2>
            <p className="text-sm text-slate-500 mt-1">
              {isEdit
                ? "Update payment details for this record."
                : "Create a new payment for your selected business."}
            </p>
          </div>
          <button onClick={handleClose} className="btn-secondary">
            Close
          </button>
        </div>

        <form onSubmit={onSubmit} className="p-6 space-y-5">
          {!isEdit ? (
            <div className="space-y-2">
              <label className="text-sm font-medium text-slate-700">
                Customer
              </label>
              <select
                value={form.customerId}
                onChange={(e) =>
                  setForm((f) => ({ ...f, customerId: e.target.value }))
                }
                className="select-field"
                disabled={customersLoading}
              >
                <option value="">
                  {customersLoading ? "Loading..." : "Select a customer"}
                </option>
                {customers.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name ?? `Customer #${c.id}`}
                  </option>
                ))}
              </select>
              {customersError ? (
                <div className="text-xs text-red-600">{customersError}</div>
              ) : null}
            </div>
          ) : null}

          {!isEdit ? (
            <div className="space-y-2">
              <label className="text-sm font-medium text-slate-700">
                Direction
              </label>
              <select
                value={form.direction}
                onChange={(e) =>
                  setForm((f) => ({ ...f, direction: e.target.value }))
                }
                className="select-field"
              >
                <option value="RECEIVABLE">Receivable (money coming in)</option>
                <option value="PAYABLE">Payable (money going out)</option>
              </select>
            </div>
          ) : null}

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
                placeholder="e.g. 120.50"
              />
            </div>

            <div className="space-y-2">
              <label className="text-sm font-medium text-slate-700">
                Due date
              </label>
              <input
                type="date"
                value={form.dueDate}
                onChange={(e) =>
                  setForm((f) => ({ ...f, dueDate: e.target.value }))
                }
                className="input-field"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
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
              {saving
                ? isEdit
                  ? "Saving..."
                  : "Creating..."
                : isEdit
                  ? "Save"
                  : "Create"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
