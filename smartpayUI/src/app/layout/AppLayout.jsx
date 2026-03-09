import { NavLink, Outlet } from "react-router-dom";
import Topbar from "./Topbar";

export default function AppLayout() {
  const linkClass = "block px-4 py-2 rounded hover:bg-gray-100 text-sm";
  const activeClass = "bg-gray-200 font-medium";

  return (
    <div className="min-h-screen flex bg-gray-100">
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

      <div className="flex-1 flex flex-col">
        <Topbar />
        <main className="flex-1 p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
