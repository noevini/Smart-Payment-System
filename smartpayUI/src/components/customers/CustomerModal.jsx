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
        await updateCustomer(customer.id, payload); // PATCH
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
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="absolute inset-0 bg-black/30" onClick={handleClose} />

      <div className="relative w-full max-w-lg bg-white rounded border shadow">
        <div className="p-4 border-b flex items-center justify-between">
          <div className="font-semibold">
            {isEdit ? "Edit customer" : "Create customer"}
          </div>

          <button
            onClick={handleClose}
            className="text-sm px-2 py-1 rounded border hover:bg-gray-50"
          >
            Close
          </button>
        </div>

        <form onSubmit={onSubmit} className="p-4 space-y-4">
          <div className="space-y-1">
            <label className="text-sm text-gray-700">Name</label>
            <input
              type="text"
              value={form.name}
              onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
              className="w-full border rounded px-3 py-2 text-sm"
              placeholder="Customer name"
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div className="space-y-1">
              <label className="text-sm text-gray-700">Email</label>
              <input
                type="email"
                value={form.email}
                onChange={(e) =>
                  setForm((f) => ({ ...f, email: e.target.value }))
                }
                className="w-full border rounded px-3 py-2 text-sm"
                placeholder="Optional"
              />
            </div>

            <div className="space-y-1">
              <label className="text-sm text-gray-700">Phone</label>
              <input
                type="text"
                value={form.phone}
                onChange={(e) =>
                  setForm((f) => ({ ...f, phone: e.target.value }))
                }
                className="w-full border rounded px-3 py-2 text-sm"
                placeholder="Optional"
              />
            </div>
          </div>

          {error ? <div className="text-sm text-red-600">{error}</div> : null}

          <div className="flex items-center justify-end gap-2 pt-2">
            <button
              type="button"
              onClick={handleClose}
              className="px-4 py-2 rounded border text-sm hover:bg-gray-50"
              disabled={saving}
            >
              Cancel
            </button>

            <button
              type="submit"
              className="px-4 py-2 rounded bg-black text-white text-sm hover:opacity-90 disabled:opacity-60"
              disabled={saving}
            >
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
