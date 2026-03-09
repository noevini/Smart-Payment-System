import { NavLink, Outlet } from "react-router-dom";

export default function AppLayout() {
  const linkClass = "block px-4 py-2 rounded hover:bg-gray-100 text-sm";

  const activeClass = "bg-gray-200 font-medium";

  return (
    <div className="min-h-screen flex bg-gray-100">
      {/* Sidebar */}
      <aside className="w-56 bg-white border-r p-4 space-y-2">
        <div className="text-lg font-bold mb-6">SmartPay</div>

        <NavLink
          to="/dashboard"
          className={({ isActive }) =>
            `${linkClass} ${isActive ? activeClass : ""}`
          }
        >
          Dashboard
        </NavLink>

        <NavLink
          to="/payments"
          className={({ isActive }) =>
            `${linkClass} ${isActive ? activeClass : ""}`
          }
        >
          Payments
        </NavLink>

        <NavLink
          to="/customers"
          className={({ isActive }) =>
            `${linkClass} ${isActive ? activeClass : ""}`
          }
        >
          Customers
        </NavLink>

        <NavLink
          to="/managers"
          className={({ isActive }) =>
            `${linkClass} ${isActive ? activeClass : ""}`
          }
        >
          Managers
        </NavLink>

        <NavLink
          to="/reports"
          className={({ isActive }) =>
            `${linkClass} ${isActive ? activeClass : ""}`
          }
        >
          Reports
        </NavLink>

        <NavLink
          to="/notifications"
          className={({ isActive }) =>
            `${linkClass} ${isActive ? activeClass : ""}`
          }
        >
          Notifications
        </NavLink>
      </aside>

      {/* Page content */}
      <main className="flex-1 p-6">
        <Outlet />
      </main>
    </div>
  );
}
