import { useEffect, useState } from "react";
import { listCustomers, deleteCustomer } from "../api/customerApi";
import { getSelectedBusinessId } from "../state/businessStorage";
import CustomerModal from "../components/customers/CustomerModal";

export default function Customers() {
  const [businessId, setBusinessId] = useState(getSelectedBusinessId());

  const [open, setOpen] = useState(false);
  const [editingCustomer, setEditingCustomer] = useState(null);

  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function loadCustomers() {
    try {
      setLoading(true);
      setError("");
      const data = await listCustomers();
      setRows(Array.isArray(data) ? data : (data?.content ?? []));
    } catch (e) {
      console.error(e);
      setError("Failed to load customers.");
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
    loadCustomers();
  }, [businessId]);

  async function handleDelete(id) {
    const ok = window.confirm("Are you sure you want to delete this customer?");
    if (!ok) return;

    try {
      await deleteCustomer(id);
      await loadCustomers();
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

      {!businessId ? (
        <div className="text-sm text-slate-500">
          Select a business to view customers.
        </div>
      ) : null}

      {loading ? (
        <div className="text-sm text-slate-500">Loading customers...</div>
      ) : error ? (
        <div className="text-sm text-red-600">{error}</div>
      ) : null}

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
              {rows.map((c) => (
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

              {!loading && rows.length === 0 ? (
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

      <CustomerModal
        open={open}
        customer={editingCustomer}
        onClose={() => {
          setOpen(false);
          setEditingCustomer(null);
        }}
        onSaved={loadCustomers}
      />
    </div>
  );
}
