import NotificationBadge from "./NotificationBadge";

export default function NotificationTable({ rows, onMarkRead }) {
  return (
    <div className="bg-white border rounded">
      <div className="p-4 border-b font-semibold">Notifications</div>

      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="text-left text-gray-500">
            <tr>
              <th className="p-4">ID</th>
              <th className="p-4">Type</th>
              <th className="p-4">Title</th>
              <th className="p-4">Message</th>
              <th className="p-4">Status</th>
              <th className="p-4">Created</th>
              <th className="p-4">Actions</th>
            </tr>
          </thead>

          <tbody>
            {rows.map((n) => (
              <tr key={n.id} className="border-t">
                <td className="p-4 font-mono">{n.id}</td>

                <td className="p-4">{String(n.type)}</td>

                <td className="p-4">{n.title}</td>

                <td className="p-4">{n.message}</td>

                <td className="p-4">
                  <NotificationBadge read={n.isRead} />
                </td>

                <td className="p-4">{String(n.createdAt).slice(0, 10)}</td>

                <td className="p-4">
                  {!n.isRead && (
                    <button
                      onClick={() => onMarkRead(n.id)}
                      className="px-3 py-2 border rounded text-sm hover:bg-gray-50"
                    >
                      Mark as read
                    </button>
                  )}
                </td>
              </tr>
            ))}

            {rows.length === 0 && (
              <tr className="border-t">
                <td colSpan={7} className="p-4 text-gray-500">
                  No notifications found.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
