export default function RecentPaymentsTable({ rows, onViewAll }) {
  return (
    <div className="bg-white border rounded">
      <div className="p-4 border-b font-semibold flex items-center justify-between">
        <span>Recent payments</span>
        <button
          onClick={onViewAll}
          className="text-sm px-3 py-1.5 rounded border hover:bg-gray-50"
        >
          View all
        </button>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="text-left text-gray-500">
            <tr>
              <th className="p-4">ID</th>
              <th className="p-4">Amount</th>
              <th className="p-4">Status</th>
              <th className="p-4">Due date</th>
            </tr>
          </thead>

          <tbody>
            {rows.map((p) => (
              <tr key={p.id} className="border-t">
                <td className="p-4 font-mono">{p.id}</td>
                <td className="p-4">
                  {p.currency} {p.amount}
                </td>
                <td className="p-4">{p.status}</td>
                <td className="p-4">
                  {p.dueDate ? String(p.dueDate).slice(0, 10) : "—"}
                </td>
              </tr>
            ))}

            {rows.length === 0 ? (
              <tr className="border-t">
                <td className="p-4 text-gray-500" colSpan={4}>
                  No payments yet.
                </td>
              </tr>
            ) : null}
          </tbody>
        </table>
      </div>
    </div>
  );
}
