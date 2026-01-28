export default function Sidebar() {
  return (
    <aside className="w-64 bg-white border-r p-4">
      <h1 className="text-lg font-bold">SmartPay</h1>
      <p className="text-sm text-gray-500 mb-4">IPD demo</p>

      <nav className="space-y-1">
        <div className="px-3 py-2 rounded bg-gray-200 text-sm font-medium">
          Dashboard
        </div>
        <div className="px-3 py-2 rounded hover:bg-gray-100 text-sm">
          Payments
        </div>
        <div className="px-3 py-2 rounded hover:bg-gray-100 text-sm">
          Customers
        </div>
      </nav>
    </aside>
  );
}
