import { useEffect, useState } from "react";
import { createPayment } from "../../app/api/paymentApi";
import { listCustomers } from "../../app/api/customersApi";

/**
 * Props:
 * - open: boolean
 * - onClose: () => void
 * - onCreated: () => Promise<void> | void   (chama refresh da tabela)
 */
export default function CreatePaymentModal({ open, onClose, onCreated }) {
  const [customers, setCustomers] = useState([]);
  const [customersLoading, setCustomersLoading] = useState(false);
  const [customersError, setCustomersError] = useState("");

  const [form, setForm] = useState({
    customerId: "",
    amount: "",
    dueDate: "",
    description: "",
    // direction: "IN", // se precisar no backend
  });

  const [creating, setCreating] = useState(false);
  const [createError, setCreateError] = useState("");

  useEffect(() => {
    let cancelled = false;

    async function loadCustomers() {
      try {
        setCustomersLoading(true);
        setCustomersError("");
        const data = await listCustomers();
        if (!cancelled) setCustomers(Array.isArray(data) ? data : []);
      } catch (e) {
        console.error(e);
        if (!cancelled) setCustomersError("Failed to load customers.");
      } finally {
        if (!cancelled) setCustomersLoading(false);
      }
    }

    if (open) loadCustomers();

    return () => {
      cancelled = true;
    };
  }, [open]);

  function resetForm() {
    setForm({
      customerId: "",
      amount: "",
      dueDate: "",
      description: "",
      // direction: "IN",
    });
    setCreateError("");
  }

  function handleClose() {
    onClose?.();
    resetForm();
  }

  async function onSubmit(e) {
    e.preventDefault();
    setCreateError("");

    if (!form.customerId) return setCreateError("Select a customer.");
    const amountNum = Number(form.amount);
    if (!Number.isFinite(amountNum) || amountNum <= 0)
      return setCreateError("Amount must be greater than 0.");
    if (!form.dueDate) return setCreateError("Due date is required.");

    const payload = {
      customerId: Number(form.customerId),
      amount: amountNum,
      dueDate: form.dueDate,
      description: form.description?.trim() || null,
      // direction: form.direction,
    };

    try {
      setCreating(true);
      await createPayment(payload);
      handleClose();
      await onCreated?.();
    } catch (e2) {
      console.error(e2);
      setCreateError(
        e2?.response?.data?.message ||
          e2?.response?.data?.error ||
          "Failed to create payment.",
      );
    } finally {
      setCreating(false);
    }
  }

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="absolute inset-0 bg-black/30" onClick={handleClose} />

      <div className="relative w-full max-w-lg bg-white rounded border shadow">
        <div className="p-4 border-b flex items-center justify-between">
          <div className="font-semibold">Create payment</div>
          <button
            onClick={handleClose}
            className="text-sm px-2 py-1 rounded border hover:bg-gray-50"
          >
            Close
          </button>
        </div>

        <form onSubmit={onSubmit} className="p-4 space-y-4">
          <div className="space-y-1">
            <label className="text-sm text-gray-700">Customer</label>
            <select
              value={form.customerId}
              onChange={(e) =>
                setForm((f) => ({ ...f, customerId: e.target.value }))
              }
              className="w-full border rounded px-3 py-2 text-sm"
              disabled={customersLoading}
            >
              <option value="">
                {customersLoading ? "Loading..." : "Select a customer"}
              </option>
              {customers.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name ?? c.fullName ?? c.companyName ?? `Customer #${c.id}`}
                </option>
              ))}
            </select>
            {customersError ? (
              <div className="text-xs text-red-600">{customersError}</div>
            ) : null}
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div className="space-y-1">
              <label className="text-sm text-gray-700">Amount</label>
              <input
                type="number"
                step="0.01"
                value={form.amount}
                onChange={(e) =>
                  setForm((f) => ({ ...f, amount: e.target.value }))
                }
                className="w-full border rounded px-3 py-2 text-sm"
                placeholder="e.g. 120.50"
              />
            </div>

            <div className="space-y-1">
              <label className="text-sm text-gray-700">Due date</label>
              <input
                type="date"
                value={form.dueDate}
                onChange={(e) =>
                  setForm((f) => ({ ...f, dueDate: e.target.value }))
                }
                className="w-full border rounded px-3 py-2 text-sm"
              />
            </div>
          </div>

          <div className="space-y-1">
            <label className="text-sm text-gray-700">Description</label>
            <input
              type="text"
              value={form.description}
              onChange={(e) =>
                setForm((f) => ({ ...f, description: e.target.value }))
              }
              className="w-full border rounded px-3 py-2 text-sm"
              placeholder="Optional"
            />
          </div>

          {createError ? (
            <div className="text-sm text-red-600">{createError}</div>
          ) : null}

          <div className="flex items-center justify-end gap-2 pt-2">
            <button
              type="button"
              onClick={handleClose}
              className="px-4 py-2 rounded border text-sm hover:bg-gray-50"
              disabled={creating}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 rounded bg-black text-white text-sm hover:opacity-90 disabled:opacity-60"
              disabled={creating}
            >
              {creating ? "Creating..." : "Create"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
