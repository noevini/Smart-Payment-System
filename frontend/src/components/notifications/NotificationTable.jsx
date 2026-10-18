import NotificationBadge from "./NotificationBadge";

export default function NotificationTable({ rows, onMarkRead }) {
  return (
    <div className="card-surface">
      <div className="p-4 border-b border-slate-200 font-semibold">
        Notifications
      </div>

      <div className="table-shell">
        <table className="table-base">
          <thead className="table-head">
            <tr>
              <th className="table-th">ID</th>
              <th className="table-th">Type</th>
              <th className="table-th">Title</th>
              <th className="table-th">Message</th>
              <th className="table-th">Status</th>
              <th className="table-th">Created</th>
              <th className="table-th">Actions</th>
            </tr>
          </thead>

          <tbody>
            {rows.map((n) => (
              <tr key={n.id} className="table-row">
                <td className="table-td font-mono">{n.id}</td>

                <td className="table-td">{String(n.type ?? "—")}</td>

                <td className="table-td">{n.title ?? "—"}</td>

                <td className="table-td">{n.message ?? "—"}</td>

                <td className="table-td">
                  <NotificationBadge read={n.isRead} />
                </td>

                <td className="table-td">
                  {n.createdAt ? String(n.createdAt).slice(0, 10) : "—"}
                </td>

                <td className="table-td">
                  {!n.isRead ? (
                    <button
                      onClick={() => onMarkRead(n.id)}
                      className="btn-secondary"
                    >
                      Mark as read
                    </button>
                  ) : (
                    <span className="text-sm text-slate-400">—</span>
                  )}
                </td>
              </tr>
            ))}

            {rows.length === 0 ? (
              <tr className="table-row">
                <td colSpan={7} className="table-td text-slate-500">
                  No notifications found.
                </td>
              </tr>
            ) : null}
          </tbody>
        </table>
      </div>
    </div>
  );
}
