import { useEffect, useState } from "react";
import { createCustomer, updateCustomer } from "../../app/api/customersApi";

export default function CustomerModal({ open, onClose, onSaved, customer }) {
  const isEdit = !!customer;

  const [form, setForm] = useState({
    name: "",
    email: "",
    phone: "",
  });

  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!open) return;

    setError("");

    if (isEdit && customer) {
      setForm({
        name: customer.name ?? "",
        email: customer.email ?? "",
        phone: customer.phone ?? "",
      });
    } else {
      setForm({
        name: "",
        email: "",
        phone: "",
      });
    }
  }, [open, isEdit, customer]);

  function handleClose() {
    onClose?.();
    setError("");
  }

  async function onSubmit(e) {
    e.preventDefault();
    setError("");

    const name = form.name.trim();
    if (!name) return setError("Name is required.");

    const payload = {
      name,
      email: form.email?.trim() || null,
      phone: form.phone?.trim() || null,
    };

    try {
      setSaving(true);

      if (isEdit) {
        await updateCustomer(customer.id, payload);
      } else {
        await createCustomer(payload);
      }

      handleClose();
      await onSaved?.();
    } catch (e2) {
      console.error(e2);
      setError(
        e2?.response?.data?.message ||
          e2?.response?.data?.error ||
          (isEdit
            ? "Failed to update customer."
            : "Failed to create customer."),
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
              {isEdit ? "Edit customer" : "Create customer"}
            </h2>
            <p className="text-sm text-slate-500 mt-1">
              {isEdit
                ? "Update customer details for this record."
                : "Add a new customer to your selected business."}
            </p>
          </div>

          <button onClick={handleClose} className="btn-secondary">
            Close
          </button>
        </div>

        <form onSubmit={onSubmit} className="p-6 space-y-5">
          <div className="space-y-2">
            <label className="text-sm font-medium text-slate-700">Name</label>
            <input
              type="text"
              value={form.name}
              onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
              className="input-field"
              placeholder="Customer name"
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div className="space-y-2">
              <label className="text-sm font-medium text-slate-700">
                Email
              </label>
              <input
                type="email"
                value={form.email}
                onChange={(e) =>
                  setForm((f) => ({ ...f, email: e.target.value }))
                }
                className="input-field"
                placeholder="Optional"
              />
            </div>

            <div className="space-y-2">
              <label className="text-sm font-medium text-slate-700">
                Phone
              </label>
              <input
                type="text"
                value={form.phone}
                onChange={(e) =>
                  setForm((f) => ({ ...f, phone: e.target.value }))
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
