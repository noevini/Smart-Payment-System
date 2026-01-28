import { useState } from "react";
import { customers as initialCustomers } from "../data/mock";

export default function Customers() {
  const [rows, setRows] = useState(initialCustomers);

  function addCustomerPlaceholder() {
    alert("Planned: Add customer form (IPD demo placeholder)");
  }

  function editPlaceholder(id) {
    alert(`Planned: Edit customer ${id} (IPD demo placeholder)`);
  }

  function deleteCustomer(id) {
    const ok = confirm(`Delete customer ${id}? (mock)`);
    if (!ok) return;
    setRows((prev) => prev.filter((c) => c.id !== id));
  }

  return (
    <div className="space-y-6">
      <div className="flex items-start justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold">Customers</h1>
          <p className="text-gray-600">
            Manage customers (mock data for IPD demo).
          </p>
        </div>

        <button
          onClick={addCustomerPlaceholder}
          className="px-4 py-2 rounded bg-blue-600 text-white text-sm font-medium"
        >
          + Add customer
        </button>
      </div>

      <div className="bg-white border rounded">
        <div className="p-4 border-b font-semibold">Customer list</div>

        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="text-left text-gray-500">
              <tr>
                <th className="p-4">Customer ID</th>
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
                  <td className="p-4">{c.name}</td>
                  <td className="p-4">{c.email}</td>
                  <td className="p-4">{c.phone}</td>
                  <td className="p-4 flex gap-2">
                    <button
                      onClick={() => editPlaceholder(c.id)}
                      className="px-3 py-1.5 rounded border text-xs hover:bg-gray-50"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => deleteCustomer(c.id)}
                      className="px-3 py-1.5 rounded border text-xs hover:bg-gray-50"
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}

              {rows.length === 0 ? (
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
