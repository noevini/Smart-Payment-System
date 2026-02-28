import { useEffect, useState } from "react";
import { listCustomers, deleteCustomer } from "../app/api/customersApi";
import { getSelectedBusinessId } from "../app/business/businessStorage";

export default function Customers() {
  const [businessId, setBusinessId] = useState(getSelectedBusinessId());

  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function loadCustomers() {
    try {
      setLoading(true);
      setError("");
      const data = await listCustomers();
      setRows(data);
    } catch (e) {
      console.error(e);
      setError("Failed to load customers.");
    } finally {
      setLoading(false);
    }
  }

  // Sync business from localStorage
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

  // Load customers when business changes
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
    <div className="space-y-6">
      <div className="flex items-start justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold">Customers</h1>
          <p className="text-gray-600">
            Manage customers (connected to backend).
          </p>
        </div>

        <button
          onClick={() => {
            if (!businessId) {
              alert("Select a business first.");
              return;
            }
            alert("Next step: open Customer modal (create).");
          }}
          className="px-4 py-2 rounded bg-black text-white text-sm hover:opacity-90"
          disabled={!businessId}
        >
          New customer
        </button>
      </div>

      {!businessId ? (
        <div className="text-sm text-gray-600">
          Select a business to view customers.
        </div>
      ) : null}

      {loading ? (
        <div className="text-sm text-gray-600">Loading customers...</div>
      ) : error ? (
        <div className="text-sm text-red-600">{error}</div>
      ) : null}

      <div className="bg-white border rounded">
        <div className="p-4 border-b font-semibold">Customer list</div>

        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="text-left text-gray-500">
              <tr>
                <th className="p-4">ID</th>
                <th className="p-4">Name</th>
                <th className="p-4">Email</th>
                <th className="p-4">Phone</th>
                <th className="p-4">Actions</th>
              </tr>
            </thead>

            <tbody>
              {rows.map((c) => (
                <tr key={c.id} className="border-t">
                  <td className="p-4 font-mono">{c.id}</td>
                  <td className="p-4">{c.name ?? "—"}</td>
                  <td className="p-4">{c.email ?? "—"}</td>
                  <td className="p-4">{c.phone ?? "—"}</td>
                  <td className="p-4 flex gap-2">
                    <button
                      onClick={() => alert("Next: implement Edit modal")}
                      className="px-3 py-1.5 rounded border text-xs hover:bg-gray-50"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => handleDelete(c.id)}
                      className="px-3 py-1.5 rounded border text-xs text-red-600 hover:bg-red-50"
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}

              {!loading && rows.length === 0 ? (
                <tr className="border-t">
                  <td className="p-4 text-gray-500" colSpan={5}>
                    No customers found.
                  </td>
                </tr>
              ) : null}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
