import { useState } from "react";
import { deleteCustomer } from "../api/customerApi";
import { useCustomers } from "../hooks/useCustomers";
import CustomerModal from "../components/customers/CustomerModal";

/**
 * Customers page — lists and manages customers for the selected business.
 *
 * Uses useCustomers hook for data fetching and business sync.
 * Supports create, edit, and delete actions via CustomerModal.
 */
export default function Customers() {
  const { customers, loading, error, businessId, reload } = useCustomers();

  // Modal state — null means create, object means edit
  const [open, setOpen] = useState(false);
  const [editingCustomer, setEditingCustomer] = useState(null);

  /**
   * Handles customer deletion with confirmation.
   * @param {number} id - customer ID to delete
   */
  async function handleDelete(id) {
    const ok = window.confirm("Are you sure you want to delete this customer?");
    if (!ok) return;

    try {
      await deleteCustomer(id);
      await reload();
    } catch (e) {
      console.error(e);
      alert("Failed to delete customer.");
    }
  }

  return (
    <div className="page-shell">
      <div className="flex items-start justify-between gap-4">
        <div>
          <h1 className="page-title">Customers</h1>
          <p className="page-subtitle">
            Manage customers for your selected business.
          </p>
        </div>

        <button
          onClick={() => {
            if (!businessId) {
              alert("Select a business first.");
              return;
            }
            setEditingCustomer(null);
            setOpen(true);
          }}
          className="btn-primary"
          disabled={!businessId}
        >
          New customer
        </button>
      </div>

      {/* No business selected */}
      {!businessId ? (
        <div className="text-sm text-slate-500">
          Select a business to view customers.
        </div>
      ) : null}

      {/* Loading / error states */}
      {loading ? (
        <div className="text-sm text-slate-500">Loading customers...</div>
      ) : error ? (
        <div className="text-sm text-red-600">{error}</div>
      ) : null}

      {/* Customers table */}
      <div className="card-surface">
        <div className="p-4 border-b border-slate-200 font-semibold">
          Customer list
        </div>

        <div className="table-shell">
          <table className="table-base">
            <thead className="table-head">
              <tr>
                <th className="table-th">ID</th>
                <th className="table-th">Name</th>
                <th className="table-th">Email</th>
                <th className="table-th">Phone</th>
                <th className="table-th">Actions</th>
              </tr>
            </thead>

            <tbody>
              {customers.map((c) => (
                <tr key={c.id} className="table-row">
                  <td className="table-td font-mono">{c.id}</td>
                  <td className="table-td">{c.name ?? "—"}</td>
                  <td className="table-td">{c.email ?? "—"}</td>
                  <td className="table-td">{c.phone ?? "—"}</td>
                  <td className="table-td">
                    <div className="flex flex-wrap gap-2">
                      <button
                        onClick={() => {
                          setEditingCustomer(c);
                          setOpen(true);
                        }}
                        className="btn-secondary"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => handleDelete(c.id)}
                        className="btn-danger"
                      >
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}

              {!loading && customers.length === 0 ? (
                <tr className="table-row">
                  <td className="table-td text-slate-500" colSpan={5}>
                    No customers found.
                  </td>
                </tr>
              ) : null}
            </tbody>
          </table>
        </div>
      </div>

      {/* Create / edit modal */}
      <CustomerModal
        open={open}
        customer={editingCustomer}
        onClose={() => {
          setOpen(false);
          setEditingCustomer(null);
        }}
        onSaved={reload}
      />
    </div>
  );
}
