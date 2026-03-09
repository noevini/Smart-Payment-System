export default function OverduePaymentsTable({ rows }) {
  return (
    <div className="bg-white border rounded">
      <div className="p-4 border-b font-semibold">Overdue payments</div>

      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="text-left text-gray-500">
            <tr>
              <th className="p-4">ID</th>
              <th className="p-4">Amount</th>
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

                <td className="p-4">{String(p.dueDate).slice(0, 10)}</td>
              </tr>
            ))}

            {rows.length === 0 && (
              <tr className="border-t">
                <td colSpan={3} className="p-4 text-gray-500">
                  No overdue payments.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
