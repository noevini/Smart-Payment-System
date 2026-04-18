import { useState } from "react";
import { useStaff } from "../hooks/useStaff";
import { deleteStaff } from "../api/staffApi";
import CreateStaffModal from "../components/staff/CreateStaffModal";

export default function Staff() {
  const { staff, loading, error, businessId, reload } = useStaff();
  const [open, setOpen] = useState(false);

  async function handleDelete(userId, name) {
    if (!window.confirm(`Remove ${name} from this business?`)) return;
    try {
      await deleteStaff(userId);
      await reload();
    } catch (e) {
      console.error(e);
      alert("Failed to remove staff member.");
    }
  }

  return (
    <div className="page-shell">
      <div className="flex items-start justify-between gap-4">
        <div>
          <h1 className="page-title">Staff</h1>
          <p className="page-subtitle">
            Manage staff members for your selected business.
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
          New staff member
        </button>
      </div>

      {!businessId ? (
        <div className="text-sm text-slate-500">
          Select a business to view staff.
        </div>
      ) : null}

      {loading ? (
        <div className="text-sm text-slate-500">Loading staff...</div>
      ) : error ? (
        <div className="text-sm text-red-600">{error}</div>
      ) : null}

      <div className="card-surface">
        <div className="p-4 border-b border-slate-200 font-semibold">
          Staff members
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
              {staff.map((s) => (
                <tr key={s.id} className="table-row">
                  <td className="table-td font-mono">{s.id}</td>
                  <td className="table-td font-medium">{s.name}</td>
                  <td className="table-td text-slate-500">{s.email}</td>
                  <td className="table-td text-slate-500">{s.phone ?? "—"}</td>
                  <td className="table-td">
                    <button
                      onClick={() => handleDelete(s.id, s.name)}
                      className="btn-danger"
                    >
                      Remove
                    </button>
                  </td>
                </tr>
              ))}

              {!loading && staff.length === 0 ? (
                <tr className="table-row">
                  <td className="table-td text-slate-500" colSpan={5}>
                    No staff members found.
                  </td>
                </tr>
              ) : null}
            </tbody>
          </table>
        </div>
      </div>

      <CreateStaffModal
        open={open}
        onClose={() => setOpen(false)}
        onCreated={reload}
      />
    </div>
  );
}
