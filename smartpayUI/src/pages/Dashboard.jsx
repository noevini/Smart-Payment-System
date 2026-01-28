import Card from "../components/Card";
import Badge from "../components/Badge";
import { payments } from "../data/mock";

function formatMoney(amount, currency) {
  try {
    return new Intl.NumberFormat("en-GB", {
      style: "currency",
      currency,
    }).format(amount);
  } catch {
    return `${currency} ${amount}`;
  }
}

export default function Dashboard() {
  const totalReceived = payments
    .filter((p) => p.status === "PAID")
    .reduce((sum, p) => sum + p.amount, 0);

  const pending = payments.filter((p) => p.status === "PENDING").length;
  const overdue = payments.filter((p) => p.status === "OVERDUE").length;

  const recent = [...payments].slice(0, 4);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Dashboard</h1>
        <p className="text-gray-600">Overview (mock data for IPD demo).</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card
          title="Total received"
          value={formatMoney(totalReceived, "GBP")}
          subtitle="Paid payments"
        />
        <Card
          title="Pending payments"
          value={pending}
          subtitle="Awaiting payment"
        />
        <Card
          title="Overdue payments"
          value={overdue}
          subtitle="Need follow-up"
        />
        <Card title="Customers" value={4} subtitle="Mock count" />
      </div>

      <div className="bg-white border rounded">
        <div className="p-4 border-b">
          <h2 className="font-semibold">Recent payments</h2>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="text-left text-gray-500">
              <tr>
                <th className="p-4">ID</th>
                <th className="p-4">Customer</th>
                <th className="p-4">Amount</th>
                <th className="p-4">Status</th>
                <th className="p-4">Due date</th>
                <th className="p-4">Method</th>
              </tr>
            </thead>
            <tbody>
              {recent.map((p) => (
                <tr key={p.id} className="border-t">
                  <td className="p-4 font-mono">{p.id}</td>
                  <td className="p-4">{p.customer}</td>
                  <td className="p-4">{formatMoney(p.amount, p.currency)}</td>
                  <td className="p-4">
                    <Badge status={p.status} />
                  </td>
                  <td className="p-4">{p.dueDate}</td>
                  <td className="p-4">{p.method}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
