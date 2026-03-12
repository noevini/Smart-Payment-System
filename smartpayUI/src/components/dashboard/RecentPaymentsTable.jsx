export default function RecentPaymentsTable({ rows }) {
  return (
    <div className="card-surface">
      <div className="p-4 border-b border-slate-200 font-semibold">
        Recent payments
      </div>

      <div className="table-shell">
        <table className="table-base">
          <thead className="table-head">
            <tr>
              <th className="table-th">ID</th>
              <th className="table-th">Amount</th>
              <th className="table-th">Status</th>
              <th className="table-th">Due date</th>
            </tr>
          </thead>

          <tbody>
            {rows.map((p) => (
              <tr key={p.id} className="table-row">
                <td className="table-td font-mono">{p.id}</td>
                <td className="table-td">
                  {p.currency} {p.amount}
                </td>
                <td className="table-td">{p.status}</td>
                <td className="table-td">
                  {p.dueDate ? String(p.dueDate).slice(0, 10) : "—"}
                </td>
              </tr>
            ))}

            {rows.length === 0 ? (
              <tr className="table-row">
                <td colSpan={4} className="table-td text-slate-500">
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
